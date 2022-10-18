package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String showAllUsers(Model model) {
        List<User> userList = userService.findAllUsers();
        model.addAttribute("users", userList);
        return "allusers";
    }

    @GetMapping(value = "/addNewUser")
    public String addNewUserView(Model model) {
        List<Role> roleList = userService.roleList();
        model.addAttribute("user", new User());
        model.addAttribute("roleList", roleList);
        return "adduser";
    }

    @PostMapping(value = "/saveUser")
    public String addUserAction(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping(value = "/updateUser/{id}")
    public String updateUserView(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        List<Role> roleList = userService.roleList();
        model.addAttribute("roleList", roleList);
        model.addAttribute("user", user);
        return "update";
    }

    @PostMapping("/{id}")
    public String updateUserAction(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping("deleteUser/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

}
