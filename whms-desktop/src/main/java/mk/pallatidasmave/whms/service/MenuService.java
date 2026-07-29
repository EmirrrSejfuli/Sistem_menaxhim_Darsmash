package mk.pallatidasmave.whms.service;

import mk.pallatidasmave.whms.model.Menu;
import mk.pallatidasmave.whms.repository.MenuRepository;
import mk.pallatidasmave.whms.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final ReservationRepository reservationRepository;

    public MenuService(MenuRepository menuRepository, ReservationRepository reservationRepository) {
        this.menuRepository = menuRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    public List<Menu> findActive() {
        return menuRepository.findAll().stream().filter(Menu::isActive).toList();
    }

    public Menu findById(Long id) {
        return menuRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Menuja nuk u gjet"));
    }

    public Menu save(Menu menu) {
        return menuRepository.save(menu);
    }

    public void delete(Long id) {
        if (reservationRepository.countByMenu_Id(id) > 0) {
            throw new IllegalStateException("Kjo menu eshte perdorur ne rezervime dhe nuk mund te fshihet (te dhenat historike ruhen).");
        }
        menuRepository.deleteById(id);
    }

    public static List<String> linesToList(String text) {
        if (text == null || text.isBlank()) return List.of();
        return List.of(text.split("\\r?\\n")).stream().map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    public static String listToLines(List<String> list) {
        if (list == null) return "";
        return String.join("\n", list);
    }
}
