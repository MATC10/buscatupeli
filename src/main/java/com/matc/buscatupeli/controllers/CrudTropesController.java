package com.matc.buscatupeli.controllers;

import com.matc.buscatupeli.models.Genre;
import com.matc.buscatupeli.models.Trope;
import com.matc.buscatupeli.services.GenreService;
import com.matc.buscatupeli.services.TropeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/tropes")
public class CrudTropesController {

    @Autowired
    TropeService tropeService;

    @Autowired
    GenreService genreService;


    @GetMapping
    public String getAllTropes(Model model) {
        model.addAttribute("tropes", tropeService.findAll());
        model.addAttribute("listGenres", genreService.findAll());
        return "crud_tropes";
    }

    @PostMapping
    public String createTrope(@Valid Trope trope, BindingResult result, Model model,
                              @RequestParam("genreId") long genreId) {
        if (result.hasErrors()) {
            // Manejar errores
            return "crud_tropes";
        }

        Genre genre = genreService.findById(genreId);
        trope.setGenre(genre);
        tropeService.save(trope);
        return "redirect:/tropes";
    }

    @GetMapping("/genre/{genreId}")
    public String getTropesByGenre(@PathVariable("genreId") long genreId, Model model) {
        Genre genre = new Genre(genreId);
        List<Trope> tropesByGenre = tropeService.findTropeByGenre(genre);
        model.addAttribute("tropes", tropesByGenre);
        return "crud_tropes";
    }

    @GetMapping("/edit/{id}")
    public String editTrope(@PathVariable("id") long id, Model model) {
        Trope trope = tropeService.findById(id);
        model.addAttribute("trope", trope);
        return "crud_tropes";
    }

    @PostMapping("/update/{id}")
    public String updateTrope(@PathVariable("id") long id, @Valid Trope trope,
                              BindingResult result) {
        if (result.hasErrors()) {
            trope.setId(id);
            return "crud_tropes";
        }
        trope.setGenre(tropeService.findById(id).getGenre());
        tropeService.save(trope);
        return "redirect:/tropes";
    }

    @PostMapping("/delete/{id}")
    public String deleteTrope(@PathVariable("id") long id) {
        tropeService.deleteById(id);
        return "redirect:/tropes";
    }

    //Obtenemos nuevos géneros tanto de clichés como de películas desde la API externa
    @GetMapping("/geNewGenres")
    public String getMoviesFromAPI() {
        genreService.insertGenresIntoDatabase();
        return  "redirect:/tropes"; // Asegúrate de devolver la vista correcta
    }

}




