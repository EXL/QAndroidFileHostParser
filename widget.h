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
    int getFilesCount() const;
    void flushToFile(int begin, int end);
    void tableDirectLinks(const QString &links);
    void md5ToTable(const QString &md5);
    int getArg() const;

private:
    Ui::Widget *ui;

private:
    WebPage *webPage;
    QWebFrame *mainFrame;
    QTimer *timer;
    QFile *textLog;

private:
    int pageCount;
    bool directLink;
    bool lastPage;
    int pgtCNT;
    int fileCNT;
    int filesCount;
    int stackSize;
    int filesOnPage;
};
#endif // WIDGET_H
