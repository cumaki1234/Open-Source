!include "MUI.nsh"

SetCompressor /SOLID /FINAL lzma
Name "KithenGardenAid"
OutFile "${DistDir}/KitchenGardenAid-${RELEASE_VERSION}.exe"
!define MUI_ICON KitchenGardenAid.ico
VIProductVersion "${RELEASE_VERSION}.0.0"
VIAddVersionKey "ProductName" "KitchenGardenAid"
VIAddVersionKey "ProductVersion" "${RELEASE_VERSION} ( ${GIT_VERSION} )"
VIAddVersionKey "FileDescription" "Garden manager"
VIAddVersionKey "LegalCopyright" "GNU GPL Ver. 3"
VIAddVersionKey "FileVersion" "${RELEASE_VERSION}"
InstallDir $PROGRAMFILES\KitchenGardenAid

InstallDirRegKey HKCU "Software\KitchenGardenAid" ""

ShowInstDetails show

;--------------------------------
;Pages
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE gpl.txt
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES

!define MUI_FINISHPAGE_NOAUTOCLOSE
!define MUI_FINISHPAGE_RUN
!define MUI_FINISHPAGE_RUN_CHECKED
!define MUI_FINISHPAGE_RUN_TEXT "Run KitchenGardenAid"
!define MUI_FINISHPAGE_RUN_FUNCTION "LaunchLink"
!insertmacro MUI_PAGE_FINISH

;Languages
!insertmacro MUI_LANGUAGE "English"

;--------------------------------
Function .onInit
    SetRegView 32
    Call GetJavaVersion
    Pop $0
    Pop $1
    Pop $2
    Pop $3
    StrCmp $0 "no" CheckRegView64 CheckJavaVersion

CheckRegView64:
    SetRegView 64
    Call GetJavaVersion
    Pop $0
    Pop $1
    Pop $2
    Pop $3
    StrCmp $0 "no" JavaNotInstalled

CheckJavaVersion:
    IntCmp $0 1 L1 UpgradeJava L1
L1: IntCmp $1 8 L3 UpgradeJava L3
L3: Return

UpgradeJava:
    StrCpy $0 "Java runtime $0.$1.$2 is installed"
    GoTo AlertUser

JavaNotInstalled:
    StrCpy $0 "Java runtime is not installed"

AlertUser:
    ;Exec "rundll32 url.dll,FileProtocolHandler http://www.java.com/inc/BrowserRedirect1.jsp"
    ;MessageBox MB_YESNO "$0; at least version 1.7 is needed.$\r$\nYou may download it from www.java.com$\r$\nDo you want to continue installation?" IDYES ContinueInstallation
     ;   Abort
	Return

ContinueInstallation:
FunctionEnd


# start default section
Section "Install" Install
    # set the installation directory as the destination for the following actions
    SetOutPath $INSTDIR

    CreateDirectory $INSTDIR
    #File /a ..\kits\KitchenGardenAid.${RELEASE_VERSION}.jar
	File /a /r /x ${DistDir}\build /x ${DistDir}\KitchenGardenAid-${RELEASE_VERSION}.exe windows\*
    #File /a KitchenGardenAid.ico
	#File /a /r ..\kits\java-runtime /oname=java-runtime

    # create the uninstaller
    WriteUninstaller "$INSTDIR\uninstall.exe"

    # create a shortcut named "new shortcut" in the start menu programs directory
    # point the new shortcut at the program uninstaller
    SetShellVarContext all
    CreateShortCut  "$DESKTOP\KitchenGardenAid.lnk" "$INSTDIR\java-runtime\bin\java" "-jar KitchenGardenAid-${RELEASE_VERSION}.jar" "$INSTDIR\KitchenGardenAid.ico"
    CreateDirectory "$SMPROGRAMS\KitchenGardenAid"
    CreateShortCut  "$SMPROGRAMS\KitchenGardenAid\KitchenGardenAid.lnk" "$INSTDIR\java-runtime\bin\java" "-jar KitchenGardenAid-${RELEASE_VERSION}.jar" "$INSTDIR\KitchenGardenAid.ico"
    CreateShortCut  "$SMPROGRAMS\KitchenGardenAid\Uninstall.lnk" "$INSTDIR\uninstall.exe"
  
    call GetJavaPath
    Pop $0

    # create association
    WriteRegStr HKCR ".kga" "" "KitchenGardenAid"
    WriteRegStr HKCR ".seed" "" "KitchenGardenAid"
    WriteRegStr HKCR "KitchenGardenAid" "" `KitchenGardenAid`
    WriteRegStr HKCR "KitchenGardenAid\DefaultIcon" "" '"$INSTDIR\KitchenGardenAid.ico"'
    WriteRegStr HKCR "KitchenGardenAid\shell" "" "open"
    WriteRegStr HKCR "KitchenGardenAid\shell\open" "" `Open with KitchenGardenAid`
    WriteRegStr HKCR "KitchenGardenAid\shell\open\command" "" '"$0\bin\javaw.exe" -jar "$INSTDIR\KitchenGardenAid-${RELEASE_VERSION}.jar" "%1"'
SectionEnd

Section "uninstall"
    SetShellVarContext all
    Delete "$DESKTOP\KitchenGardenAid.lnk"
    Delete "$SMPROGRAMS\KitchenGardenAid\KitchenGardenAid.lnk"
    Delete "$SMPROGRAMS\KitchenGardenAid\Uninstall.lnk"
    RMDir "$SMPROGRAMS\KitchenGardenAid"

    Delete "$INSTDIR\uninstall.exe"
    Delete "$INSTDIR\KitchenGardenAid.ico"
    Delete "$INSTDIR\KitchenGardenAid-${RELEASE_VERSION}.jar"
    RMDIR  /r "$INSTDIR\java-runtime"
    RMDir "$INSTDIR"
SectionEnd

Function GetJavaPath
  push $1

  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  StrCmp $1 "" DetectTry2
  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$1" "JavaHome"
  Goto GotPath
DetectTry2:
  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
  StrCmp $1 "" NotFound
  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$2" "JavaHome"
GotPath:
  Push $1
  Exch
  Pop $1
  return
NotFound:
  Push ""
  Exch
  Pop $1
FunctionEnd
  
# http://nsis.sourceforge.net/Get_full_Java_version
###############################################################################
; Find installed java version and return major, minor, micro and build/update version
; For some reason v1.2.1_004 did not give a build version, but it's the only one of its kind.
; There are 3 ways to get the build version:
;   1) from the UpdateVersion key
;   2) or from the MicroVersion key
;   3) or from the JavaHome key
;example
;  call GetJavaVersion
;  pop $0 ; major version
;  pop $1 ; minor version
;  pop $2 ; micro version
;  pop $3 ; build/update version
;  strcmp $0 "no" JavaNotInstalled
;  strcmp $3 "" nobuild
;  DetailPrint "$0.$1.$2_$3"
;  goto fin
;nobuild:
;  DetailPrint "$0.$1.$2"
;  goto fin
;JavaNotInstalled:
;  DetailPrint "Java Not Installed"
;fin:
Function GetJavaVersion
  push $R0
  push $R1
  push $2
  push $0
  push $3
  push $4

  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  StrCmp $2 "" DetectTry2
  ReadRegStr $3 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$2" "MicroVersion"
  StrCmp $3 "" DetectTry2
  ReadRegStr $4 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$2" "UpdateVersion"
  StrCmp $4 "" 0 GotFromUpdate
  ReadRegStr $4 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$2" "JavaHome"
  Goto GotJRE
DetectTry2:
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
  StrCmp $2 "" NoFound
  ReadRegStr $3 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$2" "MicroVersion"
  StrCmp $3 "" NoFound
  ReadRegStr $4 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$2" "UpdateVersion"
  StrCmp $4 "" 0 GotFromUpdate
  ReadRegStr $4 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$2" "JavaHome"
GotJRE:
  ; calc build version
  strlen $0 $3
  intcmp $0 1 0 0 GetFromMicro
  ; get it from the path
GetFromPath:
  strlen $R0 $4
  intop $R0 $R0 - 1
  StrCpy $0 ""
loopP:
  StrCpy $R1 $4 1 $R0
  StrCmp $R1 "" DotFoundP
  StrCmp $R1 "_" UScoreFound
  StrCmp $R1 "." DotFoundP
  StrCpy $0 "$R1$0"
  Goto GoLoopingP
DotFoundP:
  push ""
  Exch 6
  goto CalcMicro
UScoreFound:
  push $0
  Exch 6
  goto CalcMicro
GoLoopingP:
  intcmp $R0 0 DotFoundP DotFoundP
  IntOp $R0 $R0 - 1
  Goto loopP
GetFromMicro:
  strcpy $4 $3
  goto GetFromPath
GotFromUpdate:
  push $4
  Exch 6

CalcMicro:
  Push $3 ; micro
  Exch 6
  ; break version into major and minor
  StrCpy $R0 0
  StrCpy $0 ""
loop:
  StrCpy $R1 $2 1 $R0
  StrCmp $R1 "" done
  StrCmp $R1 "." DotFound
  StrCpy $0 "$0$R1"
  Goto GoLooping
DotFound:
  Push $0 ; major
  Exch 5
  StrCpy $0 ""
GoLooping:
  IntOp $R0 $R0 + 1
  Goto loop

done:
  Push $0 ; minor
  Exch 7
  ; restore register values
  pop $0
  pop $2
  pop $R1
  pop $R0
  pop $3
  pop $4
  return
NoFound:
  pop $4
  pop $3
  pop $0
  pop $2
  pop $R1
  pop $R0
  push ""
  push "installed"
  push "java"
  push "no"
FunctionEnd

;--
Function LaunchLink
    ExecShell "" "$SMPROGRAMS\KitchenGardenAid\KitchenGardenAid.lnk"
FunctionEnd