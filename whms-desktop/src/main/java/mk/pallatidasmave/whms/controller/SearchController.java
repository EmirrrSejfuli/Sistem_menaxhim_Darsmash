package mk.pallatidasmave.whms.controller;

import mk.pallatidasmave.whms.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {

    private final ReservationService reservationService;

    public SearchController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public String search(@RequestParam(required = false) String firstName,
                          @RequestParam(required = false) String lastName,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate eventDate,
                          Model model) {
        boolean hasQuery = (firstName != null && !firstName.isBlank())
                || (lastName != null && !lastName.isBlank())
                || eventDate != null;
        List<?> results = hasQuery ? reservationService.search(firstName, lastName, eventDate) : List.of();

        model.addAttribute("results", results);
        model.addAttribute("hasQuery", hasQuery);
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("eventDate", eventDate);
        model.addAttribute("activePage", "search");
        return "search/results";
    }
}
