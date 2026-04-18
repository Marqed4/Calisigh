!include "MUI2.nsh"
!include "LogicLib.nsh"

Name "Calisigh"
OutFile "CalisighInstaller.exe"
InstallDir "$PROGRAMFILES64\Calisigh"
RequestExecutionLevel admin

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_LANGUAGE "English"

Section "Install"
  SetOutPath "$INSTDIR"

  ; Install Java first so it's on PATH when app launches
  Call CheckAndInstallJava

  ; Copy your Tauri build output
  File /r "path\to\your\tauri\output\*.*"

  ; Desktop shortcut
  CreateShortcut "$DESKTOP\Calisigh.lnk" "$INSTDIR\Calisigh.exe"

  ; Start menu shortcut
  CreateDirectory "$SMPROGRAMS\Calisigh"
  CreateShortcut "$SMPROGRAMS\Calisigh\Calisigh.lnk" "$INSTDIR\Calisigh.exe"

  WriteUninstaller "$INSTDIR\Uninstall.exe"
SectionEnd

Function CheckAndInstallJava
  ; Check registry for JDK
  ReadRegStr $0 HKLM "SOFTWARE\JavaSoft\JDK" "CurrentVersion"
  
  ${If} $0 == ""
    ; Also check for JRE
    ReadRegStr $0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ${EndIf}

  ${If} $0 == ""
    DetailPrint "Java not found, installing Temurin JRE 21..."
    File "temurin-21-jre.msi"
    ExecWait 'msiexec /i "$INSTDIR\temurin-21-jre.msi" /quiet /norestart'
    Delete "$INSTDIR\temurin-21-jre.msi"
    DetailPrint "Java installed."
  ${Else}
    DetailPrint "Java $0 already installed, skipping."
  ${EndIf}
FunctionEnd

Section "Uninstall"
  Delete "$INSTDIR\*.*"
  RMDir /r "$INSTDIR"
  Delete "$DESKTOP\Calisigh.lnk"
  Delete "$SMPROGRAMS\Calisigh\Calisigh.lnk"
  RMDir "$SMPROGRAMS\Calisigh"
SectionEnd