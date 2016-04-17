#ifndef WIDGET_H
#define WIDGET_H

#include "webpage.h"

#include <QWidget>
#include <QTimer>
#include <QString>

enum PageState {
    EUndef,
    EAuth,
    EClients,
    EWifi_S,
    EWifi_SON,
    EWifi_SOFF,
    EWifi_DS
};

enum WifiState {
    EWifiOn,
    EWifiOff,
    EWifiState,
    EWifiDState,
    EWifiUndef
};

namespace Ui {
class Widget;
}

class Widget : public QWidget
{
    Q_OBJECT

public:
    explicit Widget(QWidget *parent = 0);
    ~Widget();

private slots:
    void webPageLoaded(bool);

private slots:
    void getAuth();
    void toAuth();

public slots:
    void getCountOfClients();
    void getWifiStat();
    void getWifiDStat();
    void wifiOn();
    void wifiOff();

    void timerOff();
    void timerExitApp();

private:
    Ui::Widget *ui;

private:
    WebPage *webPage;
    QWebFrame *mainFrame;
    QTimer *timer;
    QTimer *timerExit;

private:
    PageState m_state;
    WifiState m_wifi_button_state;
    int exitcode;

private:
    void timerExitStart(int errCode);
    int getJsClientsCode() const;
    QString parseWifiDStat() const;
    bool parseWifiStat();
    void jsWifiOn();
    void jsWifiOff();

};
#endif // WIDGET_H
