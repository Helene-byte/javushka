package service.impl;

import dto.BankCard;
import dto.Subscription;
import dto.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import service.api.Service;

public class ServiceImpl implements Service {

  private static final List<BankCard> bankCards = new ArrayList<>();

  private static final List<Subscription> subscriptions = new ArrayList<>();

  @Override
  public void subscribe(BankCard bankCard) {
    bankCards.add(bankCard);
    var subscription = new Subscription(bankCard.getNumber(), LocalDate.now());
    subscriptions.add(subscription);
    System.out.println(subscription);
  }

  @Override
  public Optional<Subscription> getSubscriptionByBankCardNumber(String bankCardNumber) {
    return subscriptions.stream()
        .filter(subscription -> subscription.getBankcardNumber().equalsIgnoreCase(bankCardNumber))
        .findFirst();
  }

  @Override
  public List<User> getAllUsers() {
    return bankCards.stream().map(BankCard::getUser).collect(Collectors.toUnmodifiableList());
  }

  @Override
  public List<Subscription> getAllSubscriptionsByCondition(Predicate<Subscription> condition) {
    return subscriptions.stream().filter(condition).collect(Collectors.toUnmodifiableList());
  }
}
