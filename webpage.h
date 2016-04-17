#ifndef WEBPAGE_H
#define WEBPAGE_H

#include <QtGlobal>
#if QT_VERSION >= 0x050000
    #include <QWebPage>
#else
    #include <QtWebKit/QWebPage>
#endif

class WebPage : public QWebPage
{
    Q_OBJECT

public:
    WebPage(QObject * parent = 0);

protected:
    virtual void javaScriptConsoleMessage(const QString & message, int lineNumber, const QString & sourceID);
};

#endif // WEBPAGE_H
