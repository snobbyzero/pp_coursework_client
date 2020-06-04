package entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
public class Order {

    @JsonProperty("id")
    Long id;

    @JsonProperty("user")
    User user;

    @JsonProperty("products")
    List<ProductCountOnly> products;

    @JsonProperty("orderDate")
    Timestamp orderDate;

    @JsonProperty("deliveryDate")
    Timestamp deliveryDate;
}
