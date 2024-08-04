package eventsapi.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// эту аннотацию будем вешать на методы, чтобы проверять действия пользователей
@Retention(RetentionPolicy.RUNTIME) // аннотация которая сохраняется после компиляции и подгружается JVM
@Target({ElementType.METHOD, ElementType.MODULE}) // аннотация будет вешаться на метод
public @interface Accessible {

    AccessCheckType checkBy();
}
