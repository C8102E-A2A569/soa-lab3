package c8102ea2a569.service2tomcat.api;

import c8102ea2a569.service2tomcat.dto.RewardResponse;
import c8102ea2a569.service2tomcat.exception.ResourceNotFoundException;
import c8102ea2a569.service2tomcat.exception.ServiceUnavailableException;
import c8102ea2a569.service2tomcat.exception.ValidationException;

import jakarta.ejb.Remote;

@Remote
public interface GrammyServiceRemote {

    void removeParticipant(Integer bandId)
            throws ValidationException, ResourceNotFoundException, ServiceUnavailableException;

    RewardResponse rewardBand(Integer bandId, String genre)
            throws ValidationException, ResourceNotFoundException, ServiceUnavailableException;
}
