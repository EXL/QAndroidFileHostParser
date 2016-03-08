#include "widget.h"
#include "ui_widget.h"

#include <QUrl>
#include <QDebug>
#include <QWebFrame>
#include <QRegExp>
#include <QMessageBox>
#include <QFileDialog>

#include <algorithm>

#define MULTI_LINE_STRING(a) #a

Widget::Widget(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::Widget)
{
    ui->setupUi(this);

    pageCount = 0;
    fileCNT = 0;
    pgtCNT = 1;
    stackSize = 0;
    directLink = false;

    textLog = new QFile(this);
    textLog->setFileName("SW-Log.txt");

    webPage = new WebPage(this);
    ui->webView->setPage(webPage);

    timer_1 = new QTimer(this);

    mainFrame = ui->webView->page()->mainFrame();

    ui->tableWidget->setRowCount(100);
    ui->tableWidget->setColumnCount(4);

    setWindowState(Qt::WindowMaximized);

    connect(mainFrame, SIGNAL(loadFinished(bool)), this, SLOT(webPageLoaded(bool)));
    connect(ui->pushButton, SIGNAL(clicked(bool)), this, SLOT(goToURL()));
    connect(ui->webView, SIGNAL(loadProgress(int)), ui->progressBar, SLOT(setValue(int)));
    connect(timer_1, SIGNAL(timeout()), this, SLOT(timerOff()));
}

Widget::~Widget()
{
    delete ui;
}

void Widget::webPageLoaded(bool)
{
    qDebug() << "Page Loaded!";

    if (!directLink) {
        pageCount = getPageCount();
        qDebug() << "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!: " << pageCount;
        ui->tableWidget->setRowCount(pageCount * 18);
    }

    ui->label_2->setText("Getting links...");

    if (pageCount && !directLink) {
        const char* theJavaScriptCodeToInject = MULTI_LINE_STRING(
                function checkForMailButton()
                {
                    var stackLinks = [];
                    var allSpans = document.getElementsByClassName("list-group-item-heading");
                    for (theSpan in allSpans)
                    {
                        var link = allSpans[theSpan].innerHTML;
                        if (link)
                        {
                            link = link.replace("<i class=\"fa fa-file-archive-o\"></i> ", "");
                            stackLinks.push(link);
                            console.log(link);
                        }
                    }
                    return stackLinks;
                }
                checkForMailButton();
            );
        QVariant jsReturn = mainFrame->evaluateJavaScript(theJavaScriptCodeToInject);

        fillTableLinks(jsReturn.toStringList());
    }

    if (directLink) {
        const char* clickJsCode = MULTI_LINE_STRING(
//                function sleeping(ms) {
//                    ms += new Date().getTime();
//                    while (new Date() < ms){}
//                }

                function clickJsCode()
                {
                    document.getElementById('loadMirror').click();
//                    console.log("Sleeep!");
//                    sleeping(10000);
//                    console.log(document.getElementById('mirrors'));
                }
                clickJsCode();
            );
        QVariant jsRet = mainFrame->evaluateJavaScript(clickJsCode);
        timer_1->start(ui->spinBox->value() * 1000);
    }
}

void Widget::goToURL()
{
    ui->label_2->setText("Loading page...");
    ui->webView->load(QUrl(ui->lineEdit->text()));
}

void Widget::fillTableLinks(const QStringList &stackLinks)
{
    stackSize = stackLinks.length();

    for (int i = 0; i < stackLinks.length(); ++i) {
        QString string = stackLinks[i];

        QString name = string;
        name.remove(QRegExp("<[^>]*>"));

        QString link = string;
        link.remove("<a href=\"");
        link = link.left(23);

        ui->tableWidget->setItem(i + (pgtCNT - 1) * stackSize, 0, new QTableWidgetItem(name));
        ui->tableWidget->setItem(i + (pgtCNT - 1) * stackSize, 1, new QTableWidgetItem("https://androidfilehost.com" + link));
    }

    directLink = true;

    ui->lineEdit->setText(ui->tableWidget->item(fileCNT + (pgtCNT - 1) * stackSize, 1)->text());
    goToURL();
}

int Widget::getPageCount() const
{
    ui->label_2->setText("Pages count is...");

    const char* theJavaScriptCodeToInject = MULTI_LINE_STRING(
                function checkForMailButton()
                {
                    var stackLinks = [];
                    var allSpans = document.getElementsByClassName("pagination navbar-right");
                    for (theSpan in allSpans)
                    {
                        var link = allSpans[theSpan].innerHTML;
                        if (link)
                        {
                            stackLinks.push(link);
                            console.log(link);
                        }
                    }
                    return stackLinks;
                }
                checkForMailButton();
            );

    QVariant jsReturn = mainFrame->evaluateJavaScript(theJavaScriptCodeToInject);

    QString s = jsReturn.toStringList()[0].replace(QRegExp("<[^>]*>"), "a");
    QString pageCnt = "";
    bool flag = false;
    for (int i = s.length(); i >= 0; --i) {
        if (s[i].isDigit()) {
            pageCnt += s[i];
            flag = true;
        } else {
            if (flag) {
                break;
            }
        }
    }

    std::reverse(pageCnt.begin(), pageCnt.end());

    int pgCnt = pageCnt.toInt();

    ui->label_2->setText(QString("Pages count is... %1.").arg(pgCnt));

    return pgCnt;
}

void Widget::tableDirectLinks(const QString &links)
{
    QString dlinks = links;
    QString buffer = "";
    dlinks = dlinks.trimmed();

    QStringList linkss = dlinks.split("<a href=\"");
    linkss.removeAt(0);
    for (int i = 0; i < linkss.length(); ++i) {
        int r = linkss[i].indexOf(".zip");
        buffer += linkss[i].left(r) + ".zip\n";
    }

    ui->tableWidget->setItem(fileCNT + (pgtCNT - 1) * stackSize, 2, new QTableWidgetItem(buffer));
    //qDebug() << linkss;
}

void Widget::md5totable(const QString &md5)
{
    //QString buffer = md5;
    //buffer.remove(QRegExp("<[^>]*>"));
    ui->tableWidget->setItem(fileCNT + (pgtCNT - 1) * stackSize, 3, new QTableWidgetItem(md5));
}

void Widget::timerOff()
{
    qDebug() << "LOLOLOLLOLLOLOL";
    const char* clickJsCode = MULTI_LINE_STRING(
                function checkForMailButton()
                {
                    var stackLinks = [];
                    var allSpans = document.getElementsByClassName("list-group");
                    for (theSpan in allSpans)
                    {
                        var link = allSpans[theSpan].innerHTML;
                        if (link)
                        {
                            stackLinks.push(link);
                            console.log(link);
                        }
                    }
                    return stackLinks;
                }
                checkForMailButton();
        );
    QVariant jsRet = mainFrame->evaluateJavaScript(clickJsCode);

    //qDebug() << jsRet.toStringList()[0];

    tableDirectLinks(jsRet.toStringList()[0]);

    const char* clickJAsCode = MULTI_LINE_STRING(
                function checkJs()
                {
                    var stackLinks = [];
                    var allSpans = document.getElementsByTagName("code");
                    for (theSpan in allSpans)
                    {
                        var link = allSpans[theSpan].innerHTML;
                        if (link)
                        {
                            stackLinks.push(link);
//                            console.log(link);
                        }
                    }
                    return stackLinks;
                }
                checkJs();
        );
    QVariant jsRetA = mainFrame->evaluateJavaScript(clickJAsCode);

    qDebug() << "-------->" << jsRetA.toStringList();

    md5totable(jsRetA.toStringList()[0]);

    timer_1->stop();
    fileCNT++;

    if (fileCNT != stackSize) {
        ui->lineEdit->setText(ui->tableWidget->item(fileCNT + (pgtCNT - 1) * stackSize, 1)->text());
        goToURL();
    } else {
        directLink = false;

        if (!(textLog->open(QIODevice::WriteOnly | QIODevice::Text | QIODevice::Append))) {
            qDebug() << "Error open file!";
        }

        QTextStream out(textLog);
        for(int i = (pgtCNT - 1) * stackSize; i < fileCNT + (pgtCNT - 1) * stackSize; ++i)
        {
            out << "Name: " << ui->tableWidget->item(i, 0)->text() << "\n";
            out << "Link: " << ui->tableWidget->item(i, 1)->text() << "\n";
            out << "MD5: " << ui->tableWidget->item(i, 3)->text() << "\n";
            out << "Direct Links:\n" << ui->tableWidget->item(i, 2)->text() << "\n\n\n";
        }
        textLog->close();

        fileCNT = 0;

        if (pgtCNT < pageCount - 1) {
            pgtCNT++;
            //pgtCNT = 42;
            ui->lineEdit->setText(QString("https://www.androidfilehost.com/?w=search&s=.xml.zip&type=files&page=%1").arg(pgtCNT));
            goToURL();
        } else {
            ui->label_2->setText("All Done!");
        }
    }
}

//void Worker::run()
//{
//    if (!wgt->itemAt(0,0)->text().isEmpty())
//    {
//        QString fileName = "tex.txt";

//        QFile file(fileName);
//        if (!(file.open(QIODevice::WriteOnly | QIODevice::Text))) {
//            QMessageBox::critical(prt, tr("Error"), tr("Error saving file!"));
//            return;
//        }

//        /* Create TextStream */
//        QTextStream out(&file);

//        /* Generate txt file */
//        for(int i = 0; i < wgt->rowCount(); ++i)
//        {
//            out << "Name: " << wgt->item(i, 0)->text() << "\n";
//            out << "Link: " << wgt->item(i, 1)->text() << "\n";
//            out << "Direct Links:\n" << wgt->item(i, 2)->text() << "\n\n\n";
//        }

//        file.close();
//        QMessageBox::information(prt, tr("Succes!"), tr("Written %1 files!").arg(wgt->rowCount()));
//    }
//    else
//    {
//        QMessageBox::critical(prt, tr("Error"), tr("Empty DB!"));
//        return;
//    }
//}
