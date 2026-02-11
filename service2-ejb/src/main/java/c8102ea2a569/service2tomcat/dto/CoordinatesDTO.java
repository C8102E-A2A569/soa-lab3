package c8102ea2a569.service2tomcat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long x;
    private Double y;
}
