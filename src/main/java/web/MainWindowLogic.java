package web;

import entity.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class MainWindowLogic {

    final RestTemplate restTemplate;

    public MainWindowLogic(String username, String password) {
        restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
    }

    public List<Order> getOrders(Long id) {
        final String url = "http://localhost:8090/user/" + id.toString() + "/orders";
        ResponseEntity<Order[]> responseEntity = restTemplate.getForEntity(url, Order[].class);
        return Arrays.asList(responseEntity.getBody());
    }

    public User getUser(Long id) {
        final String url = "http://localhost:8090/user/" + id.toString();
        return restTemplate.getForObject(url, User.class);
    }

    public List<Category> getCategories() {
        final String url = "http://localhost:8090/categories";
        ResponseEntity<Category[]> responseEntity = restTemplate.getForEntity(url, Category[].class);
        return Arrays.asList(responseEntity.getBody());
    }

    public List<Product> getProducts() {
        final String url = "http://localhost:8090/products";
        ResponseEntity<Product[]> responseEntity = restTemplate.getForEntity(url, Product[].class);
        return Arrays.asList(responseEntity.getBody());
    }

    public List<Product> getProductsByFilter(List<String> categories, String price1, String price2) {
        StringBuilder categoryList = new StringBuilder();
        categories.forEach(category -> categoryList.append(category).append("&"));
        final String url = "http://localhost:8090/products?categories=" + categoryList + "&price1=" + price1
                + "&price2=" + price2;
        ResponseEntity<Product[]> responseEntity = restTemplate.getForEntity(url, Product[].class);
        return Arrays.asList(responseEntity.getBody());
    }

    public List<Product> getProductsByName(String name) {
        final String url = "http://localhost:8090/products?name=" + name;
        ResponseEntity<Product[]> responseEntity = restTemplate.getForEntity(url, Product[].class);
        return Arrays.asList(responseEntity.getBody());
    }

    public Category addCategory(String name) {
        final String url = "http://localhost:8090/categories";
        return restTemplate.postForObject(url, name, Category.class);
    }

    public void addProduct(Product product) {
        final String url = "http://localhost:8090/products";
        restTemplate.postForObject(url, product, String.class);
    }

    public void changeProductCount(Long id, Long count) {
        final String url = "http://localhost:8090/products/" + id.toString() + "/count";
        restTemplate.postForObject(url, count, String.class);
    }

    public String addOrder(Long id, List<ProductCountOnly> cart) {
        final String url = "http://localhost:8090/user/" + id.toString() + "/orders";
        return restTemplate.postForObject(url, cart, String.class);
    }

    public String saveCart(Long id, List<ProductCountOnly> cart) {
        final String url = "http://localhost:8090/user/" + id.toString() + "/cart";
        return restTemplate.postForObject(url, cart, String.class);
    }

    public List<ProductCountOnly> loadCart(Long id) {
        final String url = "http://localhost:8090/user/" + id.toString() + "/cart";
        ResponseEntity<ProductCountOnly[]> responseEntity = restTemplate.getForEntity(url, ProductCountOnly[].class);
        return Arrays.asList(responseEntity.getBody());
    }

    public String updateUserInformation(User user) {
        final String url = "http://localhost:8090/user/" + user.getId().toString();
        HttpEntity<User> httpEntity = new HttpEntity<>(user);
        return restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class).getBody();
    }

    public List<City> loadCities() {
        final String url = "http://localhost:8090/cities";
        ResponseEntity<City[]> responseEntity = restTemplate.getForEntity(url, City[].class);
        return Arrays.asList(responseEntity.getBody());
    }

    public String changeOrder(Long id, Order order) {
        final String url = "http://localhost:8090/user/" + id.toString() + "/orders";
        HttpEntity<Order> httpEntity = new HttpEntity<>(order);
        return restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class).getBody();
    }

    public Long getCategoryProfit(String categoryName) {
        final String url = "http://localhost:8090/accounting/profit?categoryName=" + categoryName;
        return restTemplate.getForObject(url, Long.class);
    }

    public Double getAverageTotalPrice() {
        final String url = "http://localhost:8090/accounting/price";
        return restTemplate.getForObject(url, Double.class);
    }

    public Double getAverageTotalWeight() {
        final String url = "http://localhost:8090/accounting/weight";
        return restTemplate.getForObject(url, Double.class);
    }

    public List<Product> sortProducts(String sortName) {
        final String url = "http://localhost:8090/products/sort?sortName=" + sortName;
        ResponseEntity<Product[]> responseEntity = restTemplate.getForEntity(url, Product[].class);
        return Arrays.asList(responseEntity.getBody());
    }

    /*
    public List<Order> loadOrderHistory(Long id) {
        final String url = "http://localhost:8090/user/" + id.toString() + "/orders";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Order[]> responseEntity = restTemplate.getForEntity(url, Order[].class);
        return Arrays.asList(responseEntity.getBody());
    }
     */
}
