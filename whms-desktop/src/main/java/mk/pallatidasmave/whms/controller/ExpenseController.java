package mk.pallatidasmave.whms.controller;

import mk.pallatidasmave.whms.model.Expense;
import mk.pallatidasmave.whms.model.Payment;
import mk.pallatidasmave.whms.service.ExpenseService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("expenses", expenseService.findAll());
        model.addAttribute("categories", Expense.ExpenseCategory.values());
        model.addAttribute("methods", Payment.PaymentMethod.values());
        model.addAttribute("today", LocalDate.now());
        java.math.BigDecimal total = expenseService.findAll().stream()
                .map(Expense::getAmount).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        model.addAttribute("totalExpenses", total);
        model.addAttribute("activePage", "expenses");
        return "expenses/list";
    }

    @PostMapping("/save")
    public String save(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                        @RequestParam Expense.ExpenseCategory category,
                        @RequestParam BigDecimal amount,
                        @RequestParam Payment.PaymentMethod method,
                        @RequestParam(required = false) String description,
                        @RequestParam(required = false) String attachment,
                        RedirectAttributes redirect) {
        Expense e = new Expense();
        e.setDate(date);
        e.setCategory(category);
        e.setAmount(amount);
        e.setMethod(method);
        e.setDescription(description);
        e.setAttachment(attachment);
        expenseService.save(e);
        redirect.addFlashAttribute("successMsg", "Shpenzimi u regjistrua.");
        return "redirect:/expenses";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        expenseService.delete(id);
        redirect.addFlashAttribute("successMsg", "Shpenzimi u fshi.");
        return "redirect:/expenses";
    }
}
