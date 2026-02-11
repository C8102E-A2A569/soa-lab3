package c8102ea2a569.service2tomcat.config;

import c8102ea2a569.service2tomcat.api.GrammyServiceRemote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.naming.NamingException;

/**
 * Поиск удалённого EJB через JNDI (при деплое на Wildfly).
 * Имя задаётся в application.properties (ejb.jndi.name).
 */
@Configuration
public class EjbConfig {

    @Bean
    public GrammyServiceRemote grammyServiceRemote(
            @Value("${ejb.jndi.name}") String jndiName) throws NamingException {
        JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
        factory.setJndiName(jndiName);
        factory.setLookupOnStartup(true);
        factory.setProxyInterface(GrammyServiceRemote.class);
        factory.afterPropertiesSet();
        return (GrammyServiceRemote) factory.getObject();
    }
}
