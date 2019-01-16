package uk.erj4.hackchat.data.exception;

public class UserNotFoundException extends RuntimeException {
    private String username;

    public UserNotFoundException(String username) {
        this.username=username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String getMessage() {
        return "Could not find user with username \"" + username + "\"";
    }
}
