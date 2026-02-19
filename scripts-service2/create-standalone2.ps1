# Создаёт копию standalone в standalone2 для второго узла Wildfly.
# Запуск: .\create-standalone2.ps1
# Или с путём: .\create-standalone2.ps1 -WildflyHome "D:\Programs\wildfly-31.0.0.Final"

param(
    [string]$WildflyHome = $env:WILDFLY_HOME
)

if (-not $WildflyHome) {
    $WildflyHome = "D:\Programs\wildfly-31.0.0.Final"
}

$standalone = Join-Path $WildflyHome "standalone"
$standalone2 = Join-Path $WildflyHome "standalone2"

if (-not (Test-Path $standalone)) {
    Write-Error "Папка не найдена: $standalone. Задайте WILDFLY_HOME или -WildflyHome."
    exit 1
}

if (Test-Path $standalone2) {
    Write-Host "standalone2 уже существует: $standalone2"
    exit 0
}

Write-Host "Копирование standalone -> standalone2..."
Copy-Item -Path $standalone -Destination $standalone2 -Recurse
Write-Host "Готово. Второй узел будет использовать: $standalone2"
Write-Host "Запуск второго узла: bin\standalone.bat -Djboss.server.base.dir=standalone2 -Djboss.socket.binding.port-offset=100"
