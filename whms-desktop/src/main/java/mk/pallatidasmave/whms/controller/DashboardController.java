package mk.pallatidasmave.whms.controller;

import mk.pallatidasmave.whms.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAllAttributes(dashboardService.buildDashboardModel());
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }
}
