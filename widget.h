#ifndef WIDGET_H
#define WIDGET_H

#include "webpage.h"

#include <QWidget>
#include <QTimer>

enum PageState {
    EUndef,
    EAuth,
    EClients,
    EWifi_S,
    EWifi_SON,
    EWifi_SOFF
};

enum WifiState {
    EWifiOn,
    EWifiOff,
    EWifiState,
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

private:
    void timerExitStart();
    int getJsClientsCode() const;
    bool parseWifiStat();
    void jsWifiOn();
    void jsWifiOff();

};
#endif // WIDGET_H
