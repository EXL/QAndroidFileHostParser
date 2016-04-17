#-------------------------------------------------
#
# Project created by QtCreator 2016-03-06T16:55:07
#
#-------------------------------------------------

QT       += core gui webkit

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets webkitwidgets

TARGET = QAndroidFileHostParser
TEMPLATE = app


SOURCES += main.cpp\
        widget.cpp \
    webpage.cpp

HEADERS  += widget.h \
    webpage.h

FORMS    += widget.ui
