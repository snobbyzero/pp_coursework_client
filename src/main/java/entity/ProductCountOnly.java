package entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class ProductCountOnly {

    @JsonProperty("id")
    Long id;

    @JsonProperty("product")
    @NonNull
    Product product;

    @JsonProperty("count")
    @NonNull
    Long count;

    @JsonProperty("user")
    User user;

    @JsonProperty("order")
    Order order;

    public void increase() {
        this.count++;
    }

    public void decrease() {
        if (count > 0) {
            count--;
        }
    }
}
