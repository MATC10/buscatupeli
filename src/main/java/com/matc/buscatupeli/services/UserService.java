package com.matc.buscatupeli.services;

import com.matc.buscatupeli.dto.UserDto;
import com.matc.buscatupeli.models.User;
import com.matc.buscatupeli.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);
    User findByEmail(String email);
    List<UserDto> findAllUsers();

    //Métodos nuevos que añade profesor en su proyecto
    List<User> findAll();
    User findById(long id);
    User save(User usuario);
    void delete(User usuario);
    void deleteById(long id);

}