package eventsapi.service;

import eventsapi.entity.Comment;
import eventsapi.exception.EntityNotFoundException;
import eventsapi.model.PageModel;
import eventsapi.repository.CommentRepository;
import eventsapi.repository.EventRepository;
import eventsapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    public List<Comment> findAllByEventId(Long eventId) {
        return findAllByEventId(eventId, null).getContent();
    }

    // Этот метод ищет все комментарии, связанные с конкретным событием (eventId), и возвращает их в виде страницы (Page<Comment>), что позволяет обрабатывать большие объемы данных порциями.
    //Параметры метода:
    //Long eventId: идентификатор события, для которого нужно найти комментарии.
    //PageModel pageModel: объект, содержащий информацию о пагинации (номер страницы, размер страницы и т.д.). Если он равен null, будет использована пагинация по умолчанию, без разделения на страницы.
    //Действия метода:
    //Проверка на null и получение Pageable:
    //pageModel == null ? Pageable.unpaged() : pageModel.toPageRequest()
    //Если pageModel равен null, используется Pageable.unpaged(), что означает отсутствие пагинации (все результаты будут возвращены сразу).
    //В противном случае, метод вызывает pageModel.toPageRequest(), чтобы получить объект Pageable на основе информации из pageModel.
    //Поиск комментариев:
    //commentRepository.findAllByEventId(eventId, pageable): метод репозитория, который ищет все комментарии по заданному eventId и возвращает их в виде страницы, используя переданный Pageable.
    public Page<Comment> findAllByEventId(Long eventId, PageModel pageModel) {
        return commentRepository.findAllByEventId(
                eventId,
                pageModel == null ? Pageable.unpaged() : pageModel.toPageRequest()
        );
    }

    // Этот метод сохраняет комментарий (Comment) от определенного пользователя (userId) для конкретного события (eventId). Он также обеспечивает, чтобы пользователь и событие были существующими в базе данных перед сохранением комментария.
    //Аннотация @Transactional
    //@Transactional указывает на то, что этот метод должен выполняться в рамках одной транзакции базы данных. Это гарантирует атомарность операции: либо все операции будут выполнены успешно и закоммичены, либо в случае ошибки произойдет откат (rollback), и состояние базы данных не изменится.
    //Действия метода:
    //Поиск события (currentEvent):
    //eventRepository.findById(eventId): Ищет событие по его идентификатору (eventId) в репозитории eventRepository.
    //.orElseThrow(() -> new EntityNotFoundException(...)): Если событие не найдено, выбрасывает исключение EntityNotFoundException с сообщением об ошибке.
    //Поиск пользователя и сохранение комментария:
    //userRepository.findById(userId): Ищет пользователя по его идентификатору (userId) в репозитории userRepository.
    //.map(user -> { ... }): Если пользователь найден, выполняет действия в блоке { ... }, где user - это найденный пользователь.
    //В блоке { ... }:
    //comment.setUser(user): Устанавливает пользователя для комментария.
    //comment.setEvent(currentEvent): Устанавливает событие для комментария (найденное ранее).
    //commentRepository.save(comment): Сохраняет комментарий в репозитории commentRepository и возвращает сохраненный объект комментария.
    //Обработка исключений:
    //Если событие или пользователь не найдены (через .orElseThrow(...)), выбрасывается EntityNotFoundException с соответствующим сообщением.
    @Transactional
    public Comment save(Comment comment, Long userId, Long eventId) {
        var currentEvent = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                MessageFormat.format("Event with id {0} not found!", eventId)
                        )
                );
        return userRepository.findById(userId)
                .map(user -> {
                    comment.setUser(user);
                    comment.setEvent(currentEvent);

                    return commentRepository.save(comment);
                })
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                MessageFormat.format("User with id {0} not found!", userId)
                        )
                );
    }

    @Transactional
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    public boolean hasInEvent(Long commentId, Long eventId, Long authorId) {
        return commentRepository.existsByIdAndEventIdAndUserId(commentId, eventId, authorId);
    }
}
