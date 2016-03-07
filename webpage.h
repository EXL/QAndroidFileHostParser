#ifndef WEBPAGE_H
#define WEBPAGE_H

#include <QWebPage>

class WebPage : public QWebPage
{
    Q_OBJECT

public:
    WebPage(QObject * parent = 0);

protected:
    virtual void javaScriptConsoleMessage(const QString & message, int lineNumber, const QString & sourceID);
};

#endif // WEBPAGE_H
