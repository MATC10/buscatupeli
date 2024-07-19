package com.matc.buscatupeli.controllers;

import com.matc.buscatupeli.models.User;
import com.matc.buscatupeli.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    UserServiceImpl userServiceImpl;

    //si el usuario está autentificado  puede subir imágen al perfil, si no tiene ninguna imagen se le pone una por defecto.
    @ModelAttribute
    public void addUserImageToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        User user = userServiceImpl.findByEmail(currentUserName);

        if (user != null && user.getImage() != null) {
            model.addAttribute("userImage", "/uploads/" + user.getImage());
        } else {
            model.addAttribute("userImage", "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5f/Volk.svg/235px-Volk.svg.png");
        }
    }

    //comprobar que el usuario ha hecho login.
    @ModelAttribute
    public void userIsLogged(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated()) {
            model.addAttribute("isLogged", true);
        } else {
            model.addAttribute("isLogged", false);
        }
    }

    //para autenticar ciertos elementos de la barra de navegación siendo admin (para que aparezcan los CRUDs)
    @ModelAttribute
    public void isAdmin(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userServiceImpl.findByEmail(authentication.getName());
        boolean isAdmin = false;
        if (currentUser != null) {
            isAdmin = currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        }

        model.addAttribute("isAdmin", isAdmin);
    }
}