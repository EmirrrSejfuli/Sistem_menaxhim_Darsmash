package mk.pallatidasmave.whms.controller;

import mk.pallatidasmave.whms.model.Hall;
import mk.pallatidasmave.whms.model.SeatingTable;
import mk.pallatidasmave.whms.service.HallService;
import mk.pallatidasmave.whms.service.SeatingService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Endpoint-e REST (JSON) te thirrura nga JavaScript-i i planit te uljes (seating.js)
 * per te shtuar/perditesuar/fshire tavolinat pa ringarkuar faqen (drag & drop).
 */
@RestController
@RequestMapping("/api/seating")
public class SeatingRestController {

    private final SeatingService seatingService;
    private final HallService hallService;

    public SeatingRestController(SeatingService seatingService, HallService hallService) {
        this.seatingService = seatingService;
        this.hallService = hallService;
    }

    @PostMapping("/tables")
    @Transactional
    public Map<String, Object> create(@RequestBody Map<String, Object> body) {
        Hall hall = hallService.findById(Long.valueOf(body.get("hallId").toString()));
        SeatingTable t = new SeatingTable();
        t.setHall(hall);
        t.setName((String) body.getOrDefault("name", "Tavoline"));
        t.setCapacity(toInt(body.get("capacity"), 8));
        t.setShape(SeatingTable.TableShape.valueOf((String) body.getOrDefault("shape", "RRUMBULLAKET")));
        t.setGuests(toInt(body.get("guests"), 0));
        t.setPosX(toDouble(body.get("posX"), 20.0));
        t.setPosY(toDouble(body.get("posY"), 20.0));
        SeatingTable saved = seatingService.save(t);
        return toDto(saved);
    }

    @PutMapping("/tables/{id}")
    @Transactional
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        SeatingTable t = seatingService.findById(id);
        if (body.containsKey("name")) t.setName((String) body.get("name"));
        if (body.containsKey("capacity")) t.setCapacity(toInt(body.get("capacity"), t.getCapacity()));
        if (body.containsKey("shape")) t.setShape(SeatingTable.TableShape.valueOf((String) body.get("shape")));
        if (body.containsKey("guests")) t.setGuests(toInt(body.get("guests"), t.getGuests()));
        if (body.containsKey("posX")) t.setPosX(toDouble(body.get("posX"), t.getPosX()));
        if (body.containsKey("posY")) t.setPosY(toDouble(body.get("posY"), t.getPosY()));
        SeatingTable saved = seatingService.save(t);
        return toDto(saved);
    }

    @DeleteMapping("/tables/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        seatingService.delete(id);
        Map<String, Object> res = new HashMap<>();
        res.put("deleted", true);
        return res;
    }

    private Map<String, Object> toDto(SeatingTable t) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", t.getId());
        dto.put("name", t.getName());
        dto.put("capacity", t.getCapacity());
        dto.put("shape", t.getShape().name());
        dto.put("guests", t.getGuests());
        dto.put("posX", t.getPosX());
        dto.put("posY", t.getPosY());
        dto.put("overCapacity", seatingService.isOverCapacity(t));
        return dto;
    }

    private int toInt(Object o, int def) {
        if (o == null) return def;
        return (int) Double.parseDouble(o.toString());
    }

    private double toDouble(Object o, double def) {
        if (o == null) return def;
        return Double.parseDouble(o.toString());
    }
}
