package com.matc.buscatupeli.controllers;


import com.matc.buscatupeli.models.User;
import com.matc.buscatupeli.services.impl.UserServiceImpl;
import com.matc.buscatupeli.storage.UploadFileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;

@Controller
@SessionAttributes("user")
public class ProfileController {

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    private UploadFileService uploadFileService;

    //entrar al perfil de usuario
    @GetMapping("/perfil")
    public String userProfile(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        User user = userServiceImpl.findByEmail(currentUserName);

        if (user != null) {
            model.addAttribute("user", user);
        } else {
            System.out.println("User not found: " + currentUserName);
            return "error";
        }

        return "profile_user";
    }

    //subir imágenes
    @PostMapping({ "/subirarchivo" })
    public String crear(@Valid User user,
                        @RequestParam("file") MultipartFile image, SessionStatus status) {

        String uniqueFilename = null;

        if (!image.isEmpty()) {
            //si el usuario ya tiene una imagen se elimina
            if (user.getId() != null && user.getId() > 0 && user.getImage() != null
                    && user.getImage().length() > 0) {
                uploadFileService.delete(user.getImage());

            }
            //se sube la nueva imagen
            try {
                uniqueFilename = uploadFileService.copy(image);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            user.setImage(uniqueFilename);
        }


        userServiceImpl.save(user);
        //limpiamos los atributos user
        status.setComplete();
        return "redirect:/perfil";
    }

    //se carga la imagen para verla
    @GetMapping(value = "/uploads/{filename:.+}")
    public ResponseEntity<Resource> verFoto(@PathVariable String filename) {

        Resource recurso = null;
        try {
            recurso = uploadFileService.load(filename);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }


}
