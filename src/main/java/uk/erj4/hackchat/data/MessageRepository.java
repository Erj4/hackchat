package uk.erj4.hackchat.data;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Integer> {
    default List<Message> findByFromOrToOrderBySentDesc(String username) {
        return findByFromOrToOrderBySentDesc(username, username);
    }

    List<Message> findByFromOrToOrderBySentDesc(String from, String to);
}
