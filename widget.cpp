#include "widget.h"
#include "ui_widget.h"

#include <QUrl>
#include <QDebug>
#include <QApplication>

#include <QtGlobal>
#if QT_VERSION >= 0x050000
    #include <QWebFrame>
#else
    #include <QtWebKit/QWebFrame>
#endif

#include <cstdio>

#define MULTI_LINE_STRING(a) #a

#define DELAY_EXIT_SEC 2 * 1000
#define DELAY_PAGE_SEC 3 * 1000

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
    case EWifi_DS:
        fprintf(stdout, "%s\n", parseWifiDStat().toStdString().c_str());
        timerExitStart(w_ON);
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

void Widget::getWifiDStat()
{
    m_wifi_button_state = EWifiDState;
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
    case EWifiDState:
        m_state = EWifi_DS;
    default:
        break;
    }

    if (m_state == EWifi_DS) {
        ui->webView->load(QUrl("http://my.jetpack/diagnostics/"));
    } else {
        ui->webView->load(QUrl("http://my.jetpack/wifi/"));
    }
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
        }
    }

    switch (m_wifi_button_state) {
    case EWifiState:
    case EWifiOn:
    case EWifiOff:
    case EWifiDState:
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

    return jsReturn.toString().toInt();
}

QString Widget::parseWifiDStat() const
{
    // Network operator:    document.getElementById("internetStatusNetworkOperator").innerHTML.trim()
    // Status:              document.getElementById("internetStatus3G").innerHTML.trim()
    // RSSI (1x):           document.getElementById("internetStatus3G1xRSSI").innerHTML.trim()
    // RSSI (EVDO):         document.getElementById("internetStatus3GEVDORSSI").innerHTML.trim()
    // Technology:          document.getElementById("internetStatus3GTechnology").innerHTML.trim()
    // Ec/Io:               document.getElementById("internetStatus3gECIo").innerHTML.trim()
    // SID:                 document.getElementById("internetStatus3GSID").innerHTML.trim()

    const char* theJavaScriptCodeToInject = MULTI_LINE_STRING(
            function checkDStat()
            {
                var no = document.getElementById("internetStatusNetworkOperator").innerHTML.trim();
                var stat = document.getElementById("internetStatus3G").innerHTML.trim();
                var rss1 = document.getElementById("internetStatus3G1xRSSI").innerHTML.trim();
                var rss2 = document.getElementById("internetStatus3GEVDORSSI").innerHTML.trim();
                var tech = document.getElementById("internetStatus3GTechnology").innerHTML.trim();
                var ecio = document.getElementById("internetStatus3gECIo").innerHTML.trim();
                var sid = document.getElementById("internetStatus3GSID").innerHTML.trim();

//                var answer =    "Network operator: \t" + no + "\n" +
//                                "Status: \t\t" + stat + "\n" +
//                                "RSSI (1x): \t\t" + rss1 + "\n" +
//                                "RSSI (EVDO): \t\t" + rss2 + "\n" +
//                                "Technology: \t\t" + tech + "\n" +
//                                "Ec/Io: \t\t\t" + ecio + "\n" +
//                                "SID: \t\t\t" + sid;

                var answer = stat + "," + rss1 + "," + rss2 + "," + tech + "," + ecio;

                return answer;
            }
            checkDStat();
        );
    QVariant jsReturn = mainFrame->evaluateJavaScript(theJavaScriptCodeToInject);
    return jsReturn.toString();
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
