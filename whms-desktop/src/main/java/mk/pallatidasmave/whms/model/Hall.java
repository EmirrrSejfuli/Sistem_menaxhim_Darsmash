package mk.pallatidasmave.whms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "halls")
@Data
@NoArgsConstructor
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Emri i salles eshte i detyrueshem")
    @Column(nullable = false, length = 150)
    private String name;

    @Positive(message = "Kapaciteti duhet te jete numer pozitiv")
    @Column(nullable = false)
    private Integer capacity;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HallStatus status = HallStatus.AKTIVE;

    public enum HallStatus {
        AKTIVE, JOAKTIVE
    }
}
