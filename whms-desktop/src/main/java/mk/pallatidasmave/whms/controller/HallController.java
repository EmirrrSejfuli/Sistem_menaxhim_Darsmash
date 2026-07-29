package mk.pallatidasmave.whms.controller;

import jakarta.validation.Valid;
import mk.pallatidasmave.whms.model.Hall;
import mk.pallatidasmave.whms.service.HallService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/halls")
public class HallController {

    private final HallService hallService;

    public HallController(HallService hallService) {
        this.hallService = hallService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("halls", hallService.findAll());
        model.addAttribute("canAddMore", hallService.canAddMore());
        model.addAttribute("activePage", "halls");
        return "halls/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("hall", new Hall());
        model.addAttribute("activePage", "halls");
        return "halls/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("hall", hallService.findById(id));
        model.addAttribute("activePage", "halls");
        return "halls/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Hall hall, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("activePage", "halls");
            return "halls/form";
        }
        try {
            hallService.save(hall);
            redirect.addFlashAttribute("successMsg", "Salla u ruajt me sukses.");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/halls";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            hallService.delete(id);
            redirect.addFlashAttribute("successMsg", "Salla u fshi.");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/halls";
    }
}
