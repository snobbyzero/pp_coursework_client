package entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class Category {

    @JsonProperty("id")
    @NonNull
    private Long id;

    @JsonProperty("name")
    @NonNull
    private String name;

    @JsonProperty("products")
    List<Product> products = new ArrayList<>();
}
