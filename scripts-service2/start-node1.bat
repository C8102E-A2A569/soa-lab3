@echo off
setlocal
if not defined WILDFLY_HOME set "WILDFLY_HOME=D:\Programs\wildfly-31.0.0.Final"
if not exist "%WILDFLY_HOME%\bin\standalone.bat" (
    echo WILDFLY_HOME не найден: %WILDFLY_HOME%
    exit /b 1
)
echo Starting Wildfly node 1 (HTTP 8080, bind 0.0.0.0 for Docker HAProxy)...
cd /d "%WILDFLY_HOME%\bin"
call standalone.bat -b 0.0.0.0
endlocal
