package eventsapi.event;

import eventsapi.event.model.EmailNotificationEvent;
import eventsapi.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
// слушатель нашего кастомного события(отправка писем)
public class EmailNotificationEventListener {

    private final SubscriptionService subscriptionService;

    @EventListener(EmailNotificationEvent.class)
    public void onEvent(EmailNotificationEvent event) {
        log.info("Send email for subscribers. Event: " + event);
        subscriptionService.sendNotifications(
                event.getOrganization(),
                event.getCategories(),
                event.getEventName()
        );


    }
}
