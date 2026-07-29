package mk.pallatidasmave.whms.controller;

import mk.pallatidasmave.whms.model.Hall;
import mk.pallatidasmave.whms.service.HallService;
import mk.pallatidasmave.whms.service.SeatingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/seating")
public class SeatingController {

    private final HallService hallService;
    private final SeatingService seatingService;

    public SeatingController(HallService hallService, SeatingService seatingService) {
        this.hallService = hallService;
        this.seatingService = seatingService;
    }

    @GetMapping
    public String plan(@RequestParam(required = false) Long hallId, Model model) {
        List<Hall> halls = hallService.findAll();
        Hall selected = hallId != null ? hallService.findById(hallId) : (halls.isEmpty() ? null : halls.get(0));

        model.addAttribute("halls", halls);
        model.addAttribute("selectedHall", selected);
        model.addAttribute("tables", selected != null ? seatingService.findByHall(selected) : List.of());
        model.addAttribute("activePage", "seating");
        return "seating/plan";
    }
}
