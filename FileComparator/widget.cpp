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
    connect(worker, SIGNAL(toOutPutIdentical(QString)), this, SLOT(setOutPutTextIden(QString)));
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
    if (!checkFiles()) {
        return;
    }

    ui->textEdit->clear();
    ui->textEdit_2->clear();
    ui->pushButton_3->setEnabled(false);
    ui->pushButton_4->setEnabled(false);
    ui->checkBox->setEnabled(false);
    ui->progressBar->setValue(0);
    ui->label->setText("");
    ui->label_4->setText("");

    // Init
    worker->firstFile = ui->plainTextEdit->toPlainText();
    worker->secondFile = ui->plainTextEdit_2->toPlainText();

    // Start
    worker->registr = ui->checkBox->isChecked();
    worker->start(QThread::IdlePriority);
}

void Widget::setOutPutText(const QString &text)
{
    ui->textEdit->append(text);
}

void Widget::setOutPutTextIden(const QString &text)
{
    ui->textEdit_2->append(text);
}

void Widget::nowDone(int a, int b)
{
    ui->label_4->setText(QString("%1 unique strings, %2 identical strings").arg(a).arg(b));
    ui->label->setText(tr("Done!"));
    ui->progressBar->setValue(100);
    ui->pushButton_3->setEnabled(true);
    ui->pushButton_4->setEnabled(true);
    ui->checkBox->setEnabled(true);
}

void Widget::progress(int percent)
{
    ui->progressBar->setValue(percent);
}

void Widget::on_pushButton_4_clicked()
{
    if (!checkFiles()) {
        return;
    }

    QString name1 = ui->label_2->text();
    QString name2 = ui->label_3->text();

    QString te1 = ui->plainTextEdit->toPlainText();
    QString te2 = ui->plainTextEdit_2->toPlainText();

    ui->label_2->setText(name2);
    ui->label_3->setText(name1);
    ui->plainTextEdit->clear();
    ui->plainTextEdit_2->clear();
    ui->plainTextEdit->appendPlainText(te2);
    ui->plainTextEdit_2->appendPlainText(te1);
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
}

bool Widget::checkFiles()
{
    if (ui->plainTextEdit->toPlainText().isEmpty() ||
           ui->plainTextEdit_2->toPlainText().isEmpty()) {
        QMessageBox::critical(this, tr("Error!"), tr("Please select files!"));
        return false;
    }
    return true;
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
            ui->plainTextEdit->appendPlainText(textStream.readAll());
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
            bool bR;
            if (registr) {
                bR = lines1.at(i).toUpper() == lines2.at(j).toUpper();
            } else {
                bR = lines1.at(i) == lines2.at(j);
            }

            if (bR) {
                continue;
            } else {
                inner_cnt++;
            }
        }

        if (inner_cnt == M) {
            emit toOutPut(lines1.at(i));
            u_cnt++;
        } else {
            emit toOutPutIdentical(lines1.at(i));
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
