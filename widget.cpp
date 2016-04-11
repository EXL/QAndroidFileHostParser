#include "widget.h"
#include "ui_widget.h"

#include <QUrl>
#include <QDebug>
#include <QWebFrame>
#include <QApplication>

#include <cstdio>

#define MULTI_LINE_STRING(a) #a

#define DELAY_EXIT_SEC 1 * 1000
#define DELAY_PAGE_SEC 2 * 1000

Widget::Widget(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::Widget)
{
    ui->setupUi(this);

    webPage = new WebPage(this);
    ui->webView->setPage(webPage);

    m_state = EUndef;
    m_wifi_button_state = EWifiUndef;
    exitcode = (-1);

    mainFrame = ui->webView->page()->mainFrame();

    timer = new QTimer(this);
    timerExit = new QTimer(this);

    setWindowState(Qt::WindowMaximized);

    connect(mainFrame, SIGNAL(loadFinished(bool)), this, SLOT(webPageLoaded(bool)));
//    connect(ui->pushButton_3, SIGNAL(clicked(bool)), this, SLOT(getAuth()));
//    connect(ui->pushButton_4, SIGNAL(clicked(bool)), this, SLOT(getCountOfClients()));
//    connect(ui->pushButton_5, SIGNAL(clicked(bool)), this, SLOT(getWifiStat()));
//    connect(ui->pushButton, SIGNAL(clicked(bool)), this, SLOT(wifiOn()));
//    connect(ui->pushButton_2, SIGNAL(clicked(bool)), this, SLOT(wifiOff()));

    connect(timer, SIGNAL(timeout()), this, SLOT(timerOff()));
    connect(timerExit, SIGNAL(timeout()), this, SLOT(timerExitApp()));
}

Widget::~Widget()
{
    delete ui;
}

void Widget::webPageLoaded(bool)
{
    //qDebug() << "Page Loaded!" << m_state;
    int cnt = 0;
    int w_ON = 0;

    switch (m_state) {
    case EClients:
        cnt = getJsClientsCode();
        fprintf(stdout, "%d\n", cnt);
        timerExitStart(cnt);
        break;
    case EAuth:
        toAuth();
        break;
    case EWifi_S:
        w_ON = parseWifiStat() ? 1 : 0;
        fprintf(stdout, "%d\n", w_ON);
        timerExitStart(w_ON);
        break;
    case EWifi_SON:
        fprintf(stdout, "Wifi...");
        jsWifiOn();
        fprintf(stdout, "on.\n");
        timerExitStart(0);
        break;
    case EWifi_SOFF:
        fprintf(stdout, "Wifi...");
        jsWifiOff();
        fprintf(stdout, "off.\n");
        timerExitStart(0);
        break;
    default:
        break;
    }
}

void Widget::getWifiStat()
{
    m_wifi_button_state = EWifiState;
    getAuth();
}

void Widget::wifiOn()
{
    m_wifi_button_state = EWifiOn;
    getAuth();
}

void Widget::wifiOff()
{
    m_wifi_button_state = EWifiOff;
    getAuth();
}

void Widget::timerOff()
{
    switch (m_wifi_button_state) {
    case EWifiState:
        m_state = EWifi_S;
        break;
    case EWifiOn:
        m_state = EWifi_SON;
        break;
    case EWifiOff:
        m_state = EWifi_SOFF;
        break;
    default:
        break;
    }

    ui->webView->load(QUrl("http://my.jetpack/wifi/"));
    timer->stop();
}

void Widget::timerExitApp()
{
    timerExit->stop();
    qApp->exit();
}

void Widget::timerExitStart(int errCode)
{
    exitcode = errCode;
    timerExit->start(DELAY_EXIT_SEC);
}

void Widget::getAuth()
{
    m_state = EAuth;
    ui->webView->load(QUrl("http://my.jetpack/login/"));
}

void Widget::getCountOfClients()
{
    m_state = EClients;
    ui->webView->load(QUrl("http://my.jetpack/"));
}

void Widget::toAuth()
{
    QVariant jsReturn;

    const char* needAuth = MULTI_LINE_STRING(
                function qAuth()
                {
                    var auth = document.getElementsByTagName("h2")[0].innerHTML.trim();
                    return auth;
                }
                qAuth();
        );
    jsReturn = mainFrame->evaluateJavaScript(needAuth);

//    qDebug() << jsReturn.toString();
//    qDebug() << jsReturn.toString().indexOf("logged");

    if (!(jsReturn.toString().indexOf("logged") > 0)) {
        const char* theJavaScriptCodeToInject = MULTI_LINE_STRING(
                function checkAuth()
                {
                    var pswd = document.getElementById("inputPassword").value = "voha888";
                    return pswd;
                }
                checkAuth();
            );
        jsReturn = mainFrame->evaluateJavaScript(theJavaScriptCodeToInject);

        if (jsReturn.toString() == "voha888") {
            const char* clickToLoginButton = MULTI_LINE_STRING(
                    function clickButton()
                    {
                        var answ = document.getElementById("loginSubmit").click();
                        return answ;
                    }
                    clickButton();
                );
            mainFrame->evaluateJavaScript(clickToLoginButton);
        } else {
           // qDebug() << "Error: toAuth 1";
        }
    } else {
       // qDebug() << "Error: Auth already complete!";
    }

    switch (m_wifi_button_state) {
    case EWifiState:
    case EWifiOn:
    case EWifiOff:
        timer->start(DELAY_PAGE_SEC);
        break;
    default:
        break;
    }
}

int Widget::getJsClientsCode() const
{
    const char* theJavaScriptCodeToInject = MULTI_LINE_STRING(
            function checkClients()
            {
                var clients = document.getElementsByClassName("badge")[0].innerHTML;
                return clients;
            }
            checkClients();
        );
    QVariant jsReturn = mainFrame->evaluateJavaScript(theJavaScriptCodeToInject);

    //qDebug() << jsReturn.toString();

    return jsReturn.toString().toInt();
}

bool Widget::parseWifiStat()
{
    const char* theJavaScriptCodeToInject = MULTI_LINE_STRING(
            function checkedWifi()
            {
                var q = document.getElementById("enableWifi").checked;
                return q;
            }
            checkedWifi();
        );
    QVariant jsReturn = mainFrame->evaluateJavaScript(theJavaScriptCodeToInject);

    // qDebug() << jsReturn.toBool();

    return jsReturn.toBool();
}

void Widget::jsWifiOn()
{
    const char* theJavaScriptCodeToInject = MULTI_LINE_STRING(
            function toggleWifi()
            {
                var tg = document.getElementById("enableWifi").click();
                return tg;
            }
            toggleWifi();
        );
    mainFrame->evaluateJavaScript(theJavaScriptCodeToInject);
}

void Widget::jsWifiOff()
{
    const char* theJavaScriptCodeToInject_CH = MULTI_LINE_STRING(
            function toggleWifi()
            {
                var tg = document.getElementById("enableWifi").click();
                return tg;
            }
            toggleWifi();
        );
    mainFrame->evaluateJavaScript(theJavaScriptCodeToInject_CH);

    const char* theJavaScriptCodeToInject_Confirm = MULTI_LINE_STRING(
            function toggle()
            {
                var tg = document.getElementsByClassName("primary")[1].click();
                return tg;
            }
            toggle();
        );
    mainFrame->evaluateJavaScript(theJavaScriptCodeToInject_Confirm);
}
