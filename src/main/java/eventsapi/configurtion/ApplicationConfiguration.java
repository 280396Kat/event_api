package eventsapi.configurtion;

import eventsapi.aop.AccessCheckType;
import eventsapi.service.checker.AccessCheckerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public Map<AccessCheckType, AccessCheckerService> accessCheckerService(Collection<AccessCheckerService> checkerServices) {
        return checkerServices.stream().collect(Collectors.toMap(AccessCheckerService::getType, Function.identity()));
    }

    // бин для отправки ивентов, чтобы они отправлялись в другом потоке асинхронно
    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        eventMulticaster.setTaskExecutor(executor);
        return eventMulticaster;

    }
}
