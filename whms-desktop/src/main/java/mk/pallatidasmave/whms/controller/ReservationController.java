package mk.pallatidasmave.whms.controller;

import mk.pallatidasmave.whms.model.*;
import mk.pallatidasmave.whms.service.HallService;
import mk.pallatidasmave.whms.service.MenuService;
import mk.pallatidasmave.whms.service.ReservationService;
import mk.pallatidasmave.whms.service.SettingsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final HallService hallService;
    private final MenuService menuService;
    private final SettingsService settingsService;

    public ReservationController(ReservationService reservationService, HallService hallService,
                                  MenuService menuService, SettingsService settingsService) {
        this.reservationService = reservationService;
        this.hallService = hallService;
        this.menuService = menuService;
        this.settingsService = settingsService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("reservations", reservationService.findAll());
        model.addAttribute("activePage", "reservations");
        return "reservations/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("halls", hallService.findAll());
        model.addAttribute("menus", menuService.findActive());
        model.addAttribute("eventTypes", Reservation.EventType.values());
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("activePage", "reservations");
        return "reservations/form";
    }

    /** Kontroll AJAX per konflikt salle/date, thirret nga forma me JavaScript perpara ruajtjes. */
    @GetMapping("/check-conflict")
    @ResponseBody
    public String checkConflict(@RequestParam Long hallId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Hall hall = hallService.findById(hallId);
        Reservation conflict = reservationService.checkConflict(hall, date);
        if (conflict == null) return "";
        return "Kujdes! Salla \"" + hall.getName() + "\" ka tashme nje rezervim me date " + date +
                " (Rezervimi " + conflict.getReservationNumber() + "). Mund te vazhdoni, por verifikoni orarin.";
    }

    @PostMapping("/save")
    public String create(@RequestParam String customerFirstName,
                          @RequestParam String customerLastName,
                          @RequestParam(required = false) String customerPhone,
                          @RequestParam Reservation.EventType eventType,
                          @RequestParam Long hallId,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate eventDate,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime eventTime,
                          @RequestParam Integer guests,
                          @RequestParam Long menuId,
                          @RequestParam(required = false) String notes,
                          RedirectAttributes redirect) {
        Reservation r = new Reservation();
        r.setCustomerFirstName(customerFirstName);
        r.setCustomerLastName(customerLastName);
        r.setCustomerPhone(customerPhone);
        r.setEventType(eventType);
        r.setHall(hallService.findById(hallId));
        r.setEventDate(eventDate);
        r.setEventTime(eventTime);
        r.setGuests(guests);
        r.setNotes(notes);
        Menu menu = menuService.findById(menuId);
        Reservation saved = reservationService.create(r, menu);
        redirect.addFlashAttribute("successMsg", "Rezervimi " + saved.getReservationNumber() + " u krijua me sukses.");
        return "redirect:/reservations/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("reservation", reservationService.findById(id));
        model.addAttribute("statuses", Reservation.ReservationStatus.values());
        model.addAttribute("paymentMethods", Payment.PaymentMethod.values());
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("activePage", "reservations");
        return "reservations/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("reservation", reservationService.findById(id));
        model.addAttribute("halls", hallService.findAll());
        model.addAttribute("menus", menuService.findActive());
        model.addAttribute("eventTypes", Reservation.EventType.values());
        model.addAttribute("activePage", "reservations");
        return "reservations/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                          @RequestParam String customerFirstName,
                          @RequestParam String customerLastName,
                          @RequestParam(required = false) String customerPhone,
                          @RequestParam Reservation.EventType eventType,
                          @RequestParam Long hallId,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate eventDate,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime eventTime,
                          @RequestParam Integer guests,
                          @RequestParam Long menuId,
                          @RequestParam(required = false) String notes,
                          RedirectAttributes redirect) {
        Reservation r = reservationService.findById(id);
        r.setCustomerFirstName(customerFirstName);
        r.setCustomerLastName(customerLastName);
        r.setCustomerPhone(customerPhone);
        r.setEventType(eventType);
        r.setHall(hallService.findById(hallId));
        r.setEventDate(eventDate);
        r.setEventTime(eventTime);
        r.setGuests(guests);
        r.setNotes(notes);
        boolean menuChanged = r.getMenu() == null || !r.getMenu().getId().equals(menuId);
        Menu menu = menuService.findById(menuId);
        reservationService.update(r, menu, menuChanged);
        redirect.addFlashAttribute("successMsg", "Rezervimi u perditesua.");
        return "redirect:/reservations/" + id;
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam Reservation.ReservationStatus status, RedirectAttributes redirect) {
        reservationService.updateStatus(id, status);
        redirect.addFlashAttribute("successMsg", "Statusi u perditesua.");
        return "redirect:/reservations/" + id;
    }

    @PostMapping("/{id}/discount")
    public String updateDiscount(@PathVariable Long id, @RequestParam BigDecimal discount, RedirectAttributes redirect) {
        reservationService.updateDiscount(id, discount);
        redirect.addFlashAttribute("successMsg", "Zbritja u ruajt.");
        return "redirect:/reservations/" + id;
    }

    @PostMapping("/{id}/services/add")
    public String addService(@PathVariable Long id, @RequestParam String name, @RequestParam BigDecimal price, RedirectAttributes redirect) {
        reservationService.addService(id, name, price);
        redirect.addFlashAttribute("successMsg", "Sherbimi u shtua. Cmimi total u perditesua automatikisht.");
        return "redirect:/reservations/" + id;
    }

    @PostMapping("/{id}/services/{serviceId}/delete")
    public String removeService(@PathVariable Long id, @PathVariable Long serviceId, RedirectAttributes redirect) {
        reservationService.removeService(id, serviceId);
        redirect.addFlashAttribute("successMsg", "Sherbimi u hoq.");
        return "redirect:/reservations/" + id;
    }

    @PostMapping("/{id}/payments/add")
    public String addPayment(@PathVariable Long id,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                              @RequestParam BigDecimal amount,
                              @RequestParam Payment.PaymentMethod method,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false, defaultValue = "false") boolean confirmOverpay,
                              RedirectAttributes redirect) {
        boolean needsConfirmation = reservationService.addPayment(id, date, amount, method, description, confirmOverpay);
        if (needsConfirmation) {
            redirect.addFlashAttribute("errorMsg",
                "Shuma e futur e kalon mbetjen aktuale. Nese jeni te sigurte, shenoni kutine 'Konfirmo tejkalimin' dhe provoni perseri.");
        } else {
            redirect.addFlashAttribute("successMsg", "Pagesa u regjistrua me sukses.");
        }
        return "redirect:/reservations/" + id;
    }

    @PostMapping("/{id}/payments/{paymentId}/delete")
    public String removePayment(@PathVariable Long id, @PathVariable Long paymentId, RedirectAttributes redirect) {
        reservationService.removePayment(id, paymentId);
        redirect.addFlashAttribute("successMsg", "Pagesa u hoq.");
        return "redirect:/reservations/" + id;
    }

    @GetMapping("/{id}/document")
    public String document(@PathVariable Long id, Model model) {
        model.addAttribute("reservation", reservationService.findById(id));
        model.addAttribute("settings", settingsService.get());
        return "reservations/document";
    }
}
