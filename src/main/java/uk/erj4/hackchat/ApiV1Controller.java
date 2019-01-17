package uk.erj4.hackchat;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.hibernate.id.IdentifierGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import uk.erj4.hackchat.data.Message;
import uk.erj4.hackchat.data.MessageRepository;
import uk.erj4.hackchat.data.User;
import uk.erj4.hackchat.data.UserRepository;
import uk.erj4.hackchat.data.exception.UserNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ApiV1Controller {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("")
    public String index() {
        return "Something something Spring Boot!";
    }

    @PostMapping("/users")
    public ResponseEntity postUser(@RequestBody User user) {
        try {
            userRepository.save(user);
        } catch (Exception originalException) {

            Throwable e = originalException;
            while (e.getCause() != null && !(e instanceof ConstraintViolationException)) e = e.getCause();
            if (e instanceof ConstraintViolationException) {
                List<String> violations = ((ConstraintViolationException) e).getConstraintViolations().stream()
                        .map(ApiV1Controller::prettyPrintViolation)
                        .collect(Collectors.toList());
                return new ResponseEntity<>(violations, HttpStatus.BAD_REQUEST);
            }
            if (e instanceof IdentifierGenerationException) {
                return new ResponseEntity<>("username value 'null' must not be empty", HttpStatus.BAD_REQUEST);
            }
            throw originalException;
        }
        return null;
    }

    @GetMapping("/users")
    @ResponseBody
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{username}")
    @ResponseBody
    public User getUser(@PathVariable String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(username);
        return user.orElseThrow(() -> HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "User \"" + username + "\" does not exist",
                HttpHeaders.EMPTY, null, null));

    }

    @PostMapping("/users/{username}/messages")
    public ResponseEntity postMessage(@PathVariable String username, @RequestBody Message message) throws UserNotFoundException {
        message.setFrom(getUser(username));
        message.setSent(Instant.now());
        try {
            messageRepository.save(message);
        } catch (Exception originalException) {
            Throwable e = originalException;
            while (e.getCause() != null && !(e instanceof ConstraintViolationException) && !(e instanceof IdentifierGenerationException)) e = e.getCause();
            if (e instanceof ConstraintViolationException) {
                List<String> violations = ((ConstraintViolationException) e).getConstraintViolations().stream()
                        .map(ApiV1Controller::prettyPrintViolation)
                        .collect(Collectors.toList());
                return new ResponseEntity<>(violations, HttpStatus.BAD_REQUEST);
            }
            throw originalException;
        }
        return null;
    }

    @GetMapping("/users/{username}/messages")
    public List<Message> getMessages(@PathVariable String username) throws UserNotFoundException {
        return messageRepository.findByFromOrToOrderBySentDesc(username);
    }

    private static String prettyPrintViolation(ConstraintViolation v) {
        return String.format("%s value '%s' %s", v.getPropertyPath(), v.getInvalidValue(), v.getMessage());
    }
}
