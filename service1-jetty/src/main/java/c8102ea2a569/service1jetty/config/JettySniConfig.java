package c8102ea2a569.service1jetty.config;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("docker")
public class JettySniConfig {

    @Bean
    public WebServerFactoryCustomizer<JettyServletWebServerFactory> jettySniCustomizer() {
        return factory -> factory.addServerCustomizers(server -> {
            applySniDisable(server);
            server.addEventListener(new LifeCycle.Listener() {
                @Override
                public void lifeCycleStarting(LifeCycle event) {
                    applySniDisable((Server) event);
                }
            });
        });
    }

    private void applySniDisable(Server server) {
        for (Connector connector : server.getConnectors()) {
            SslConnectionFactory sslFactory = connector.getConnectionFactory(SslConnectionFactory.class);
            if (sslFactory != null) {
                SslContextFactory.Server sslContextFactory = (SslContextFactory.Server) sslFactory.getSslContextFactory();
                if (sslContextFactory != null) {
                    sslContextFactory.setSniRequired(false);
                }
                HttpConfiguration httpConfig = getHttpConfiguration(connector);
                if (httpConfig != null) {
                    ensureSniDisabled(httpConfig);
                }
            }
        }
    }

    private void ensureSniDisabled(HttpConfiguration httpConfig) {
        SecureRequestCustomizer src = httpConfig.getCustomizer(SecureRequestCustomizer.class);
        if (src != null) {
            src.setSniRequired(false);
            src.setSniHostCheck(false);
        }
    }

    private HttpConfiguration getHttpConfiguration(Connector connector) {
        for (String next : new String[]{"http/1.1", "h2", "h2c"}) {
            var factory = connector.getConnectionFactory(next);
            if (factory instanceof HttpConfiguration.ConnectionFactory cf) {
                return cf.getHttpConfiguration();
            }
        }
        return null;
    }
}
