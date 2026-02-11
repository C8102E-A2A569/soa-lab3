package c8102ea2a569.service2tomcat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RewardResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer bandId;
    private String genre;
    private String message;
}
