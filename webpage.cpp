#include "webpage.h"

#include <QDebug>

WebPage::WebPage(QObject *parent) :
    QWebPage(parent)
{

}

void WebPage::javaScriptConsoleMessage(const QString &message, int lineNumber, const QString &sourceID)
{
    Q_UNUSED(lineNumber);
    Q_UNUSED(sourceID);
    Q_UNUSED(message);
    // qDebug() << "[JS]:" << message;
}

