## Структура

- **service2-ejb** — EJB-JAR: Remote-интерфейс `GrammyServiceRemote`, Stateless Bean `GrammyServiceBean` (вся бизнес-логика), вызов Service1 через JAX-RS Client (`client/Service1Client.java`), JPA (таблица `grammy_rewards`).
- **service2-tomcat** — WAR: Spring MVC REST, только делегирование в EJB через JNDI. Деплой на Wildfly. Клиент к Service1, сущности и репозитории находятся в service2-ejb; пустые пакеты `client`, `entity`, `repository` в service2-tomcat можно удалить.
