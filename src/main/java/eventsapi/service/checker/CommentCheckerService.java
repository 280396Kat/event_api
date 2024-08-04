package eventsapi.service.checker;

import eventsapi.aop.AccessCheckType;
import eventsapi.service.CommentService;
import eventsapi.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentCheckerService extends AbstractAccessCheckerService<CommentCheckerService.CommentAccessData>{

    private final CommentService commentService;


    // проверяем: пользователь, который делает что-то с комментарием, должен быть его зоздателем
    @Override
    protected boolean check(CommentAccessData accessData) {
        return commentService.hasInEvent(
                accessData.commentId,
                accessData.eventId,
                accessData.currentUserId
        );
    }

    @Override
    protected CommentAccessData getAccessData(HttpServletRequest request) {
        return new CommentAccessData(
                getFromPathVariable(request, "id", Long::valueOf),
                getFromRequestParam(request, "eventId", Long::valueOf),
                AuthUtils.getAuthenticatedUser().getId()
        );
    }

    @Override
    public AccessCheckType getType() {
        return AccessCheckType.COMMENT;
    }

    protected record CommentAccessData(Long commentId, Long eventId, Long currentUserId) implements AccessData {

    }
}
