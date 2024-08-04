package eventsapi.service;

import eventsapi.entity.Organization;
import eventsapi.entity.Role;
import eventsapi.entity.User;
import eventsapi.exception.AccessDeniedException;
import eventsapi.exception.EntityNotFoundException;
import eventsapi.repository.OrganizationRepository;
import eventsapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    private final UserRepository userRepository;

    // принимает организацию, которую нужно сохранить и юзера, который собирается эту организацию создать
    @Transactional
    public Organization save(Organization organization, Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    if (!user.hasRole(Role.ROLE_ORGANIZATION_OWNER)) {  // если юзер имеет роль
                        throw new AccessDeniedException("You don't has rights for create organization!");
                    }
                    organization.setOwner(user);
                    return organizationRepository.save(organization);
                })
                .orElseThrow(() -> new EntityNotFoundException(
                        MessageFormat.format("User with id {0} not found!", userId)
                ));
    }

    public Organization findById(Long id) {
        return organizationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                MessageFormat.format("Organization with id {0} not found!", id)
        ));
    }
}
