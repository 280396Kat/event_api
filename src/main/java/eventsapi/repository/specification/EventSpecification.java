package eventsapi.repository.specification;

import eventsapi.entity.Category;
import eventsapi.entity.Event;
import eventsapi.model.EventFilterModel;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.util.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDate;
import java.util.Set;

// кастомные ошибки, которые мы будем использовать при разработке нашего приложени
public interface EventSpecification {

    static Specification<Event> withFilter(EventFilterModel filterModel) {
        return Specification.where(isEquals("id", filterModel.getId()))
                .and(isEquals("name", filterModel.getName()))
                .and(isEquals("location", "city", filterModel.getCity()))
                .and(isEquals("location", "street", filterModel.getStreet()))
                .and(isEquals("organization", "name", filterModel.getOrganizationName()))
                .and(inCategories(filterModel.getCategoryIds()))
                .and(isEquals(filterModel.getStartTime(), "startTime"))
                .and(isEquals(filterModel.getEndTime(), "endTime"));
    }

    // объект в бд равен тому, что нам пришло от клиента
    private static <T> Specification<Event> isEquals(String filedName, T object) {
        return (root, query, criteriaBuilder) -> {
            if (object == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get(filedName), object);
        };
    }

    private static Specification<Event> isEquals(LocalDate date, String fieldName) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return null;
            }
            return criteriaBuilder.equal(
                    criteriaBuilder.function("date", LocalDate.class, root.get(fieldName)),
                    date
            );
        };
    }

    private static Specification<Event> isEquals(String rootName, String fieldName, String value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get(rootName).get(fieldName), value);
        };
    }

    // метод, чтобы убедиться, что у нас есть категории, которые передает клиент
    private static Specification<Event> inCategories(Set<Long> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            if (CollectionUtils.isEmpty(categoryIds)) {
                return null;
            }

            Join<Event, Category> categoryJoin = root.join("categories", JoinType.INNER);

            Predicate[] predicates = categoryIds.stream()
                    .map(categoryId -> criteriaBuilder.equal(categoryJoin.get("id"), categoryId))
                    .toArray(Predicate[]::new);

            return criteriaBuilder.or(predicates);
        };
    }
}

