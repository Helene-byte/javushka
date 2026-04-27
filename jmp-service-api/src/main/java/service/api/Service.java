package service.api;

import dto.BankCard;
import dto.Subscription;
import dto.User;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Service {
  int PAYABLE_AGE = 18;

  void subscribe(BankCard bankCard);

  Optional<Subscription> getSubscriptionByBankCardNumber(String bankCardNumber);

  List<User> getAllUsers();

  List<Subscription> getAllSubscriptionsByCondition(Predicate<Subscription> condition);

  default double getAverageUsersAge() {
    return getAllUsers().stream()
        .mapToLong(user -> ChronoUnit.YEARS.between(user.getBirthday(), LocalDate.now()))
        .average()
        .getAsDouble();
  }

  static boolean isPayableUser(User user) {
    return ChronoUnit.YEARS.between(user.getBirthday(), LocalDate.now()) > PAYABLE_AGE;
  }
}
