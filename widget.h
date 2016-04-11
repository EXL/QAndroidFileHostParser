#ifndef WIDGET_H
#define WIDGET_H

#include "webpage.h"

#include <QWidget>
#include <QTimer>
#include <QFile>

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

public slots:
    void goToURL();

private:
    Ui::Widget *ui;

private:
    WebPage *webPage;
    QWebFrame *mainFrame;
    //QTimer *timer_1;
};
#endif // WIDGET_H
