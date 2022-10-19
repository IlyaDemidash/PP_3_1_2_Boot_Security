package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepo;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/*
Создание конфигурации для Spring Security (юзеры, пароли, роли...)
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final SuccessUserHandler successUserHandler;
    private UserServiceImpl userService;

    private UserRepo userRepo;


    @Autowired
    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    public WebSecurityConfig(SuccessUserHandler successUserHandler, UserRepo userRepo) {
        this.successUserHandler = successUserHandler;
        this.userRepo = userRepo;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("ADMIN", "USER")
                .anyRequest().authenticated()
                .and()
                .formLogin().successHandler(successUserHandler)
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

    /*
    Преобразование паролей (из текста в хэш)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
    Далее создаем authenticationProvider его задача сказать существует ли такой пользователь и если существует,
    то его нужно положить в SpringSecurityContext. Для этого есть метод:
    setUserDetailsService - будет предоставлять юзеров
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder()); //указываем наш энкодер паролей
        authenticationProvider.setUserDetailsService(userService);
        return authenticationProvider;
    }

    /*
    Создаем пользователей в БД при старте приложения, пароли: 100
     */
    @PostConstruct
    public void addAdminInDB() {
        Role role = new Role("ROLE_ADMIN");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = new User("admin", "$2a$12$vIhRrp5142m0cBytxX8Jb.9yQAKaaxEdAWw/PT5DAeSPllF0/JotG", 33, "admin@mail.ru", roles);
        userRepo.save(user);
        Role role2 = new Role("ROLE_USER");
        Set<Role> roles2 = new HashSet<>();
        roles2.add(role2);
        User user2 = new User("user", "$2a$12$vIhRrp5142m0cBytxX8Jb.9yQAKaaxEdAWw/PT5DAeSPllF0/JotG", 35, "user@mail.ru", roles2);
        userRepo.save(user2);
    }
}