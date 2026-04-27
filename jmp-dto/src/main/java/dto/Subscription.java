package dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class Subscription {

  private String bankcardNumber;
  private LocalDate startDate;

  public Subscription(String bankcardNumber, LocalDate startDate) {
    this.bankcardNumber = bankcardNumber;
    this.startDate = startDate;
  }
}
