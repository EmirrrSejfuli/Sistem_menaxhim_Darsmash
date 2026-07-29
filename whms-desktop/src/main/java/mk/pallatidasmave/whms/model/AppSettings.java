package mk.pallatidasmave.whms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "app_settings")
@Data
@NoArgsConstructor
public class AppSettings {

    @Id
    private Long id = 1L;

    @Column(nullable = false, length = 200)
    private String businessName = "Pallati i Dasmave";

    @Column(length = 300)
    private String address = "";

    @Column(length = 50)
    private String phone = "";

    @Column(length = 150)
    private String email = "";

    @Lob
    @Column(columnDefinition = "CLOB")
    private String terms = "";
}
