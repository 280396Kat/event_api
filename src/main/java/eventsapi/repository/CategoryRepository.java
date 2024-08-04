package eventsapi.repository;

import eventsapi.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

// параметризированный интерфейс(1 - сущность, пок оторой будут идти запросы 2 - id нашей сущности)
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // говорящий метод, который превращается в запрос и ищет все категории по полю name, которые есть передаваемые в метод коллекции
    List<Category> findAllByNameIn(Collection<String> name);
}
