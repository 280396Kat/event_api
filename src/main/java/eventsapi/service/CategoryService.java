package eventsapi.service;

import eventsapi.entity.Category;
import eventsapi.exception.EntityNotFoundException;
import eventsapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    //найти категорию по id
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        MessageFormat.format("Category with id {0} not found!", id)
                ));
    }

    //найти все категории
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    // метод склеивает категории. Если категории нет в БД, создаем их.
    // Шаг 1: Получаем имена категорий
    //Мы преобразуем каждую категорию из входного множества в ее имя (String) с помощью .map(Category::getName).
    //Collectors.toSet() создает новое множество (Set<String>), содержащее уникальные имена всех категорий.
    //Шаг 2: Находим существующие категории
    //categoryRepository.findAllByNameIn(eventCategories) ищет все категории из базы данных, имена которых совпадают с именами в eventCategories.
    //Результат сохраняется в списке existedCategories.
    //Шаг 3: Фильтруем категории для обновления
    //Мы фильтруем входное множество категорий (categories), исключая те, имена которых уже существуют в базе данных (existedCategoryNames).
    //Оставшиеся категории, которые нужно добавить или обновить, сохраняются в списке categoriesForUpdate.
    //Шаг 4: Обновляем базу данных и возвращаем результат
    //Мы объединяем существующие категории (existedCategories) с новыми категориями для обновления (categoriesForUpdate) с помощью Stream.concat.
    //categoryRepository.saveAll(categoriesForUpdate) сохраняет все категории для обновления в базу данных.
    //collect(Collectors.toSet()) собирает все результаты в итоговое множество (Set<Category>), которое затем возвращается из метода.
    //Этот метод позволяет эффективно управлять категориями в базе данных: он находит существующие категории, определяет новые категории для вставки или обновления, сохраняет изменения и возвращает обновленное множество категорий.
    public Set<Category> upsertCategories(Set<Category> categories) {
        Set<String> eventCategories = categories.stream()
                .map(Category::getName).collect(Collectors.toSet());
        List<Category> existedCategories = categoryRepository.findAllByNameIn(eventCategories);
        Set<String> existedCategoryNames = existedCategories.stream()
                .map(Category::getName)
                .collect(Collectors.toSet());
        List<Category> categoriesForUpdate = categories.stream()
                .filter(it -> !existedCategoryNames.contains(it.getName()))
                .toList();
        return Stream.concat(existedCategories.stream(),
                        categoryRepository.saveAll(categoriesForUpdate).stream())
                .collect(Collectors.toSet());
    }
}
