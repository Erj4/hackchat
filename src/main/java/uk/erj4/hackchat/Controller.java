package uk.erj4.hackchat;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class Controller {
    @RequestMapping("/")
    public String index() {
        return "Something something Spring Boot!";
    }
}
