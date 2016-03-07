#include "widget.h"
#include "ui_widget.h"

#include <QFileDialog>
#include <QFile>
#include <QDebug>
#include <QMessageBox>

Widget::Widget(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::Widget)
{
    ui->setupUi(this);

    worker = new Worker();

    connect(worker, SIGNAL(toOutPut(QString)), this, SLOT(setOutPutText(QString)));
    connect(worker, SIGNAL(done(int,int)), this, SLOT(nowDone(int,int)));
    connect(worker, SIGNAL(toProgressBar(int)), this, SLOT(progress(int)));
}

Widget::~Widget()
{
    delete ui;
    delete worker;
}

void Widget::on_pushButton_3_clicked()
{
    if (ui->plainTextEdit->toPlainText().isEmpty() ||
           ui->plainTextEdit_2->toPlainText().isEmpty()) {
        QMessageBox::critical(this, tr("Error!"), tr("Please select files!"));
        return;
    }

    ui->textEdit->clear();
    ui->pushButton_3->setEnabled(false);
    ui->progressBar->setValue(0);
    ui->label->setText("");
    ui->label_4->setText("");

    // Init
    worker->firstFile = ui->plainTextEdit->toPlainText();
    worker->secondFile = ui->plainTextEdit_2->toPlainText();

    // Start
    worker->start(QThread::IdlePriority);
}

void Widget::setOutPutText(const QString &text)
{
    ui->textEdit->append(text);
}

void Widget::nowDone(int a, int b)
{
    ui->label_4->setText(QString("%1 unique strings, %2 identical strings").arg(a).arg(b));
    ui->label->setText(tr("Done!"));
    ui->progressBar->setValue(100);
    ui->pushButton_3->setEnabled(true);
}

void Widget::progress(int percent)
{
    ui->progressBar->setValue(percent);
}

void Widget::on_pushButton_2_clicked()
{
    QString fileName = getFileName();
    if (!fileName.isEmpty()) {
        ui->plainTextEdit_2->clear();
        openTextFile(fileName, false);
    }
}

void Widget::on_pushButton_clicked()
{
    QString fileName = getFileName();
    if (!fileName.isEmpty()) {
        ui->plainTextEdit->clear();
        openTextFile(fileName, true);
    }
}

QString Widget::getFileName()
{

    return QFileDialog::getOpenFileName(this,
                                        tr("Open Text File"));
//    QStringList selectedFiles;
//    QFileDialog fileDialog;
//    fileDialog.setFileMode(QFileDialog::AnyFile);
//    if (fileDialog.exec()) {
//        selectedFiles = fileDialog.selectedFiles();
//    }
//    return (selectedFiles.size() == 1) ? selectedFiles.at(0) : NULL;
}

void Widget::openTextFile(const QString fileName, bool qFirst)
{
    if (fileName != NULL) {
        QFile file;
        QTextStream textStream;
        file.setFileName(fileName);
        file.open(QIODevice::ReadOnly | QIODevice::Text);
        textStream.setDevice(&file);

        QFileInfo fileInfo(file);

        if (qFirst) {
            ui->label_2->setText(fileInfo.fileName());

            //while (!(textStream.atEnd())) {
            ui->plainTextEdit->appendPlainText(textStream.readAll());
            //}
            if (ui->plainTextEdit_2->toPlainText().isEmpty()) {
                ui->label->setText(tr("2: Please Select Second Text File"));
            }
        } else {
            ui->label_3->setText(fileInfo.fileName());
            ui->plainTextEdit_2->appendPlainText(textStream.readAll());
            if (ui->plainTextEdit->toPlainText().isEmpty()) {
                ui->label->setText(tr("2: Please Select Second Text File"));
            }
        }

        if (!ui->plainTextEdit->toPlainText().isEmpty() &&
                !ui->plainTextEdit_2->toPlainText().isEmpty()) {
            ui->label->setText(tr("3: Get Uniq Strings"));
        }

        file.close();
    }
}

Worker::Worker()
{
    moveToThread(this);
}

Worker::~Worker()
{
    /* Empty Destructor */
}

void Worker::run()
{
    QStringList lines1;
    QStringList lines2;

    QTextStream in1(&firstFile);
    QTextStream in2(&secondFile);

    while(!in1.atEnd()) {
        lines1 << in1.readLine().trimmed();
    }

    emit toProgressBar(5);

    while(!in2.atEnd()) {
        lines2 << in2.readLine().trimmed();
    }

    emit toProgressBar(10);

    const int N = lines1.size();
    const int M = lines2.size();
    int i_cnt = 0;
    int u_cnt = 0;
    int inner_cnt = 0;
    int inner_cnt2 = 0;
    int barPerc = 10;
    float prop = N / 80;

    for (int i = 0; i < N; ++i) {
        for (int j = 0; j < M; ++j) {
            if (lines1.at(i) == lines2.at(j)) {
                continue;
            } else {
                inner_cnt++;
            }
        }

        if (inner_cnt == M) {
            emit toOutPut(lines1.at(i));
            u_cnt++;
        } else {
            i_cnt++;
        }

        inner_cnt2++;
        if (inner_cnt2 == prop) {
            barPerc++;
            emit toProgressBar(10 + barPerc);
            inner_cnt2 = 0;
        }

        inner_cnt = 0;
    }

    // Done
    emit done(u_cnt, i_cnt);
}
