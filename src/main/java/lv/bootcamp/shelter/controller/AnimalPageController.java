package lv.bootcamp.shelter.controller;

import lombok.RequiredArgsConstructor;
import lv.bootcamp.shelter.form.AnimalForm;
import lv.bootcamp.shelter.model.AnimalType;
import lv.bootcamp.shelter.service.AnimalService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AnimalPageController {

    private final AnimalService animalService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/animals")
    public String animals(Model model, Authentication authentication) {

        model.addAttribute("animals", animalService.findAll());

        boolean isAdmin = false;
        boolean isUser = false;

        if (authentication != null) {
            isAdmin = authentication.getAuthorities()
                    .stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            isUser = authentication.getAuthorities()
                    .stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
        }

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isUser", isUser);

        return "animals";
    }

    @GetMapping("/animals/new")
    public String newAnimal(Model model, Authentication authentication) {

        boolean isAdmin = false;

        if (authentication != null) {
            isAdmin = authentication.getAuthorities()
                    .stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }

        if (!isAdmin) {
            return "redirect:/";
        }

        model.addAttribute(
                "form",
                new AnimalForm(null, null, null, null, null, null)
        );

        model.addAttribute("types", AnimalType.values());
        model.addAttribute("isAdmin", true);

        return "animals-new";
    }

    @PostMapping("/animals")
    public String createAnimal(@ModelAttribute AnimalForm form) {

        animalService.createFromForm(form);

        return "redirect:/animals";
    }

}