#ifndef WIDGET_H
#define WIDGET_H

#include <QWidget>
#include <QThread>

class Worker;

class QTextEdit;
class QLabel;
class QPushButton;
class QProgressBar;

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
    void on_pushButton_clicked();
    void on_pushButton_2_clicked();
    void on_pushButton_3_clicked();
    void on_pushButton_4_clicked();

public slots:
    void setOutPutText(const QString& text);
    void setOutPutTextIden(const QString& text);
    void nowDone(int a, int b);
    void progress(int percent);

private:
    Ui::Widget *ui;

private:
    QString getFileName();
    bool checkFiles();
    void openTextFile(const QString fileName, bool qFirst);

private:
    Worker *worker;
};

class Worker : public QThread
{
    Q_OBJECT

public:
    explicit Worker();
    ~Worker();

public:
    QString firstFile;
    QString secondFile;
    bool registr;

private:
    void run();

signals:
    void toOutPut(const QString&);
    void toOutPutIdentical(const QString &);
    void done(int a, int b);
    void toProgressBar(int percent);
};

#endif // WIDGET_H
