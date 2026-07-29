package mk.pallatidasmave.whms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menus")
@Data
@NoArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Emri i menuse eshte i detyrueshem")
    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 1000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "menu_foods", joinColumns = @JoinColumn(name = "menu_id"))
    @Column(name = "food_item", length = 255)
    private List<String> foods = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "menu_beverages", joinColumns = @JoinColumn(name = "menu_id"))
    @Column(name = "beverage_item", length = 255)
    private List<String> beverages = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerGuest = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean active = true;
}
