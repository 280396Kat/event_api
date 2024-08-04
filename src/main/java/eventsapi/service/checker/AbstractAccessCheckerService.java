package eventsapi.service.checker;

import eventsapi.aop.AccessCheckType;
import eventsapi.aop.Accessible;
import eventsapi.securiry.AppUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractAccessCheckerService<T extends AbstractAccessCheckerService.AccessData> implements AccessCheckerService{
    @Override
    public boolean check(HttpServletRequest request, Accessible accessible) {
        if (!isUserAuthenticated()) {
            return false;
        }
        var accessData = getAccessData(request);
        return check(accessData);
    }

    @Override
    public AccessCheckType getType() {
        return null;
    }

    // метод для получения значений из переменных пути. converter используем для того, чтобы преобразовать String в какой-то конкретный тип
    @SuppressWarnings("unchecked")   // С помощью аннотации @SuppressWarnings, программист может сказать компилятору:
    // не нужно показывать предупреждения, так задумано, это не ошибка.
    protected <V> V getFromPathVariable(HttpServletRequest request, String key, Function<String, V> converter) {
    var pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    return converter.apply(Objects.requireNonNull(pathVariables.get(key)));
    }

    // получаем параметры запроса из get-запроса
    protected <V> V getFromRequestParam(HttpServletRequest request, String key, Function<String, V> converter) {
        var paramValue = request.getParameter(key);
        if (paramValue == null) {
            return null;
        }
        return converter.apply(paramValue);
    }

    // проверка на то, что юзер аутентифицирован
    private boolean isUserAuthenticated() {
        return SecurityContextHolder.getContext() != null &&
                SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof AppUserDetails;
    }

    protected abstract boolean check(T accessData);

    protected abstract T getAccessData(HttpServletRequest request);

    interface AccessData {}
}
