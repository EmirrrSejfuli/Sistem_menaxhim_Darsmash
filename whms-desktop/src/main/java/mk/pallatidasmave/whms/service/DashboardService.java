package mk.pallatidasmave.whms.service;

import mk.pallatidasmave.whms.model.*;
import mk.pallatidasmave.whms.repository.ExpenseRepository;
import mk.pallatidasmave.whms.repository.HallRepository;
import mk.pallatidasmave.whms.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Mbledh statistikat qe shfaqen ne Panelin kryesor: eventet e sotme/javore,
 * te ardhurat/shpenzimet/fitimin mujor, pagesat ne pritje, menune/sallen me te zgjedhur,
 * dhe grafikun e te ardhurave per 6 muajt e fundit.
 */
@Service
public class DashboardService {

    private final ReservationRepository reservationRepository;
    private final ExpenseRepository expenseRepository;
    private final HallRepository hallRepository;

    public DashboardService(ReservationRepository reservationRepository, ExpenseRepository expenseRepository,
                             HallRepository hallRepository) {
        this.reservationRepository = reservationRepository;
        this.expenseRepository = expenseRepository;
        this.hallRepository = hallRepository;
    }

    public Map<String, Object> buildDashboardModel() {
        Map<String, Object> model = new HashMap<>();
        LocalDate today = LocalDate.now();
        YearMonth ym = YearMonth.from(today);
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<Reservation> active = reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() != Reservation.ReservationStatus.ANULUAR)
                .collect(Collectors.toList());

        long todaysCount = active.stream().filter(r -> today.equals(r.getEventDate())).count();
        long weeklyCount = active.stream().filter(r -> r.getEventDate() != null
                && !r.getEventDate().isBefore(startOfWeek) && !r.getEventDate().isAfter(endOfWeek)).count();
        long monthlyCount = active.stream().filter(r -> r.getEventDate() != null && YearMonth.from(r.getEventDate()).equals(ym)).count();

        BigDecimal monthlyRevenue = BigDecimal.ZERO;
        for (Reservation r : reservationRepository.findAll()) {
            for (Payment p : r.getPayments()) {
                if (p.getDate() != null && YearMonth.from(p.getDate()).equals(ym)) {
                    monthlyRevenue = monthlyRevenue.add(p.getAmount());
                }
            }
        }
        BigDecimal monthlyExpenses = expenseRepository.findAll().stream()
                .filter(e -> e.getDate() != null && YearMonth.from(e.getDate()).equals(ym))
                .map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal netProfit = monthlyRevenue.subtract(monthlyExpenses);

        BigDecimal pendingTotal = active.stream().map(Reservation::getRemainingBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalReceived = reservationRepository.findAll().stream().map(Reservation::getPaidAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        long availableHalls = hallRepository.findAll().stream().filter(h -> h.getStatus() == Hall.HallStatus.AKTIVE).count();

        Map<String, Long> menuCounts = active.stream()
                .filter(r -> r.getMenuNameSnapshot() != null)
                .collect(Collectors.groupingBy(Reservation::getMenuNameSnapshot, Collectors.counting()));
        String topMenu = menuCounts.entrySet().stream().max(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + " (" + e.getValue() + ")").orElse("—");

        Map<Long, Long> hallCounts = active.stream()
                .filter(r -> r.getHall() != null)
                .collect(Collectors.groupingBy(r -> r.getHall().getId(), Collectors.counting()));
        String topHall = hallCounts.entrySet().stream().max(Map.Entry.comparingByValue())
                .map(e -> hallRepository.findById(e.getKey()).map(Hall::getName).orElse("—"))
                .orElse("—");

        List<Map<String, Object>> chart = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth m = ym.minusMonths(i);
            BigDecimal rev = BigDecimal.ZERO;
            for (Reservation r : reservationRepository.findAll()) {
                for (Payment p : r.getPayments()) {
                    if (p.getDate() != null && YearMonth.from(p.getDate()).equals(m)) {
                        rev = rev.add(p.getAmount());
                    }
                }
            }
            Map<String, Object> point = new HashMap<>();
            point.put("label", m.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, new Locale("sq")));
            point.put("value", rev);
            chart.add(point);
        }
        BigDecimal maxChartValue = chart.stream()
                .map(p -> (BigDecimal) p.get("value"))
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ONE);
        if (maxChartValue.compareTo(BigDecimal.ZERO) <= 0) maxChartValue = BigDecimal.ONE;

        for (Map<String, Object> point : chart) {
            BigDecimal v = (BigDecimal) point.get("value");
            int percent = v.multiply(BigDecimal.valueOf(100))
                    .divide(maxChartValue, 0, java.math.RoundingMode.HALF_UP)
                    .max(BigDecimal.valueOf(4))
                    .intValue();
            point.put("percent", percent);
        }

        List<Reservation> recent = reservationRepository.findAllByOrderByCreatedAtDesc().stream().limit(6).toList();

        model.put("todaysCount", todaysCount);
        model.put("weeklyCount", weeklyCount);
        model.put("monthlyCount", monthlyCount);
        model.put("monthlyRevenue", monthlyRevenue);
        model.put("monthlyExpenses", monthlyExpenses);
        model.put("netProfit", netProfit);
        model.put("pendingTotal", pendingTotal);
        model.put("totalReceived", totalReceived);
        model.put("availableHalls", availableHalls);
        model.put("totalHalls", hallRepository.count());
        model.put("topMenu", topMenu);
        model.put("topHall", topHall);
        model.put("chart", chart);
        model.put("maxChartValue", maxChartValue);
        model.put("recent", recent);
        model.put("totalReservations", reservationRepository.count());
        return model;
    }
}
