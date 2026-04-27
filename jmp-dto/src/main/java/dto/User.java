package dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class User {

  private String name;
  private String surname;
  private LocalDate birthday;
}
