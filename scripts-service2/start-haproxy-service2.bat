@echo off
setlocal
set "SCRIPT_DIR=%~dp0"
echo HAProxy Service2 (Docker): frontend 9090 -^> backends 8080, 8180
echo Статистика: http://localhost:9091/stats
docker start haproxy-service2
if errorlevel 1 (
    echo Контейнер не найден. Создайте его из каталога lab-2, примонтировав haproxy/haproxy-service2-docker.cfg. См. docs/SERVICE2-TWO-NODES.md
)
endlocal
