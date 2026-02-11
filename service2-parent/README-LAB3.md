# Service2 — ЛР3: EJB, два экземпляра, HAProxy

## Структура

- **service2-ejb** — EJB-JAR: Remote-интерфейс `GrammyServiceRemote`, Stateless Bean `GrammyServiceBean` (вся бизнес-логика), вызов Service1 через JAX-RS Client, JPA (таблица `grammy_rewards`).
- **service2-tomcat** — WAR: Spring MVC REST, только делегирование в EJB через JNDI. Деплой на Wildfly.

## Сборка

Из каталога `lab-2`:

```bash
mvn -f service2-parent/pom.xml clean install -DskipTests
```

Артефакты: `service2-ejb/target/service2-ejb-*.jar`, `service2-tomcat/target/service2-tomcat-*.war`.

## Wildfly: DataSource и системные свойства

1. Добавить драйвер PostgreSQL и DataSource (имя JNDI должно совпадать с `persistence.xml`):

   - JNDI: `java:jboss/datasources/PostgreSQLDS`
   - Параметры БД: URL, user, password (postgres/postgres/postgres для локальной разработки).

2. Системное свойство для URL первого сервиса (для EJB):

   - `-Dservice1.base.url=https://localhost:8443` (или адрес за HAProxy/Consul).

Пример через CLI:

```bash
# Драйвер (подставьте путь к postgresql-*.jar)
/subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql,driver-class-name=org.postgresql.Driver)

# DataSource
/subsystem=datasources/data-source=PostgreSQLDS:add(jndi-name=java:jboss/datasources/PostgreSQLDS,driver-name=postgresql,connection-url=jdbc:postgresql://localhost:5432/postgres,user-name=postgres,password=postgres)
```

## Пул EJB (настраиваемая мощность)

В `standalone.xml` (или через CLI) для стейтлесс-пула, например:

```xml
<subsystem xmlns="urn:jboss:domain:ejb3:...">
  <strict-max-pool name="slsb-strict-max-pool" max-pool-size="20" instance-acquisition-timeout="5"/>
  ...
</subsystem>
```

Или через CLI:

```bash
/subsystem=ejb3/strict-max-pool=slsb-strict-max-pool:write-attribute(name=max-pool-size,value=20)
```

При необходимости создаётся отдельный пул для своего приложения.

## Два экземпляра и HAProxy

1. **Экземпляр 1**: порт HTTPS 8444 (и при необходимости HTTP 8080).
2. **Экземпляр 2**: порт HTTPS 8544 (и при необходимости HTTP 8180).

На каждом экземпляре: тот же DataSource, то же `-Dservice1.base.url`, деплой одного и того же `service2-tomcat-*.war`.

3. **HAProxy** для Service2 (см. `lab-2/haproxy/haproxy.cfg`):
   - frontend на 8444 (SSL, нужен сертификат `service2.pem` в `haproxy/certs/`);
   - backend: два сервера (wildfly1:8444, wildfly2:8544), балансировка roundrobin.

Клиенты обращаются к HAProxy на 8444; запросы распределяются между двумя узлами Wildfly.

## JNDI EJB

При деплое WAR (в котором в `WEB-INF/lib` лежит `service2-ejb.jar`) Wildfly регистрирует EJB. Имя для поиска из веб-модуля:

`java:global/service2-tomcat/service2-ejb/GrammyServiceBean!c8102ea2a569.service2tomcat.api.GrammyServiceRemote`

Оно задано в `service2-tomcat/.../config/EjbConfig.java`.

## Запуск только веб-модуля (mvn spring-boot:run)

Не поддерживается: при старте требуется JNDI и работающий Wildfly с задеплоенным EJB. Для проверки нужно поднимать Wildfly и деплоить WAR.
