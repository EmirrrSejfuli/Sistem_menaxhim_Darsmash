package mk.pallatidasmave.whms.repository;

import mk.pallatidasmave.whms.model.Hall;
import mk.pallatidasmave.whms.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByOrderByCreatedAtDesc();

    List<Reservation> findByHallAndEventDateAndStatusNot(Hall hall, LocalDate eventDate, Reservation.ReservationStatus excludedStatus);

    List<Reservation> findByCustomerFirstNameContainingIgnoreCaseAndCustomerLastNameContainingIgnoreCaseAndEventDate(
            String firstName, String lastName, LocalDate eventDate);

    List<Reservation> findByCustomerFirstNameContainingIgnoreCaseAndCustomerLastNameContainingIgnoreCase(
            String firstName, String lastName);

    List<Reservation> findByEventDateBetweenAndStatusNot(LocalDate from, LocalDate to, Reservation.ReservationStatus excludedStatus);

    long countByMenu_Id(Long menuId);

    long countByHall_Id(Long hallId);

    boolean existsByReservationNumber(String reservationNumber);
}
