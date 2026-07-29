package mk.pallatidasmave.whms.service;

import mk.pallatidasmave.whms.model.Hall;
import mk.pallatidasmave.whms.model.SeatingTable;
import mk.pallatidasmave.whms.repository.SeatingTableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatingService {

    private final SeatingTableRepository seatingTableRepository;

    public SeatingService(SeatingTableRepository seatingTableRepository) {
        this.seatingTableRepository = seatingTableRepository;
    }

    public List<SeatingTable> findByHall(Hall hall) {
        return seatingTableRepository.findByHall(hall);
    }

    public SeatingTable save(SeatingTable table) {
        return seatingTableRepository.save(table);
    }

    public SeatingTable findById(Long id) {
        return seatingTableRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tavolina nuk u gjet"));
    }

    public void delete(Long id) {
        seatingTableRepository.deleteById(id);
    }

    public boolean isOverCapacity(SeatingTable table) {
        return table.getGuests() != null && table.getCapacity() != null && table.getGuests() > table.getCapacity();
    }
}
