package c8102ea2a569.service2tomcat.service;

import c8102ea2a569.service2tomcat.api.GrammyServiceRemote;
import c8102ea2a569.service2tomcat.dto.RewardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Тонкий слой сервиса: только вызов методов удалённого EJB.
 * Вся бизнес-логика в GrammyServiceBean (service2-ejb).
 */
@Service
@RequiredArgsConstructor
public class GrammyService {

    private final GrammyServiceRemote grammyServiceRemote;

    public void removeParticipant(Integer bandId) {
        grammyServiceRemote.removeParticipant(bandId);
    }

    public RewardResponse rewardBand(Integer bandId, String genre) {
        return grammyServiceRemote.rewardBand(bandId, genre);
    }
}
