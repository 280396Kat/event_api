package eventsapi.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "Username must be set!")
    // специальная аннотация для валидации, которая говорит что поле не должно быть null или пустой строкой
    @Size(min = 3, max = 24, message = "Username min size in {min}, max size is{max}")
    private String name;

    @NotBlank(message = "Email must be set!")
    @Email(message = "Invalid email address!")
    private String email;

    @NotBlank(message = "Password must be set!")
    @Size(min = 6, message = "Size of password must start from {min}")
    private String password;

    private Set<String> roles;
}
