package application;

import bank.api.Bank;
import dto.BankCard;
import dto.BankCardType;
import dto.User;
import service.api.Service;
import service.api.SubscriptionNotFoundException;

import java.time.LocalDate;
import java.util.ServiceLoader;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Loading via ServiceLoader ===");

        // Task 25: use ServiceLoader to find implementations instead of new ConcreteClass()
        var bank = ServiceLoader.load(Bank.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Bank implementation found"));

        var service = ServiceLoader.load(Service.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Service implementation found"));

        System.out.println("Bank loaded:    " + bank.getClass().getSimpleName());
        System.out.println("Service loaded: " + service.getClass().getSimpleName());

        var user1 = new User("Scarlett", "Ohara", LocalDate.of(1990, 12, 2));
        var user2 = new User("Jane", "Eyre", LocalDate.of(1980, 12, 2));

        final BankCard bankCard = bank.createBankCard(user1, BankCardType.DEBIT);
        service.subscribe(bankCard);
        service.subscribe(bank.createBankCard(user1, BankCardType.DEBIT));
        service.subscribe(bank.createBankCard(user1, BankCardType.CREDIT));
        service.subscribe(bank.createBankCard(user2, BankCardType.DEBIT));
        service.subscribe(bank.createBankCard(user2, BankCardType.DEBIT));

        System.out.println("\n=== getAllUsers ===");
        System.out.println(service.getAllUsers());

        // Task 21: orElseThrow with custom exception (no exception expected — card was just subscribed)
        System.out.println("\n=== getSubscriptionByBankCardNumber (orElseThrow) ===");
        var subscription = service.getSubscriptionByBankCardNumber(bankCard.getNumber())
                .orElseThrow(() -> new SubscriptionNotFoundException(bankCard.getNumber()));
        System.out.println(subscription);

        System.out.println("\n=== getAverageUsersAge ===");
        System.out.println(service.getAverageUsersAge());

        System.out.println("\n=== isPayableUser ===");
        System.out.println("user1 payable: " + Service.isPayableUser(user1));
        System.out.println("user2 payable: " + Service.isPayableUser(user2));

        // Task 22: getAllSubscriptionsByCondition with a Predicate
        System.out.println("\n=== getAllSubscriptionsByCondition (startDate = today) ===");
        var todaysSubs = service.getAllSubscriptionsByCondition(
                s -> s.getStartDate().equals(LocalDate.now()));
        todaysSubs.forEach(System.out::println);

        // Task 21: demonstrate that the exception IS thrown for an unknown card
        System.out.println("\n=== SubscriptionNotFoundException demo ===");
        try {
            service.getSubscriptionByBankCardNumber("UNKNOWN-999")
                    .orElseThrow(() -> new SubscriptionNotFoundException("UNKNOWN-999"));
        } catch (SubscriptionNotFoundException e) {
            System.out.println("Caught expected: " + e.getMessage());
        }
    }
}