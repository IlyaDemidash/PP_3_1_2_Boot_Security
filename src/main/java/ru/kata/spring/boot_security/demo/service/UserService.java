package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;

import java.util.List;

public interface UserService {
    public List<User> findAllUsers();

    public User findById(Long id);

    public User saveUser(User user);

    public void deleteUser(Long id);

    public User findByUsername(String username);

    List<Role> roleList();
}
