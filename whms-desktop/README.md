# WHMS Desktop — Pallati i Dasmave
### Sistemi i Menaxhimit të Sallave të Dasmave (Wedding Hall Management System)

Aplikacion desktop **vetë-mjaftueshëm** për restorante/sallat e dasmave: menaxhon rezervime,
salla, menu, pagesa, shpenzime dhe planin e uljes — pa MySQL, pa internet, pa server të jashtëm.

- **Backend:** Java 17, Spring Boot 3, Spring Security, Spring Data JPA
- **Frontend:** Thymeleaf, HTML/CSS/JS (vanilla)
- **Bazë të dhënash:** H2 (skedar lokal, integruar — `whms-data/whmsdb.mv.db`)
- **Nisje:** hapet automatikisht në shfletuesin e paracaktuar + ikonë në System Tray

---

## 1. Struktura e projektit

```
whms-desktop/
├── pom.xml
├── src/main/java/mk/pallatidasmave/whms/
│   ├── WhmsApplication.java        (pika hyrëse + hap shfletuesin + System Tray)
│   ├── config/                     (Siguria, të dhëna fillestare, cilësime globale)
│   ├── model/                      (9 entitete: Hall, Menu, Reservation, ...)
│   ├── repository/                 (Spring Data JPA)
│   ├── service/                    (logjika e biznesit)
│   └── controller/                 (MVC + REST për planin e uljes)
└── src/main/resources/
    ├── application.properties
    ├── templates/                  (faqet Thymeleaf, në shqip)
    └── static/                     (css, js)
```

---

## 2. Si ta hapësh dhe testosh (mënyra e zhvillimit)

Kërkohen: **JDK 17+** dhe **Maven** (ose përdor `./mvnw` nëse e shtoni wrapper-in) të instaluara,
si dhe akses interneti **vetëm gjatë ndërtimit** (për të shkarkuar librari nga Maven Central —
pas ndërtimit, aplikacioni punon plotësisht offline).

```bash
cd whms-desktop
mvn clean package
java -jar target/whms.jar
```

Aplikacioni do të:
1. Krijojë automatikisht dosjen `whms-data/` (baza e të dhënave) pranë `whms.jar`
2. Krijojë përdoruesin fillestar: **admin** / **admin123**
3. Hapë vetë shfletuesin te `http://127.0.0.1:8088`
4. Shtojë një ikonë të vogël në System Tray (nëse sistemi operativ e mbështet) me opsion "Mbyll Aplikacionin"

**E rëndësishme:** Ndryshoni fjalëkalimin e `admin` menjëherë pas hyrjes së parë (Cilësimet → Fjalëkalimi).

---

## 3. Si ta paketosh si .exe (Windows) — pa kërkuar Java të instaluar

`jpackage` është pjesë e JDK-së (17+) dhe krijon një instalues **vetë-mjaftueshëm** që
përfshin edhe JRE-në — blerësi nuk ka nevojë të instalojë asgjë tjetër.

### Hapi 1: Ndërto jar-in e ekzekutueshëm
```bash
mvn clean package
```

### Hapi 2: Krijo .exe me jpackage (ekzekuto në Windows, me JDK 17+ të instaluar)
```powershell
jpackage ^
  --input target ^
  --name "PallatiDasmave" ^
  --main-jar whms.jar ^
  --main-class org.springframework.boot.loader.launch.JarLauncher ^
  --type exe ^
  --icon icon.ico ^
  --win-shortcut ^
  --win-menu ^
  --win-dir-chooser ^
  --app-version 1.0.0 ^
  --vendor "Emri Yt / Biznesi Yt" ^
  --description "Sistemi i Menaxhimit te Sallave te Dasmave"
```

> **Shënim:** Nëse `spring-boot-maven-plugin` prodhon një "fat jar" me strukturë `BOOT-INF`,
> main class për versione të reja të Spring Boot 3.2+ është `org.springframework.boot.loader.launch.JarLauncher`
> (për versione më të vjetra: `org.springframework.boot.loader.JarLauncher`). Kontrolloni brenda
> `target/whms.jar` (`unzip -l target/whms.jar | grep Launcher`) nëse hasni gabim gjatë nisjes.

Kjo krijon një skedar `PallatiDasmave-1.0.0.exe` — instalues i vërtetë Windows, me ikonë,
shkurtore në Desktop/Start Menu, dhe **JRE të integruar** (blerësi nuk instalon Java).

### Për macOS (.dmg) — ekzekuto në Mac me JDK 17+
```bash
jpackage \
  --input target \
  --name "PallatiDasmave" \
  --main-jar whms.jar \
  --main-class org.springframework.boot.loader.launch.JarLauncher \
  --type dmg \
  --icon icon.icns \
  --app-version 1.0.0 \
  --vendor "Emri Yt / Biznesi Yt"
```

### Për Linux (.deb) — opsionale
```bash
jpackage --input target --name "PallatiDasmave" --main-jar whms.jar \
  --main-class org.springframework.boot.loader.launch.JarLauncher --type deb
```

**Shënim i rëndësishëm:** `jpackage` ndërton pako specifike për sistemin operativ ku ekzekutohet
(nuk mund të krijosh .exe nga Linux/Mac pa "cross-compilation" të komplikuar). Nëse i shet
klientit një Windows PC, më praktike është të ndërtosh vetë `.exe`-në në një makinë Windows
(ose makinë virtuale Windows) përpara se t'ia dorëzosh.

---

## 4. Çfarë i dorëzon blerësit

Për shitje "një herë, pa kontakt të mëtejshëm", dorëzoji:

1. **Instaluesin** (`PallatiDasmave-1.0.0.exe` ose `.dmg`) — kjo është e vetmja gjë që i duhet për ta përdorur.
2. Një udhëzim i shkurtër (1 faqe) me:
   - Si të instalojë (dopio-klik, "Next, Next, Install")
   - Fjalëkalimi fillestar: `admin` / `admin123` — dhe këshillë ta ndryshojë menjëherë
   - Ku ndodhen të dhënat: dosja `whms-data` (për backup — thjesht kopjoje atë dosje diku tjetër herë pas here)
   - Si të mbyllë aplikacionin: nga ikona në System Tray → "Mbyll Aplikacionin"

**Nuk i duhet të dish MySQL, server, apo internet** — gjithçka punon lokalisht në kompjuterin e blerësit.

---

## 5. Backup dhe rikuperim i të dhënave

Të gjitha të dhënat (rezervime, salla, menu, pagesa, shpenzime) ruhen në 2 skedarë brenda dosjes
`whms-data/` (krijohet automatikisht pranë aplikacionit):
- `whmsdb.mv.db` — vetë baza e të dhënave

Për backup: mjafton të kopjosh periodikisht dosjen `whms-data/` (p.sh. në një USB ose cloud si Google Drive).
Për rikuperim: mbyll aplikacionin, zëvendëso dosjen `whms-data/` me kopjen e ruajtur, hape sërish aplikacionin.

---

## 6. Personalizim përpara shitjes

Përpara se t'ia shesësh një klienti specifik, mund të duash të personalizosh:

- **Emrin e biznesit / logon:** Cilësimet → Profili i Biznesit (brenda vetë aplikacionit, s'kërkon kod)
- **Ikonën e aplikacionit:** zëvendëso `icon.ico` / `icon.icns` para se të nisësh `jpackage`
- **Portin e serverit lokal** (nëse 8088 është i zënë në kompjuterin e klientit): ndrysho
  `server.port` në `application.properties` para ndërtimit
- **Fjalëkalimin fillestar:** ndrysho vlerën `"admin123"` në `DataInitializer.java` para ndërtimit,
  ose thjesht udhëzo klientin ta ndryshojë vetë pas instalimit (rekomandohet)

---

## 7. Siguria — çka duhet ditur

- Fjalëkalimet ruhen të enkriptuara (BCrypt), jo si tekst i thjeshtë.
- Aplikacioni dëgjon vetëm në `127.0.0.1` (localhost) — nuk është i arritshëm nga rrjeti/interneti,
  vetëm nga vetë kompjuteri ku është instaluar.
- CSRF protection është aktiv për të gjitha format dhe thirrjet AJAX.
- Për shumë përdorues me role të ndryshme (të ardhme), struktura e bazës së të dhënave e mbështet
  tashmë (fusha `role` në tabelën `users`), por ndërfaqja aktuale mbështet vetëm 1 administrator.

---

## 8. Zgjerime të mundshme në të ardhmen

Struktura e projektit i mbështet lehtë (pa ristrukturim të madh):
- Role shtesë (Punonjës, Menaxher, Arkëtar, Staf Kuzhine)
- Rezervime online / njoftime me email-SMS (do kërkojë internet, aktualisht jo i përfshirë qëllimisht)
- Raporte dhe eksportime shtesë (Excel, statistika vjetore)
- Menaxhim inventari

---

## 9. Kredencialet fillestare (kujtesë)

| Fusha | Vlera |
|---|---|
| URL | http://127.0.0.1:8088 |
| Përdoruesi | admin |
| Fjalëkalimi | admin123 |

**Ndryshojeni fjalëkalimin menjëherë pas instalimit** — Cilësimet → Fjalëkalimi.
