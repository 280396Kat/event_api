package eventsapi.repository;

import eventsapi.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    // Page используется для -> Пагинация — это процесс разделения больших наборов результатов на несколько частей, представленных на отдельных страницах.
    Page<Comment> findAllByEventId(Long eventId, Pageable pageable);

    boolean existsByIdAndEventIdAndUserId(Long id, Long event, Long userId);
}
