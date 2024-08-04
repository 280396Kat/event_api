package eventsapi.mapper;

import eventsapi.entity.Category;
import eventsapi.web.dto.CategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

// помогает сопотавлять объекты из одной сущности в другой
// мапстракт автоматически генерирует реализации интерфейсов маппинга для преобразования одного java-объекта в другой
// на основании аннотаций и интерфейсов. Позволяет писать меньше кода для ручного преобразования объектов.
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE // игнорируем неизвестные для маппинга поля
)
public interface CategoryMapper {

    CategoryDto toDto(Category category); // передаем  category, а из неё получаем CategoryDto

    List<CategoryDto> toDtoList(List<Category> categories);
}
