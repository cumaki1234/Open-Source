set BUILD=%1
if [%1] EQU [] set BUILD=.
rmdir %BUILD%\org /s /q
if [%1] NEQ [] mkdir %1

"%JAVA_HOME%\bin\javac" ^
    -d %BUILD% -encoding utf-8 --release 9 ^
    src\org\sourceforge\kga\*.java ^
    src\org\sourceforge\kga\flowlist\*.java ^
    src\org\sourceforge\kga\io\*.java ^
    src\org\sourceforge\kga\plant\*.java ^
    src\org\sourceforge\kga\rules\*.java ^
    src\org\sourceforge\kga\wrappers\*.java ^
    src\org\sourceforge\kga\gui\*.java ^
    src\org\sourceforge\kga\gui\gardenplan\*.java ^
    src\org\sourceforge\kga\gui\gardenplan\toolbar\*.java ^
    src\org\sourceforge\kga\gui\plants\*.java ^
    src\org\sourceforge\kga\gui\rules\*.java ^
    src\org\sourceforge\kga\gui\tableRecords\*.java ^
    src\org\sourceforge\kga\gui\tableRecords\seedlistmanager\*.java ^
    src\org\sourceforge\kga\gui\tableRecords\soilNutrition\*.java ^
    src\org\sourceforge\kga\gui\actions\*.java ^
    src\org\sourceforge\kga\gui\actions\importData\*.java ^
    src\org\sourceforge\kga\translation\*.java ^
    src\org\sourceforge\kga\prefs\*.java --module-path javafx\lib --add-modules=javafx.controls,javafx.web,javafx.swing
