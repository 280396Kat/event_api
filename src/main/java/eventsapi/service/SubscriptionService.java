package eventsapi.service;

import eventsapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final OrganizationService organizationService;

    private final CategoryService categoryService;

    private final UserService userService;

    private final UserRepository userRepository;

    private final EmailSenderService emailSenderService;

    // Этот метод sendNotifications отвечает за отправку уведомлений по электронной почте пользователям, подписанным на определенные категории событий в рамках конкретной организации. Давайте подробно разберем, как он работает.
    //Параметры метода:
    //Long organization: идентификатор организации, для которой создается событие.
    //Collection<Long> categoriesId: коллекция идентификаторов категорий, на которые подписаны пользователи.
    //String eventName: имя события, которое будет включено в текст уведомления.
    //Получение списка email-адресов подписчиков:
    //var emails = userService.getEmailsBySubscriptions(categoriesId, organization);
    //Этот вызов обращается к userService, чтобы получить список email-адресов пользователей, подписанных на указанные категории событий в данной организации. Метод getEmailsBySubscriptions возвращает коллекцию email-адресов.
    //Отправка уведомлений по каждому email:
    //emails.forEach(email -> { ... })
    //Для каждого email из полученного списка выполняются следующие действия:
    //Создание заголовка письма:
    //String title = "New event for one of your subscriptions!";
    //Заголовок письма фиксирован и указывает, что для одной из подписок пользователя появилось новое событие.
    //Формирование тела письма:
    //String body = MessageFormat.format("New event! Name is: {0}", eventName);
    //Тело письма формируется с использованием MessageFormat, вставляя имя события (eventName) в текст сообщения.
    //Отправка письма:
    //emailSenderService.send(email, title, body);
    //Вызов метода send из emailSenderService отправляет письмо на текущий email-адрес с указанным заголовком и телом.
    public void sendNotifications(Long organization, Collection<Long> categoriesId, String eventName) {
        var emails = userService.getEmailsBySubscriptions(categoriesId, organization);
        emails.forEach(email -> {
            String title = "New event for one of your subscriptions!";
            String body = MessageFormat.format("New event! Name is: {0}", eventName);
            emailSenderService.send(email, title, body);
        });
    }

    @Transactional
    public void subscribeOnOrganization(Long userId, Long organizationId) {
        var currentUser = userService.findById(userId);
        currentUser.addSubscription(organizationService.findById(organizationId));
        userService.save(currentUser);
    }


    // Метод subscribeOnCategory отвечает за подписку пользователя на определенную категорию.
    @Transactional
    public void subscribeOnCategory(Long userId, Long categoryId) {
        var currentUser = userService.findById(userId);
        currentUser.addSubscription(categoryService.findById(categoryId));
        userService.save(currentUser);
    }

    @Transactional
    public void unsubscribeFromOrganization(Long userId, Long organizationId) {
        var currentUser = userService.findById(userId);
        var removed = currentUser.removeOrganizationSubscription(organizationId);
        if (!removed) {
            return;
        }
        userService.save(currentUser);
    }

    // отвечает за отписку пользователя на определенную категорию.
    @Transactional
    public void unsubscribeFromCategory(Long userId, Long categoryId) {
        var currentUser = userService.findById(userId);
        var removed = currentUser.removeCategorySubscription(categoryId);
        if (!removed) {
            return;
        }
        userService.save(currentUser);
    }

    public boolean hasCategorySubscription(Long userId, Long categoryId) {
        return userRepository.existsByIdAndSubscribedCategoriesId(userId, categoryId);
    }

    public boolean hasOrganizationSubscription(Long userId, Long organizationId) {
        return userRepository.existsByIdAndSubscribedOrganizationsId(userId, organizationId);
    }

}
