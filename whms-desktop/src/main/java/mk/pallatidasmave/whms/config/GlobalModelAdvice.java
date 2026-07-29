package mk.pallatidasmave.whms.config;

import mk.pallatidasmave.whms.service.SettingsService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Shton automatikisht emrin e biznesit ne modelin e cdo faqeje (perdoret nga fragmenti i sidebar-it),
 * ne menyre qe te mos duhet perseritur ne cdo kontrollues.
 */
@ControllerAdvice
public class GlobalModelAdvice {

    private final SettingsService settingsService;

    public GlobalModelAdvice(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @ModelAttribute("businessName")
    public String businessName() {
        try {
            return settingsService.get().getBusinessName();
        } catch (Exception e) {
            return "Pallati i Dasmave";
        }
    }
}
