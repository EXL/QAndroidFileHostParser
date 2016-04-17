#include "widget.h"

#include <QApplication>
#include <QStringList>

#include <iostream>

void toQuit()
{
    qApp->exit(-1);
    exit(-1);
}

void showHelp()
{
    std::cerr << "Example:\n\t"
             << "mifi cl - Get count of clients\n\t"
             << "mifi wstat - Get wifi status (1 - on, 0 - off)\n\t"
             << "mifi 3g - Get detailed wifi status\n\t"
             << "mifi won - Wifi On\n\t"
             << "mifi woff - Wifi Off\n\t"
             << "mifi help - This help text."
             << std::endl;
}

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    Widget w;

    //    w.show();

    QStringList args = a.arguments();
    if (args.count() != 2) {
        std::cerr << "Error:\n\tUsage: mifi argument" << std::endl;
        showHelp();
        toQuit();
    } else {
        if (args[1] == "cl") {
            w.getCountOfClients();
        } else if (args[1] == "wstat") {
            w.getWifiStat();
        } else if (args[1] == "3g") {
            w.getWifiDStat();
        } else if (args[1] == "won") {
            w.wifiOn();
        } else if (args[1] == "woff") {
            w.wifiOff();
        } else if (args[1] == "help") {
            showHelp();
            toQuit();
        } else {
            std::cerr << "Error:\n\tUnknown second argument!" << std::endl;
            toQuit();
        }
    }

    return a.exec();
}
