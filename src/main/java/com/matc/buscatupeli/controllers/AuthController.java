package com.matc.buscatupeli.controllers;

import com.matc.buscatupeli.dto.UserDto;
import com.matc.buscatupeli.models.Role;
import com.matc.buscatupeli.models.User;
import com.matc.buscatupeli.repositories.RoleRepository;
import com.matc.buscatupeli.services.UserService;
import com.matc.buscatupeli.services.impl.NotificationServiceImpl;
import com.matc.buscatupeli.services.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private NotificationServiceImpl notificationServiceImpl;

    public AuthController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // handler method to handle user registration request
    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }


    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               Model model)
    {
        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }

        if (userServiceImpl.countUsers() == 0) {
            // If there are no users, set the role of the new user to admin
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN");
            user.setRoles(Arrays.asList(roleAdmin));
        }

        userService.saveUser(user);

        // Después de que el usuario se haya registrado con éxito, envía el correo electrónico
        String to = user.getEmail();
        String subject = "Bienvenido a BuscaTuPeli!";
        String text = "Gracias por registrarte, " + user.getNickname() + "!";

        notificationServiceImpl.sendNotification(to, subject, text);

        return "redirect:/login";
    }



    @GetMapping("/users")
    public String getUsers(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByEmail(authentication.getName());
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("currentUser", currentUser);
        return "users";
    }
    @PostMapping("/delete")
    public String borrar(@RequestParam("userid") Long userid, Model model){
        userService.deleteById(userid);
        return "redirect:/users";
    }

    @PostMapping("/updateUser/{id}")
    public String updateUser(@PathVariable("id") long id, @RequestParam("nickname") String nickname, @RequestParam("email") String email, Model model) {
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            return "error";
        }

        existingUser.setNickname(nickname);
        existingUser.setEmail(email);
        userService.save(existingUser);

        return "redirect:/users";
    }


    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        Random random = new Random();

        User user = userService.findById(id);

        //creamos un random para poner delante del email
        user.setEmail((random.nextInt(9999) + 1000) + "-_" + user.getEmail());
        userService.save(user);
        return "redirect:/users";
    }
}