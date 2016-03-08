#include <QString>
#include <QVector>
#include <QFile>
#include <QDebug>
#include <QTextStream>

int main(int argc, char *argv[])
{
    Q_UNUSED(argc);
    Q_UNUSED(argv);

    QVector<QString> in;

    QFile fileIn("SW-Log.txt");
    QFile fileOut("SW-log-formatted.txt");

    if (!fileIn.open(QIODevice::ReadOnly | QIODevice::Text)) {
        qDebug() << "Error open SW-Log.txt for reading!";
        return (-1);
    }

    if (!fileOut.open(QIODevice::WriteOnly | QIODevice::Text)) {
        qDebug() << "Error open SW-log-formatted.txt for writting!";
        return (-1);
    }

    QTextStream inStream(&fileIn);
    QTextStream outStream(&fileOut);

    while (!(inStream.atEnd())) {
        in.push_back(inStream.readLine());
    }

    //qDebug() << in.length();
    QString name;
    QString link;
    QString dlink;
    QString md5;
    QString sep = ";";
    int counter = 0;
    for (int i = 0; i < in.size(); ++i) {
        if (in[i].startsWith("Name: ")) {
            name = in[i].remove("Name: ").trimmed();
        }

        if (in[i].startsWith("Link: ")) {
            link = in[i];
            link = link.remove("Link: ").trimmed();
        }

        if (in[i].startsWith("MD5: ")) {
            md5 = in[i];
            md5 = md5.remove("MD5: ").trimmed();
        }

        if (in[i].startsWith("http")) {
            dlink += in[i] + sep;
        }

        if (in[i] == "") {
            counter++;
        }

        if (counter == 3) {
            counter = 0;
            outStream << name + sep + dlink + link + sep + md5 + "\n";
            name = dlink = link = md5 = "";
        }
    }

    fileIn.close();
    fileOut.close();

    return 0;
}

