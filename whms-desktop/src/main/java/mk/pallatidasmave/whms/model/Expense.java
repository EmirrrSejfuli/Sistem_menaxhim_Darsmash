package mk.pallatidasmave.whms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ExpenseCategory category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Payment.PaymentMethod method;

    @Column(length = 500)
    private String description;

    @Column(length = 255)
    private String attachment;

    public enum ExpenseCategory {
        PAGA_PUNONJESISH, FURNIZIME_USHQIMORE, PIJE, DEKORIM, ENERGJI_ELEKTRIKE,
        UJE, INTERNET, KARBURANT, MATERIALE, MIREMBAJTJE, TE_TJERA
    }
}
