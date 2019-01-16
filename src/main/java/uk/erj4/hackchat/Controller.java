package uk.erj4.hackchat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.erj4.hackchat.data.Message;
import uk.erj4.hackchat.data.MessageRepository;
import uk.erj4.hackchat.data.User;
import uk.erj4.hackchat.data.UserRepository;
import uk.erj4.hackchat.data.exception.UserNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
public class Controller {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/")
    public String index() {
        return "Something something Spring Boot!";
    }

    @PostMapping("/users")
    public void postUser(@RequestBody User user) {
        userRepository.save(user);
    }

    @GetMapping("/users")
    public @ResponseBody Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{username}")
    public @ResponseBody User getUser(@PathVariable String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findFirstByUsername(username);
        return user.orElseThrow(() -> new UserNotFoundException(username));

    }

    @PostMapping("/users/{username}/messages")
    public void postMessage(@PathVariable String username, @RequestBody Message message) throws UserNotFoundException {
        message.setFrom(getUser(username));
        message.setSent(Instant.now());
        messageRepository.save(message);
    }

    @GetMapping("/users/{username}/messages")
    public List<Message> getMessages(@PathVariable String username) throws UserNotFoundException {
        return messageRepository.findByFromOrToOrderBySentDesc(username);
    }
}
