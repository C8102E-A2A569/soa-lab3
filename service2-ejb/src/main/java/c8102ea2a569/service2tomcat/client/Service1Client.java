package c8102ea2a569.service2tomcat.client;

import c8102ea2a569.service2tomcat.dto.MusicBandDTO;
import c8102ea2a569.service2tomcat.exception.ResourceNotFoundException;
import c8102ea2a569.service2tomcat.exception.ServiceUnavailableException;

import javax.net.ssl.SSLContext;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.logging.Logger;

/**
 * HTTP-клиент к первому сервису (REST API групп).
 * Использует JAX-RS Client с доверием к самоподписанному сертификату.
 */
public class Service1Client {

    private static final Logger LOG = Logger.getLogger(Service1Client.class.getName());
    private static final String BASE_URL_PROP = "service1.base.url";
    private static final String DEFAULT_BASE_URL = "https://localhost:8443";

    private final Client client;
    private final String baseUrl;

    public Service1Client() {
        baseUrl = System.getProperty(BASE_URL_PROP, DEFAULT_BASE_URL);
        try {
            SSLContext sslContext = createTrustAllContext();
            client = ClientBuilder.newBuilder()
                    .sslContext(sslContext)
                    .hostnameVerifier((hostname, session) -> true)
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create JAX-RS client", e);
        }
    }

    private static SSLContext createTrustAllContext() throws Exception {
        javax.net.ssl.TrustManager[] trustAll = new javax.net.ssl.TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                }
        };
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, trustAll, new java.security.SecureRandom());
        return ctx;
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }

    public MusicBandDTO getBandById(Integer bandId) {
        String url = baseUrl + "/api/bands/" + bandId;
        try {
            MusicBandDTO dto = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get(MusicBandDTO.class);
            return dto;
        } catch (NotFoundException e) {
            throw new ResourceNotFoundException("Группа с ID " + bandId + " не найдена в Service 1");
        } catch (Exception e) {
            throw new ServiceUnavailableException("Service 1 недоступен: " + e.getMessage());
        }
    }

    public void updateBand(Integer bandId, MusicBandDTO dto) {
        String url = baseUrl + "/api/bands/" + bandId;
        try (Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(dto, MediaType.APPLICATION_JSON))) {
            if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new ResourceNotFoundException("Группа с ID " + bandId + " не найдена в Service 1");
            }
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                throw new ServiceUnavailableException("Service 1 вернул: " + response.getStatus());
            }
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceUnavailableException("Service 1 недоступен: " + e.getMessage());
        }
    }
}
