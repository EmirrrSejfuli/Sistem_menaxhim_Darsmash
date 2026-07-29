package mk.pallatidasmave.whms.service;

import mk.pallatidasmave.whms.model.*;
import mk.pallatidasmave.whms.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAllByOrderByCreatedAtDesc();
    }

    public Reservation findById(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Rezervimi nuk u gjet"));
    }

    /**
     * Kontrollon nese salla e zgjedhur eshte tashme e rezervuar per te njejten date.
     * Sistemi NUK bllokon rezervimin - vetem paralajmeron administratorin.
     */
    public Reservation checkConflict(Hall hall, LocalDate eventDate) {
        List<Reservation> conflicts = reservationRepository.findByHallAndEventDateAndStatusNot(
                hall, eventDate, Reservation.ReservationStatus.ANULUAR);
        return conflicts.isEmpty() ? null : conflicts.get(0);
    }

    @Transactional
    public Reservation create(Reservation reservation, Menu menu) {
        reservation.setReservationNumber(generateReservationNumber());
        reservation.setCreatedAt(LocalDateTime.now());
        applyMenuSnapshot(reservation, menu);
        if (reservation.getStatus() == null) {
            reservation.setStatus(Reservation.ReservationStatus.NE_PRITJE);
        }
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation update(Reservation reservation, Menu newMenu, boolean menuChanged) {
        if (menuChanged) {
            applyMenuSnapshot(reservation, newMenu);
        }
        return reservationRepository.save(reservation);
    }

    /** "Kopjon" te dhenat e menuse ne rezervim, ne menyre qe ndryshimet e ardhshme te menuse te mos ndikojne historikun. */
    public void applyMenuSnapshot(Reservation reservation, Menu menu) {
        reservation.setMenu(menu);
        if (menu != null) {
            reservation.setMenuNameSnapshot(menu.getName());
            reservation.setMenuDescriptionSnapshot(menu.getDescription());
            reservation.setMenuPricePerGuestSnapshot(menu.getPricePerGuest());
            // E RENDESISHME: krijojme LISTA TE REJA (kopje), jo te njejtin objekt/koleksion te Hibernate-it
            // te lidhur me Menu-ne. Nese perdorim direkt menu.getFoods(), Hibernate hutohet sepse i njejti
            // koleksion "i menaxhuar" perpiqet t'i perkase dy entiteteve njekohesisht (gabim gjate ruajtjes).
            reservation.setMenuFoodsSnapshot(new java.util.ArrayList<>(menu.getFoods()));
            reservation.setMenuBeveragesSnapshot(new java.util.ArrayList<>(menu.getBeverages()));
        }
    }

    @Transactional
    public void updateStatus(Long reservationId, Reservation.ReservationStatus status) {
        Reservation r = findById(reservationId);
        r.setStatus(status);
        reservationRepository.save(r);
    }

    @Transactional
    public void updateDiscount(Long reservationId, BigDecimal discount) {
        Reservation r = findById(reservationId);
        r.setDiscount(discount != null ? discount : BigDecimal.ZERO);
        reservationRepository.save(r);
    }

    @Transactional
    public void addService(Long reservationId, String name, BigDecimal price) {
        Reservation r = findById(reservationId);
        AdditionalService s = new AdditionalService();
        s.setReservation(r);
        s.setName(name);
        s.setPrice(price != null ? price : BigDecimal.ZERO);
        r.getServices().add(s);
        reservationRepository.save(r);
    }

    @Transactional
    public void removeService(Long reservationId, Long serviceId) {
        Reservation r = findById(reservationId);
        r.getServices().removeIf(s -> s.getId().equals(serviceId));
        reservationRepository.save(r);
    }

    /**
     * Regjistron nje pagese. Kthen true nese shuma e kalon mbetjen (per te shfaqur paralajmerim ne UI
     * perpara konfirmimit perfundimtar).
     */
    @Transactional
    public boolean addPayment(Long reservationId, LocalDate date, BigDecimal amount, Payment.PaymentMethod method, String description, boolean forceOverpay) {
        Reservation r = findById(reservationId);
        BigDecimal remaining = r.getRemainingBalance();
        if (amount.compareTo(remaining) > 0 && !forceOverpay) {
            return true; // kerkon konfirmim shtese
        }
        Payment p = new Payment();
        p.setReservation(r);
        p.setDate(date);
        p.setAmount(amount);
        p.setMethod(method);
        p.setDescription(description);
        r.getPayments().add(p);
        reservationRepository.save(r);
        return false;
    }

    @Transactional
    public void removePayment(Long reservationId, Long paymentId) {
        Reservation r = findById(reservationId);
        r.getPayments().removeIf(p -> p.getId().equals(paymentId));
        reservationRepository.save(r);
    }

    public List<Reservation> search(String firstName, String lastName, LocalDate date) {
        String f = firstName == null ? "" : firstName;
        String l = lastName == null ? "" : lastName;
        if (date != null) {
            return reservationRepository.findByCustomerFirstNameContainingIgnoreCaseAndCustomerLastNameContainingIgnoreCaseAndEventDate(f, l, date);
        }
        return reservationRepository.findByCustomerFirstNameContainingIgnoreCaseAndCustomerLastNameContainingIgnoreCase(f, l);
    }

    /** Gjeneron numra rezervimi unike ne formen R-0001, R-0002, ... */
    private synchronized String generateReservationNumber() {
        long seq = reservationRepository.count() + 1;
        String candidate;
        do {
            candidate = String.format("R-%04d", seq);
            seq++;
        } while (reservationRepository.existsByReservationNumber(candidate));
        return candidate;
    }
}
