package mk.pallatidasmave.whms.repository;

import mk.pallatidasmave.whms.model.AppSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppSettingsRepository extends JpaRepository<AppSettings, Long> {
}
