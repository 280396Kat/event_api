package eventsapi.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
 // dto будут работать с нашими контроллерами
public class CategoryDto {

    private Long id;

    private String name;
}
