package eventsapi.mapper;

import eventsapi.entity.Organization;
import eventsapi.web.dto.CreateOrganizationRequest;
import eventsapi.web.dto.OrganizationDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE // игнорируем неизвестные для маппинга поля
)
public interface OrganizationMapper {

    Organization toEntity(CreateOrganizationRequest request);

    OrganizationDto toDto(Organization organization);
}
