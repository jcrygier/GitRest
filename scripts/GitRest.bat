@echo off
setlocal

if "%OS%"=="Windows_NT" goto getpath
echo Please use this script with a Windows NT Instance
goto :eof

:getpath
set _SCRIPT_PATH=%~dp0

rem Find the prunsrv executable
set _PRUNSRV_EXE=%_SCRIPT_PATH%commons-daemon-windows\prunsrv.exe
set _PRUNSRV_AMD64_EXE=%_SCRIPT_PATH%commons-daemon-windows\prunsrv.exe
set _PRUNSRV_IA64_EXE=%_SCRIPT_PATH%commons-daemon-windows\prunsrv.exe
if exist "%_PRUNSRV_EXE%" goto validate
echo Unable to locate Commons Daemon wrapper:
echo %_PRUNSRV_EXE%
pause
goto :eof

:validate
rem Find the requested command.
for /F %%v in ('echo %1^|findstr "^install ^installAmd64$ ^installIa64$ ^uninstall$ ^uninstallAmd64$ ^uninstallIa64$"') do call :exec set COMMAND=%%v

if "%COMMAND%" == "" (
    echo Usage: %0 { install : installAmd64 : installIa64 : uninstall : uninstallAmd64 :uninstallIa64  }
    pause
    goto :eof
) else (
    shift
)

rem
rem Find / Set the classpath
rem
setLocal EnableDelayedExpansion
set CLASSPATH="
for /R %_SCRIPT_PATH%libs %%a in (*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%a
)
set CLASSPATH=!CLASSPATH!"

set INSTALL_OPTIONS=//IS//GitRest --DisplayName="GitRest" --Description="REST-ful Git interface to enable a Git client via a Web Browser" --Classpath="%CLASSPATH%" --LogPath=%_SCRIPT_PATH%logs --LogPrefix=gitrest  --LogLevel=Info --StdError=auto --StdOutput=auto --Jvm=auto --StartMode=jvm --StopMode=jvm --StartClass=com.crygier.git.rest.Main --StartParams=start;"%_SCRIPT_PATH%conf.properties" --StopClass=com.crygier.git.rest.Main --StopParams=stop
set UNINSTALL_OPTIONS=//DS/GitRest

rem
rem Run the application, based on what was selected during validate
rem
call :%COMMAND%
if errorlevel 1 pause
goto :eof

:install
"%_PRUNSRV_EXE%" %INSTALL_OPTIONS%
goto :eof

:installAmd64
"%_PRUNSRV_AMD64_EXE%" %INSTALL_OPTIONS%
goto :eof

:installIa64
"%_PRUNSRV_IA64_EXE%" %INSTALL_OPTIONS%
goto :eof

:uninstall
"%_PRUNSRV_EXE%" %UNINSTALL_OPTIONS%
goto :eof

:uninstallAmd64
"%_PRUNSRV_AMD64_EXE%" %UNINSTALL_OPTIONS%
goto :eof

:uninstallIa64
"%_PRUNSRV_IA64_EXE%" %UNINSTALL_OPTIONS%
goto :eof

:exec
%*
goto :eof

