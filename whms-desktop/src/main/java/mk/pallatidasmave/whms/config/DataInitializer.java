package mk.pallatidasmave.whms.config;

import mk.pallatidasmave.whms.model.*;
import mk.pallatidasmave.whms.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mbjell te dhena fillestare ne baze te te dhenave kur aplikacioni niset per here te pare
 * (administratori i pare, sallat shembull, tre menute e paracaktuara dhe cilesimet e biznesit).
 * Nese te dhenat ekzistojne tashme (skedari whms-data ekziston), nuk bën asnjë ndryshim.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final HallRepository hallRepository;
    private final MenuRepository menuRepository;
    private final AppSettingsRepository appSettingsRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, HallRepository hallRepository,
                            MenuRepository menuRepository, AppSettingsRepository appSettingsRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.hallRepository = hallRepository;
        this.menuRepository = menuRepository;
        this.appSettingsRepository = appSettingsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedHalls();
        seedMenus();
        seedSettings();
    }

    private void seedAdmin() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            // FJALEKALIMI FILLESTAR: admin123  -- NDRYSHOJENI menjehere pas hyrjes se pare!
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.UserRole.ADMINISTRATOR);
            userRepository.save(admin);
        }
    }

    private void seedHalls() {
        if (hallRepository.count() == 0) {
            Hall h1 = new Hall();
            h1.setName("Salla \"Mbreterore\"");
            h1.setCapacity(300);
            h1.setDescription("Salla kryesore me dritare panoramike dhe skene te madhe.");
            h1.setStatus(Hall.HallStatus.AKTIVE);

            Hall h2 = new Hall();
            h2.setName("Salla \"Kristal\"");
            h2.setCapacity(150);
            h2.setDescription("Sale me e vogel, ideale per fejesa dhe ditelindje.");
            h2.setStatus(Hall.HallStatus.AKTIVE);

            hallRepository.saveAll(List.of(h1, h2));
        }
    }

    private void seedMenus() {
        if (menuRepository.count() == 0) {
            Menu classic = new Menu();
            classic.setName("Klasike");
            classic.setDescription("Menu tradicionale me pjata vendase.");
            classic.setFoods(List.of("Turshi & meze", "Supe pule", "Mish pjekur mikse", "Oriz me perime", "Embelsire shtepie"));
            classic.setBeverages(List.of("Uje", "Lengje", "Vere e kuqe/e bardhe", "Kafe/Caj"));
            classic.setPricePerGuest(new BigDecimal("25.00"));
            classic.setActive(true);

            Menu premium = new Menu();
            premium.setName("Premium");
            premium.setDescription("Menu e zgjeruar me pjata te perzgjedhura.");
            premium.setFoods(List.of("Antipasto italian", "Krem supe kerpudhash", "Salmon i pjekur", "Mish vici ne salce", "Rizoto", "Torte embelsirash"));
            premium.setBeverages(List.of("Uje", "Lengje", "Vere e selektuar", "Prosecco per tost", "Kafe/Caj"));
            premium.setPricePerGuest(new BigDecimal("38.00"));
            premium.setActive(true);

            Menu royal = new Menu();
            royal.setName("Mbreterore");
            royal.setDescription("Menu ekskluzive per eventet me te medha.");
            royal.setFoods(List.of("Bufe i hapur antipasto", "Supe guaske", "Fileto vici", "Karkaleca te grilluar", "Deserte te perziera", "Torte nusërie"));
            royal.setBeverages(List.of("Uje", "Lengje", "Vere premium", "Shampanje", "Kafe/Caj/Espresso bar"));
            royal.setPricePerGuest(new BigDecimal("55.00"));
            royal.setActive(true);

            menuRepository.saveAll(List.of(classic, premium, royal));
        }
    }

    private void seedSettings() {
        if (appSettingsRepository.count() == 0) {
            AppSettings s = new AppSettings();
            s.setId(1L);
            s.setBusinessName("Pallati i Dasmave");
            s.setAddress("Rruga Kryesore nr. 12, Shkup");
            s.setPhone("070 000 000");
            s.setEmail("info@pallatidasmave.mk");
            s.setTerms(
                "Rezervimi konsiderohet i konfirmuar vetem pas pageses se paradhenies prej te pakten 30% te vleres totale.\n" +
                "Anulimi i rezervimit deri ne 30 dite para eventit rikthen 50% te paradhenies. Pas kesaj periudhe paradhenia nuk kthehet.\n" +
                "Numri final i mysafireve duhet konfirmuar te pakten 5 dite para eventit.\n" +
                "Cdo sherbim shtese i kerkuar pas nenshkrimit te kesaj marreveshjeje i nenshtrohet disponueshmerise dhe cmimit ne fuqi ne ate kohe."
            );
            appSettingsRepository.save(s);
        }
    }
}
