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
    void goToURL();
    void timerOff();

private:
    void fillTableLinks(const QStringList &stackLinks);
    int getPageCount() const;
    void tableDirectLinks(const QString &links);

private:
    Ui::Widget *ui;

private:
    WebPage *webPage;
    QWebFrame *mainFrame;
    QTimer *timer_1;
    QFile *textLog;

private:
    int pageCount;
    bool directLink;

    int pgtCNT;
    int fileCNT;
    int stackSize;
};
#endif // WIDGET_H
