package mk.pallatidasmave.whms.service;

import mk.pallatidasmave.whms.model.AppSettings;
import mk.pallatidasmave.whms.model.User;
import mk.pallatidasmave.whms.repository.AppSettingsRepository;
import mk.pallatidasmave.whms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    private final AppSettingsRepository appSettingsRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SettingsService(AppSettingsRepository appSettingsRepository, UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {
        this.appSettingsRepository = appSettingsRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppSettings get() {
        return appSettingsRepository.findById(1L).orElseGet(() -> {
            AppSettings s = new AppSettings();
            s.setId(1L);
            return appSettingsRepository.save(s);
        });
    }

    public AppSettings save(AppSettings settings) {
        settings.setId(1L);
        return appSettingsRepository.save(settings);
    }

    public void updateAccount(String username, String newUsername, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Perdoruesi nuk u gjet"));
        if (newUsername != null && !newUsername.isBlank()) {
            user.setUsername(newUsername.trim());
        }
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        userRepository.save(user);
    }
}
