package entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.OneToOne;

@NoArgsConstructor
@Data
public class City {

    @JsonProperty("id")
    Long id;

    @JsonProperty("name")
    String name;

    @JsonProperty("timeOfDelivery")
    Integer timeOfDelivery;

    @JsonProperty("user")
    User user;

    public String toString() {
        return this.name;
    }
}
