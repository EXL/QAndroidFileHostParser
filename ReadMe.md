QAndroidFileHostParser
======================

Utility with tools for parsing various websites. Written with using Java and the JavaFX framework.

![Windows 10 QAndroidFileHostParser](images/QAndroidFileHostParser-JavaFX.png)

This program has built-in such useful tools as:

### Requests Tool

![Windows 10 Requests Tool](images/RequestsTool-JavaFX.png)

This tool can be used to send POST and GET requests to remote servers.

### File and Text Comparator Tool

![Windows 10 Comparator Tool](images/ComparatorTool-JavaFX.png)

This utility can be used to process text files, reports and CSV generations.

## Download

You can download ready-to-run JavaFX-bundle packed with [7z file archiver](http://www.7-zip.org/) for MS Windows from the [releases](https://github.com/EXL/QAndroidFileHostParser/releases) section.

## Build instructions

For example, MS Windows:

* Install the latest [Java](https://www.java.com/download/);

* Clone repository into deploy directory;

```bat
cd C:\Dev\Deploy\
git clone https://github.com/EXL/QAndroidFileHostParser QAndroidFileHostParser
```

* Build the fat (with all dependencies) JavaFX-bundle into deploy directory with Gradle Build Tool;

```bat
cd C:\Dev\Deploy\QAndroidFileHostParser\
gradlew.bat jfxNative
```

* Get cooked JavaFX-bundle from the `build\jfx\native\` directory.

## Another branches in this repository

* Old utilities written with using Qt 5 framework: [qt5_old](https://github.com/EXL/QAndroidFileHostParser/tree/qt5_old).

* Console utility for Verizon Jetpack MiFi: [mifi5510](https://github.com/EXL/QAndroidFileHostParser/tree/mifi5510).
