package mk.pallatidasmave.whms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seating_tables")
@Data
@NoArgsConstructor
public class SeatingTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TableShape shape = TableShape.RRUMBULLAKET;

    @Column(nullable = false)
    private Double posX = 20.0;

    @Column(nullable = false)
    private Double posY = 20.0;

    @Column(nullable = false)
    private Integer guests = 0;

    public enum TableShape {
        RRUMBULLAKET, KATROR, DREJTKENDOR
    }
}
