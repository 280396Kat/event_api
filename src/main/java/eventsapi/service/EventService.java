package eventsapi.service;

import eventsapi.entity.BaseEntity;
import eventsapi.entity.Event;
import eventsapi.event.model.EmailNotificationEvent;
import eventsapi.exception.AccessDeniedException;
import eventsapi.exception.EntityNotFoundException;
import eventsapi.model.EventFilterModel;
import eventsapi.repository.EventRepository;
import eventsapi.repository.LocationRepository;
import eventsapi.repository.ScheduleRepository;
import eventsapi.repository.specification.EventSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.ap.shaded.freemarker.template.utility.StringUtil;
import org.springframework.boot.ExitCodeEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// сервис работы с событиями. Для последующей отправки писем для пользователей, которые подписаны на какую-то организацию или категории
public class EventService {

    private final EventRepository eventRepository;

    private final CategoryService categoryService;

    private final ScheduleRepository scheduleRepository;

    private final LocationRepository locationRepository;

    private final OrganizationService organizationService;

    private final UserService userService;

    private final ApplicationEventPublisher eventPublisher;

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Page<Event> filter(EventFilterModel filterModel) {
        return eventRepository.findAll(
                EventSpecification.withFilter(filterModel),
                filterModel.getPage().toPageRequest()
        );
    }

    public Event getById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(
                MessageFormat.format("Event with id {0} not found!", eventId)
        ));
    }

    @Transactional
    public Event create(Event event) {
        event.setCategories(categoryService.upsertCategories(event.getCategories()));
        event.setSchedule(scheduleRepository.save(event.getSchedule()));
        var location = locationRepository.findByCityAndStreet(event.getLocation().getCity(),
                        event.getLocation().getStreet())
                .orElseGet(() -> locationRepository.save(event.getLocation()));
        event.setLocation(location);
        var organization = organizationService.findById(event.getOrganization().getId());
        if (event.getOrganization().isNotSameOwner(organization.getOwner().getId())) {
            throw new AccessDeniedException("User is not owner!");
        }
        organization.addEvent(event);
        event.setOrganization(organization);
        var savedEvent = eventRepository.save(event);
        eventPublisher.publishEvent(new EmailNotificationEvent(
                this,
                event.getCategories().stream().map(BaseEntity::getId).collect(Collectors.toSet()),
                organization.getId(),
                event.getName()
        ));

        return savedEvent;
    }

    @Transactional
    public Event update(Long eventId, Event eventForUpdate) {
        var curentEvent = getById(eventId);
        if (eventForUpdate.getName() != null && !Objects.equals(eventForUpdate.getName(), curentEvent.getName())) {
            curentEvent.setName(eventForUpdate.getName()); // если имена не равны, добавляем новое
        }
        if (eventForUpdate.getStartTime() != null && !Objects.equals(eventForUpdate.getStartTime(), curentEvent.getStartTime())) {
            curentEvent.setStartTime(eventForUpdate.getStartTime());
        }
        if (eventForUpdate.getEndTime() != null && !Objects.equals(eventForUpdate.getEndTime(), curentEvent.getEndTime())) {
            curentEvent.setEndTime(eventForUpdate.getEndTime());
        }
        var currentSchedule = curentEvent.getSchedule();
        var updateSchedule = eventForUpdate.getSchedule();
        if (updateSchedule != null && StringUtils.isNoneBlank(updateSchedule.getDescription()) &&
                !Objects.equals(currentSchedule.getDescription(), updateSchedule.getDescription())) {
            currentSchedule.setDescription(updateSchedule.getDescription());
        }
        if (!CollectionUtils.isEmpty(eventForUpdate.getCategories())) {
            curentEvent.setCategories(categoryService.upsertCategories(eventForUpdate.getCategories()));
        }
        return eventRepository.save(curentEvent);
    }

    @Transactional
    public boolean addParticipant(Long eventId, Long participantId) {
        var event = getById(eventId);
        var participant = userService.findById(participantId);
        var isAdded = event.addParticipant(participant);
        if (!isAdded) {
            return false;
        }
        eventRepository.save(event);
        return true;
    }

    @Transactional
    public boolean removeParticipant(Long eventId, Long participantId) {
        var event = getById(eventId);
        var participant = userService.findById(participantId);
        var isRemoved = event.removeParticipant(participant);
        if (!isRemoved) {
            return false;
        }
        eventRepository.save(event);
        return true;
    }

    @Transactional
    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }

    public boolean hasParticipant(Long eventId, Long participantId) {
        return eventRepository.existsByIdAndParticipantsId(eventId, participantId);
    }

    // пользователь по id и есть создатель события
    public boolean isEventCreator(Long eventId, Long userId) {
        return eventRepository.existsByIdAndOrganizationOwnerId(eventId, userId);
    }

}

