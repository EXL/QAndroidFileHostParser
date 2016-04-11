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

    webPage = new WebPage(this);
    ui->webView->setPage(webPage);

    //timer_1 = new QTimer(this);

    mainFrame = ui->webView->page()->mainFrame();

    setWindowState(Qt::WindowMaximized);

    connect(mainFrame, SIGNAL(loadFinished(bool)), this, SLOT(webPageLoaded(bool)));
    connect(ui->pushButton_3, SIGNAL(clicked(bool)), this, SLOT(goToURL()));
    //connect(ui->webView, SIGNAL(loadProgress(int)), ui->progressBar, SLOT(setValue(int)));
    //connect(timer_1, SIGNAL(timeout()), this, SLOT(timerOff()));
}

Widget::~Widget()
{
    delete ui;
}

void Widget::webPageLoaded(bool)
{
    qDebug() << "Page Loaded!";

    if (1) {
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

        //fillTableLinks(jsReturn.toStringList());
    }

    if (1) {
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
        //timer_1->start(ui->spinBox->value() * 1000);
    }
}

void Widget::goToURL()
{
    qDebug() << "Loading page...";
    ui->webView->load(QUrl(ui->lineEdit->text()));
}
