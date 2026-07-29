package mk.pallatidasmave.whms.service;

import mk.pallatidasmave.whms.model.Hall;
import mk.pallatidasmave.whms.repository.HallRepository;
import mk.pallatidasmave.whms.repository.ReservationRepository;
import mk.pallatidasmave.whms.repository.SeatingTableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HallService {

    private static final int MAX_HALLS = 5;

    private final HallRepository hallRepository;
    private final ReservationRepository reservationRepository;
    private final SeatingTableRepository seatingTableRepository;

    public HallService(HallRepository hallRepository, ReservationRepository reservationRepository,
                        SeatingTableRepository seatingTableRepository) {
        this.hallRepository = hallRepository;
        this.reservationRepository = reservationRepository;
        this.seatingTableRepository = seatingTableRepository;
    }

    public List<Hall> findAll() {
        return hallRepository.findAll();
    }

    public Hall findById(Long id) {
        return hallRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Salla nuk u gjet"));
    }

    public Hall save(Hall hall) {
        if (hall.getId() == null && hallRepository.count() >= MAX_HALLS) {
            throw new IllegalStateException("Numri maksimal i sallave (5) eshte arritur.");
        }
        return hallRepository.save(hall);
    }

    public void delete(Long id) {
        if (reservationRepository.countByHall_Id(id) > 0) {
            throw new IllegalStateException("Kjo salle ka rezervime te lidhura dhe nuk mund te fshihet.");
        }
        seatingTableRepository.findByHall_Id(id).forEach(t -> seatingTableRepository.deleteById(t.getId()));
        hallRepository.deleteById(id);
    }

    public boolean canAddMore() {
        return hallRepository.count() < MAX_HALLS;
    }
}
