package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepo;
import ru.kata.spring.boot_security.demo.repositories.UserRepo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
Задача этого сервиса по имени пользователя предоставить юзера. И так как этому сервису нужен будет доступ к БД
для получения юзера в нем инжектим репо юзера. Так же основные операции.
 */
@Service
public class UserServiceImpl implements UserDetailsService, UserService {
    private UserRepo userRepo;
    private RoleRepo roleRepo;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
    }

    //Как ищем пользователя
    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    /*
    По имени пользователя возвращаем юзера, но уже в формате UserDetails.
    Делаем запрос в БД по имени. Грубо говоря показываем спрингу (AuthenticationProvider) как получить User в формате UserDetails.
    Реализация метода зависит от того где лежат наши пользователи, как мы их будем получать в данном случае из БД
    */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = Optional.ofNullable(findByUsername(username));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
        /*
        Если нашли, то его нужно преобразовать к UserDetails, создаем юзера спрингового и передаем ему
        имя нашего пользователя, полученный пароль и коллекцию GrantedAuthorities.
         */
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
        }
       else throw new UsernameNotFoundException("User with username: " + username + "not found!");
    }
    //метод из коллекции ролей получает коллекцию прав доступа GrantedAuthorities
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }

    @Override
    public List<User> findAllUsers() {

        return userRepo.findAll();
    }

    @Override
    public User findById(Long id) {

        return userRepo.findById(id).orElse(null);
    }

    @Override
    public User saveUser(User user) {

        return userRepo.save(user);
    }

    @Override
    public void deleteUser(Long id) {

        userRepo.deleteById(id);
    }

    @Override
    public List<Role> roleList() {
        return roleRepo.findAll();
    }

}
