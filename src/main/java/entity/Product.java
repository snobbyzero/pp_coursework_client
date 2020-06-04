package entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Product {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    @NonNull
    private String name;

    @JsonProperty("description")
    @NonNull
    private String description;

    @JsonProperty("count")
    @NonNull
    private Long count;

    @JsonProperty("weight")
    @NonNull
    private Float weight;

    @JsonProperty("categories")
    List<Category> categories = new ArrayList<>();

    //@JsonProperty("users")
    //List<User> users = new ArrayList<>();

    @JsonProperty("imagePath")
    @NonNull
    String imagePath;

    @JsonProperty("price")
    @NonNull
    Integer price;

    @JsonIgnore
    List<ProductCountOnly> productCountOnlyList = new ArrayList<>();

}
