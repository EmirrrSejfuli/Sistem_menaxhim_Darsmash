package mk.pallatidasmave.whms.repository;

import mk.pallatidasmave.whms.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByOrderByDateDesc();
    List<Expense> findByDateBetween(LocalDate from, LocalDate to);
}
