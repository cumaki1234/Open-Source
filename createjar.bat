SET BUILD=kits\build
SET JDK_PATH=%JAVA_HOME%
rem C:\program Files\java\jdk1.8.0_261\bin
rmdir %BUILD% /s /q

call compile.bat %BUILD%

rem cd resources\pics
rem call create_png.bat
rem cd ..\..

mkdir %BUILD%\resources\translation
mkdir %BUILD%\resources\pics
mkdir %BUILD%\resources\css
copy resources\pics\*.png %BUILD%\resources\pics\*
copy resources\css\*.css %BUILD%\resources\css\*
copy resources\translation\* %BUILD%\resources\translation\*
copy resources\version.txt  %BUILD%\resources\version.txt 
copy resources\species.xml %BUILD%\resources\species.xml
copy resources\example.kga %BUILD%\resources\example.kga
copy resources\christian.kga %BUILD%\resources\christian.kga

cd %BUILD%
SET RELEASE_VERSION=2.2.1
"%JDK_PATH%\bin\jar" cfe ..\KitchenGardenAid-%RELEASE_VERSION%.jar org.sourceforge.kga.KitchenGardenAid *

cd ..\..

rem the jdeps command can be used if to check dependencies of the jar file if you think they may have changed
rmdir /S /Q kits\java-runtime
"%JDK_PATH%\bin\jlink" --no-header-files --no-man-pages --compress=2 -p javafx\lib --add-modules javafx.controls,java.base,java.desktop,java.logging,java.prefs,java.xml,jdk.crypto.ec,javafx.swing,javafx.web --output kits\java-runtime
copy /Y javafx\bin\* kits\java-runtime\bin


"kits\java-runtime\bin\java" -jar kits\KitchenGardenAid.%RELEASE_VERSION%.jar

rem rmdir %BUILD% /s /q