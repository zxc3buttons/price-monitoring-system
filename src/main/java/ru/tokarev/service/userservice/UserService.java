package ru.tokarev.service.userservice;

import ru.tokarev.entity.User;

import java.util.List;

public interface UserService {

    User getById(Long id);

    List<User> getAll();

    User createUser(User user);

    User updateUser(Long id, User user);

    User updateUserRole(Long id, User user);

    void deleteUser(Long id);
}
