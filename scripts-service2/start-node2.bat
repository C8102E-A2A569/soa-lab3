@echo off
setlocal
if not defined WILDFLY_HOME set "WILDFLY_HOME=D:\Programs\wildfly-31.0.0.Final"
if not exist "%WILDFLY_HOME%\bin\standalone.bat" (
    echo WILDFLY_HOME не найден: %WILDFLY_HOME%
    exit /b 1
)
if not exist "%WILDFLY_HOME%\standalone2" (
    echo Сначала создайте standalone2: powershell -File "%~dp0create-standalone2.ps1"
    exit /b 1
)
echo Starting Wildfly node 2 (HTTP 8180, port-offset=100, bind 0.0.0.0 for Docker HAProxy)...
cd /d "%WILDFLY_HOME%\bin"
call standalone.bat -Djboss.server.base.dir="%WILDFLY_HOME%\standalone2" -Djboss.socket.binding.port-offset=100 -b 0.0.0.0
endlocal
