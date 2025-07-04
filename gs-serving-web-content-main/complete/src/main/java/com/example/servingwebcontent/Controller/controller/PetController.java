package com.example.servingwebcontent.Controller.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.servingwebcontent.methods.Pet;

@Controller
@RequestMapping("/Pet")
public class PetController {

    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("pet", new Pet(0, "Chó", "Dog", 2));
        return "pet_form";
    }

    @PostMapping("/validate")
    public String validatePet(@ModelAttribute("pet") Pet pet, Model model) {
        return "pet_result";
    }

    @Override
    public String toString() {
        return "PetController []";
    }
}
