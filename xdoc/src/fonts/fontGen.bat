@echo off
java -Xmx256m -cp ..\..\lib\fop.jar;..\..\lib\avalon-framework-cvs-20020806.jar org.apache.fop.fonts.apps.TTFReader %1 %2 %3 %4 %5 %6 %7 %8 %9
