package web;

import entity.RegistrationForm;
import entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class LoginWindowLogic {

    public User login(String username, String password) {
        final String url = "http://localhost:8090/user/login?username=" + username + "&password=" + password;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<User> responseEntity;
        try {
             responseEntity = restTemplate.getForEntity(url, User.class);
        } catch (HttpClientErrorException e) {
            return null;
        }
        return responseEntity.getBody();
    }

    public String register(String username, String password) {
        final String url = "http://localhost:8090/user/registration";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(url, new RegistrationForm(username, password), String.class);
    }
}
