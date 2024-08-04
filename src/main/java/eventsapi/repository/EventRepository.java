package eventsapi.repository;

import eventsapi.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Override
    // указываем сущности, которые хоти подтянуть вместе с нашим Event. Используется для оптимизации загрузки связанных сущностей в бд.
    // позволяет указать, какие сущности из бд должны быть загружены одним запросом
    @EntityGraph(attributePaths = {"categories", "location", "organization", "schedule"})
    // Specification предоставляет механизм для динамических запросов к бд на основе заданных критериев. Позволяет добавить фильтрацию в запросы.
    Page<Event> findAll(Specification<Event> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"categories", "location", "organization", "schedule"})
    List<Event> findAll();

    @Override
    @EntityGraph(attributePaths = {"categories", "location", "organization", "schedule"})
    Optional<Event> findById(Long aLong);

    boolean existsByIdAndParticipantsId(Long eventId, Long userId);

    boolean existsByIdAndOrganizationOwnerId(Long eventId, Long userId);
}
