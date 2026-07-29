package mk.pallatidasmave.whms.controller;

import mk.pallatidasmave.whms.model.AppSettings;
import mk.pallatidasmave.whms.service.SettingsService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("settings", settingsService.get());
        model.addAttribute("activePage", "settings");
        model.addAttribute("settingsTab", "profile");
        return "settings/profile";
    }

    @PostMapping("/profile")
    public String saveProfile(@ModelAttribute AppSettings settings, RedirectAttributes redirect) {
        settingsService.save(settings);
        redirect.addFlashAttribute("successMsg", "Profili i biznesit u perditesua.");
        return "redirect:/settings/profile";
    }

    @GetMapping("/terms")
    public String terms(Model model) {
        model.addAttribute("settings", settingsService.get());
        model.addAttribute("activePage", "settings");
        model.addAttribute("settingsTab", "terms");
        return "settings/terms";
    }

    @PostMapping("/terms")
    public String saveTerms(@RequestParam String terms, RedirectAttributes redirect) {
        AppSettings s = settingsService.get();
        s.setTerms(terms);
        settingsService.save(s);
        redirect.addFlashAttribute("successMsg", "Kushtet e rezervimit u ruajten.");
        return "redirect:/settings/terms";
    }

    @GetMapping("/password")
    public String password(Model model, Authentication auth) {
        model.addAttribute("currentUsername", auth.getName());
        model.addAttribute("activePage", "settings");
        model.addAttribute("settingsTab", "password");
        return "settings/password";
    }

    @PostMapping("/password")
    public String savePassword(@RequestParam(required = false) String newUsername,
                                @RequestParam(required = false) String newPassword,
                                Authentication auth, RedirectAttributes redirect) {
        settingsService.updateAccount(auth.getName(), newUsername, newPassword);
        redirect.addFlashAttribute("successMsg", "Te dhenat e llogarise u perditesuan. Nese keni ndryshuar emrin e perdoruesit, do t'ju duhet te kyceni perseri.");
        return "redirect:/settings/password";
    }
}
