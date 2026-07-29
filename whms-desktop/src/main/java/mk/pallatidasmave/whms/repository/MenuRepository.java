package mk.pallatidasmave.whms.repository;

import mk.pallatidasmave.whms.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
