package c8102ea2a569.service2tomcat.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ErrorResponse {

    private String error;

    private String message;

    private int status;

}
