package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;

import java.util.List;

public interface UserService {
    List<User> findAllUsers();

    User findById(Long id);

    User saveUser(User user);

    void deleteUser(Long id);

    User findByUsername(String username);

    List<Role> roleList();
}
