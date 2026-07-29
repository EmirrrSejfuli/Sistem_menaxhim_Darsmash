package mk.pallatidasmave.whms.repository;

import mk.pallatidasmave.whms.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByDateBetween(LocalDate from, LocalDate to);
}
