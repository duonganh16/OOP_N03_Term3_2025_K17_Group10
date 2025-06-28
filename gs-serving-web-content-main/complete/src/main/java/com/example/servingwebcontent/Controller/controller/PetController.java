package com.petcare.controller;

import org.testng.reporters.jq.Model;

import methods.Pet;

@Controller
@RequestMapping("/pet")
public class PetController {

    @GetMapping("/form")
    public String showForm(Model model) {
        model("pet", new Pet(0, "Chó", "Dog", 2));
        return "pet_form";
    }

    private void model(String string, Pet pet) {
        throw new UnsupportedOperationException("Unimplemented method 'model'");
    }

    @Override
    public String toString() {
        return "PetController []";
    }

    @PostMapping("/validate")
    public String validatePet(@ModelAttribute("pet") Pet pet, Model model) {
        return "pet_result";
    }
}
