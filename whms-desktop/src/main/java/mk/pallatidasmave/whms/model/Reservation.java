package mk.pallatidasmave.whms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Rezervimi eshte funksionaliteti kryesor i aplikacionit.
 * Cdo rezervim trajtohet i pavarur - te dhenat e klientit futen cdo here
 * pa mbajtur nje baze te vecante klientesh (sipas specifikimit te sistemit).
 */
@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String reservationNumber;

    // ---------- Klienti ----------
    @NotBlank(message = "Emri i klientit eshte i detyrueshem")
    @Column(nullable = false, length = 100)
    private String customerFirstName;

    @NotBlank(message = "Mbiemri i klientit eshte i detyrueshem")
    @Column(nullable = false, length = 100)
    private String customerLastName;

    @Column(length = 30)
    private String customerPhone;

    // ---------- Eventi ----------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EventType eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(nullable = false)
    private LocalDate eventDate;

    private LocalTime eventTime;

    @Column(nullable = false)
    private Integer guests;

    @Column(length = 2000)
    private String notes;

    // ---------- Menuja (snapshot per ruajtjen e historikut) ----------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @Column(length = 150)
    private String menuNameSnapshot;

    @Column(length = 1000)
    private String menuDescriptionSnapshot;

    @Column(precision = 10, scale = 2)
    private BigDecimal menuPricePerGuestSnapshot = BigDecimal.ZERO;

    @ElementCollection
    @CollectionTable(name = "reservation_menu_foods", joinColumns = @JoinColumn(name = "reservation_id"))
    @Column(name = "food_item", length = 255)
    private List<String> menuFoodsSnapshot = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "reservation_menu_beverages", joinColumns = @JoinColumn(name = "reservation_id"))
    @Column(name = "beverage_item", length = 255)
    private List<String> menuBeveragesSnapshot = new ArrayList<>();

    // ---------- Financat ----------
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AdditionalService> services = new ArrayList<>();

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    // ---------- Statusi ----------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status = ReservationStatus.NE_PRITJE;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum EventType {
        DASME, FEJESE, DITELINDJE, TJETER
    }

    public enum ReservationStatus {
        NE_PRITJE, KONFIRMUAR, ANULUAR, PERFUNDUAR
    }

    // ---------- Fusha te llogaritura (nuk ruhen ne baze) ----------

    @Transient
    public BigDecimal getBasePrice() {
        BigDecimal price = menuPricePerGuestSnapshot != null ? menuPricePerGuestSnapshot : BigDecimal.ZERO;
        return price.multiply(BigDecimal.valueOf(guests != null ? guests : 0));
    }

    @Transient
    public BigDecimal getServicesTotal() {
        return services.stream()
                .map(AdditionalService::getPrice)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transient
    public BigDecimal getTotalPrice() {
        BigDecimal total = getBasePrice().add(getServicesTotal()).subtract(discount != null ? discount : BigDecimal.ZERO);
        return total.max(BigDecimal.ZERO);
    }

    @Transient
    public BigDecimal getPaidAmount() {
        return payments.stream()
                .map(Payment::getAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transient
    public BigDecimal getRemainingBalance() {
        return getTotalPrice().subtract(getPaidAmount()).max(BigDecimal.ZERO);
    }

    @Transient
    public String getPaymentStatus() {
        BigDecimal paid = getPaidAmount();
        BigDecimal remaining = getRemainingBalance();
        if (paid.compareTo(BigDecimal.ZERO) <= 0) return "E papaguar";
        if (remaining.compareTo(BigDecimal.valueOf(0.01)) > 0) return "Pjeserisht e paguar";
        return "E paguar plotesisht";
    }
}
