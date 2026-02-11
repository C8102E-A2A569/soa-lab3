package c8102ea2a569.service2tomcat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudioDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String address;
}
