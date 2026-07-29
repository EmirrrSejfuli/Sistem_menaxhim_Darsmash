package mk.pallatidasmave.whms.service;

import mk.pallatidasmave.whms.model.Expense;
import mk.pallatidasmave.whms.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> findAll() {
        return expenseRepository.findAllByOrderByDateDesc();
    }

    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

    public void delete(Long id) {
        expenseRepository.deleteById(id);
    }
}
