# Lab-2 / Lab-3

## Service1 (вызываемый)

- ЛР3: Spring Boot, Consul, два экземпляра (18443, 18543), HAProxy на 8443.
- Swagger: https://localhost:8443/swagger-ui/index.html (через HAProxy) или https://localhost:18443/... (инстанс 1).

Запуск через Docker: `docker-compose up -d` (Consul, PostgreSQL, 2× Service1, HAProxy).

- **Запуск на университетском сервере (helios / se.ifmo.ru):** [RUN-ON-SERVER.md](RUN-ON-SERVER.md) — Docker, выбор БД (контейнер или pg), порт (8443 или 9001).

## Service2 (вызывающий)

- ЛР3: два модуля — **service2-ejb** (EJB-JAR, бизнес-логика, Remote EJB) и **service2-tomcat** (WAR, REST, делегирование в EJB). Деплой на Wildfly; два экземпляра Wildfly + HAProxy.
- Сборка: `mvn -f service2-parent/pom.xml clean install -DskipTests`
- Подробности: [service2-parent/README-LAB3.md](service2-parent/README-LAB3.md)
- Swagger (после деплоя на Wildfly): https://localhost:8444/swagger-ui/index.html
