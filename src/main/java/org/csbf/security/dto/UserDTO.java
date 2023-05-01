package org.csbf.security.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private boolean accountEnabled;
    private boolean accountBlocked;
}
