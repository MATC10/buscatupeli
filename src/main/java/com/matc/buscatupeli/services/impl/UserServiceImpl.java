package com.matc.buscatupeli.services.impl;

import com.matc.buscatupeli.dto.UserDto;
import com.matc.buscatupeli.models.Role;
import com.matc.buscatupeli.models.User;
import com.matc.buscatupeli.repositories.RoleRepository;
import com.matc.buscatupeli.repositories.UserRepository;
import com.matc.buscatupeli.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setNickname(userDto.getNickname());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN");
        Role roleUser = roleRepository.findByName("ROLE_USER");

        if(roleAdmin == null){
            roleAdmin = new Role();
            roleAdmin.setName("ROLE_ADMIN");
            roleRepository.save(roleAdmin);
            user.setRoles(Arrays.asList(roleAdmin));
        } else if(roleUser == null){
            roleUser = new Role();
            roleUser.setName("ROLE_USER");
            roleRepository.save(roleUser);
            user.setRoles(Arrays.asList(roleUser));
        } else {
            user.setRoles(Arrays.asList(roleUser));
        }

        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }



    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map((user) -> convertEntityToDto(user))
                .collect(Collectors.toList());
    }

    private UserDto convertEntityToDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setNickname(user.getNickname());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        return roleRepository.save(role);
    }

    //Métodos nuevos
    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User findById(long id){
        return userRepository.findById(id);
    }

    public User findByNickname(String username){return userRepository.findByNickname(username);}

    public User save(User usuario){
        userRepository.save(usuario);
        return usuario;
    }

    public void delete(User usuario){
        userRepository.delete(usuario);
    }

    public void deleteById(long id){
        userRepository.deleteById(id);
    }


    public long countUsers() {
        return userRepository.count();
    }



}