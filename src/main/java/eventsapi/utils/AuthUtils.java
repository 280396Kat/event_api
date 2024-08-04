package eventsapi.utils;

import eventsapi.exception.ServerException;
import eventsapi.securiry.AppUserDetails;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@UtilityClass
public class AuthUtils { // класс для вытаскивания инфо из UserDetails

    // Оператор instanceof нужен, чтобы проверить, был ли объект, на который ссылается переменная X,
    // создан на основе какого-либо класса Y.
    public Long getCurrentUserId(UserDetails userDetails) {
        if (userDetails instanceof AppUserDetails details) {
            return details.getId();
        }
        throw new SecurityException("UserDetails is not instanceof AppUserDetails");
    }

    // var определяет тип создаваемой переменной по типу значения, которое ей присваивают.
    public AppUserDetails getAuthenticatedUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof AppUserDetails details) {
            return details;
        }
        throw new ServerException("Principal is security context is not instanceof AppUserDetails");
    }
}
