package c8102ea2a569.service2tomcat.bean;

import c8102ea2a569.service2tomcat.api.GrammyServiceRemote;
import c8102ea2a569.service2tomcat.client.Service1Client;
import c8102ea2a569.service2tomcat.dto.MusicBandDTO;
import c8102ea2a569.service2tomcat.dto.RewardResponse;
import c8102ea2a569.service2tomcat.entity.GrammyRewardEntity;
import c8102ea2a569.service2tomcat.exception.ResourceNotFoundException;
import c8102ea2a569.service2tomcat.exception.ServiceUnavailableException;
import c8102ea2a569.service2tomcat.exception.ValidationException;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;

@Stateless
public class GrammyServiceBean implements GrammyServiceRemote {

    @PersistenceContext(unitName = "service2-ejbPU")
    private EntityManager em;

    private final Service1Client service1Client = new Service1Client();

    @Override
    public void removeParticipant(Integer bandId) {
        MusicBandDTO band = service1Client.getBandById(bandId);

        if (band.getNumberOfParticipants() == null || band.getNumberOfParticipants() <= 1) {
            throw new ValidationException("Невозможно удалить участника — в группе должен остаться хотя бы один участник");
        }

        band.setNumberOfParticipants(band.getNumberOfParticipants() - 1);
        service1Client.updateBand(bandId, band);
    }

    @Override
    public RewardResponse rewardBand(Integer bandId, String genre) {
        MusicBandDTO band = service1Client.getBandById(bandId);

        if (band.getGenre() == null || !band.getGenre().equalsIgnoreCase(genre)) {
            throw new ValidationException(
                    "Жанр группы (" + band.getGenre() + ") не соответствует указанному жанру награды: " + genre
            );
        }

        if (existsByBandIdAndGenre(bandId, genre)) {
            throw new ValidationException("Группа уже награждена Grammy в жанре " + genre);
        }

        GrammyRewardEntity reward = new GrammyRewardEntity();
        reward.setBandId(bandId);
        reward.setGenre(genre);
        reward.setRewardDate(LocalDateTime.now());
        em.persist(reward);

        return new RewardResponse(bandId, genre, "Группа была награждена Grammy в жанре " + genre);
    }

    private boolean existsByBandIdAndGenre(Integer bandId, String genre) {
        Long count = em.createQuery(
                        "SELECT COUNT(e) FROM GrammyRewardEntity e WHERE e.bandId = :bandId AND e.genre = :genre",
                        Long.class)
                .setParameter("bandId", bandId)
                .setParameter("genre", genre)
                .getSingleResult();
        return count != null && count > 0;
    }
}
