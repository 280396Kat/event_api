package eventsapi.web.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserDto {

    private Long id;

    private String userName;

    private String email;

    private String firstName;

    private String lastName;

    private Set<String> roles;
}
