package mk.pallatidasmave.whms.repository;

import mk.pallatidasmave.whms.model.Hall;
import mk.pallatidasmave.whms.model.SeatingTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatingTableRepository extends JpaRepository<SeatingTable, Long> {
    List<SeatingTable> findByHall(Hall hall);
    List<SeatingTable> findByHall_Id(Long hallId);
}
