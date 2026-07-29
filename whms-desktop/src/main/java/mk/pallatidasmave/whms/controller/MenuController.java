package mk.pallatidasmave.whms.controller;

import mk.pallatidasmave.whms.model.Menu;
import mk.pallatidasmave.whms.service.MenuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("menus", menuService.findAll());
        model.addAttribute("activePage", "menus");
        return "menus/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("menu", new Menu());
        model.addAttribute("foodsText", "");
        model.addAttribute("beveragesText", "");
        model.addAttribute("activePage", "menus");
        return "menus/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Menu menu = menuService.findById(id);
        model.addAttribute("menu", menu);
        model.addAttribute("foodsText", MenuService.listToLines(menu.getFoods()));
        model.addAttribute("beveragesText", MenuService.listToLines(menu.getBeverages()));
        model.addAttribute("activePage", "menus");
        return "menus/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                        @RequestParam String name,
                        @RequestParam(required = false) String description,
                        @RequestParam BigDecimal pricePerGuest,
                        @RequestParam(required = false) String foodsText,
                        @RequestParam(required = false) String beveragesText,
                        @RequestParam(required = false, defaultValue = "false") boolean active,
                        RedirectAttributes redirect) {
        Menu menu = (id != null) ? menuService.findById(id) : new Menu();
        menu.setName(name);
        menu.setDescription(description);
        menu.setPricePerGuest(pricePerGuest);
        menu.setActive(active);
        menu.setFoods(MenuService.linesToList(foodsText));
        menu.setBeverages(MenuService.linesToList(beveragesText));
        menuService.save(menu);
        redirect.addFlashAttribute("successMsg", "Menuja u ruajt me sukses.");
        return "redirect:/menus";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            menuService.delete(id);
            redirect.addFlashAttribute("successMsg", "Menuja u fshi.");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/menus";
    }
}
