rem rmdir kits\build /s /q

rem rem SET /P RELEASE_VERSION=<resources/version.txt
rem SET RELEASE_VERSION=2.2.1
rem git log -n1 --format=%%h > gitversion.txt
rem SET /P GIT_VERSION=<gitversion.txt
rem del gitversion.txt
rem del kits\KitchenGardenAid.%RELEASE_VERSION%.exe
rem copy resources\KitchenGardenAid.ico kits
rem "c:\Program Files (x86)\NSIS\makensis.exe" /DRELEASE_VERSION=%RELEASE_VERSION% /DDistDir=..\kits /DGIT_VERSION=%GIT_VERSION% resources\KitchenGardenAid.nsi

kits\KitchenGardenAid.%RELEASE_VERSION%.exe
gradlew createInstaller  