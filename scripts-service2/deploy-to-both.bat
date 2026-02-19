@echo off
setlocal
if not defined WILDFLY_HOME set "WILDFLY_HOME=D:\Programs\wildfly-31.0.0.Final"
set "WAR=service2-tomcat\target\service2-tomcat-0.0.1-SNAPSHOT.war"
cd /d "%~dp0"
set "PARENT=%~dp0.."
set "WAR_PATH=%PARENT%\%WAR%"
if not exist "%WAR_PATH%" (
    echo Соберите проект: mvn -f service2-parent/pom.xml clean package -DskipTests
    echo WAR не найден: %WAR_PATH%
    exit /b 1
)
set "DEP1=%WILDFLY_HOME%\standalone\deployments"
set "DEP2=%WILDFLY_HOME%\standalone2\deployments"
if not exist "%DEP1%" (
    echo Папка не найдена: %DEP1%. Задайте WILDFLY_HOME.
    exit /b 1
)
if not exist "%DEP2%" (
    echo Сначала создайте standalone2: powershell -File "%~dp0create-standalone2.ps1"
    exit /b 1
)
echo Копирование WAR в оба узла...
copy /Y "%WAR_PATH%" "%DEP1%\"
copy /Y "%WAR_PATH%" "%DEP2%\"
echo Готово. Узел 1: %DEP1%
echo Узел 2: %DEP2%
echo Перезапустите оба Wildfly, если они уже запущены.
endlocal
