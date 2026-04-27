package dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class Subscription {

  String bankcardNumber;
  LocalDate startDate;

  public Subscription(String bankcardNumber, LocalDate startDate) {
    this.bankcardNumber = bankcardNumber;
    this.startDate = startDate;
  }
}
