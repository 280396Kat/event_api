package eventsapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateOrganizationRequest {

    @NotBlank(message = "Organization must not be blank!")
    private String name;
}
