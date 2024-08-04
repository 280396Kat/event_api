package eventsapi.mapper;

import eventsapi.entity.User;
import eventsapi.web.dto.CreateUserRequest;
import eventsapi.web.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    User toEntity(CreateUserRequest request);

    UserDto toDto(User user);
}