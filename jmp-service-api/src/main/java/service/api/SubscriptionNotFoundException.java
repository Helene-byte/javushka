package service.api;

public class SubscriptionNotFoundException extends RuntimeException {

    public SubscriptionNotFoundException() {
        super("Subscription not found");
    }

    public SubscriptionNotFoundException(String bankcardNumber) {
        super("Subscription not found for card number: " + bankcardNumber);
    }
}

