package eventsapi.service.checker;

import eventsapi.aop.AccessCheckType;
import eventsapi.service.SubscriptionService;
import eventsapi.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
        // сервис проверки для подписок
public class SubscriptionCheckerService extends AbstractAccessCheckerService<SubscriptionCheckerService.SubscriptionAccessData> {

    private final SubscriptionService subscriptionService;

    @Override
    protected boolean check(SubscriptionAccessData accessData) {
        if (accessData.categoryId != null && accessData.organizationId != null) {
            return subscriptionService.hasOrganizationSubscription(accessData.currentUser, accessData.organizationId)
                    && subscriptionService.hasCategorySubscription(accessData.currentUser, accessData.categoryId);
        }
        if (accessData.categoryId == null) {
            return subscriptionService.hasOrganizationSubscription(accessData.currentUser, accessData.organizationId);
        } else {
            return subscriptionService.hasCategorySubscription(accessData.currentUser, accessData.categoryId);
        }
    }

    @Override
    protected SubscriptionAccessData getAccessData(HttpServletRequest request) {
        return new SubscriptionAccessData(
                getFromRequestParam(request, "categoryId", Long::valueOf),
                getFromRequestParam(request, "organizationId", Long::valueOf),
                AuthUtils.getAuthenticatedUser().getId()
        );
    }

    @Override
    public AccessCheckType getType() {
        return AccessCheckType.SUBSCRIPTION;
    }

    protected record SubscriptionAccessData(Long categoryId, Long organizationId, Long currentUser) implements
            AbstractAccessCheckerService.AccessData {
    }
}
