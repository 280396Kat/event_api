package eventsapi.service.checker;

import eventsapi.aop.AccessCheckType;
import eventsapi.aop.Accessible;
import jakarta.servlet.http.HttpServletRequest;

public interface AccessCheckerService {

    boolean check(HttpServletRequest request, Accessible accessible);

    AccessCheckType getType(); // тип чекер сервиса
}
