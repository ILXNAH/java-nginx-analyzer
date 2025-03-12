# Java Nginx Analyzer (Prometheus Metrics Counter)

## 📌 Popis projektu
Tento projekt je jednoduchý analyzátor Nginx logů, který vystavuje Prometheus metriky. 
Aplikace běží na **Java 21** a je možné ji spustit jak lokálně, tak v Docker kontejneru. Metriky jsou dostupné na:

http://localhost:9400/metrics

---

## 🛠 Požadavky
Před spuštěním projektu je nutné mít nainstalované:
- **Java 21** (např. OpenJDK / Temurin)
- **Docker Engine** (např. Docker Desktop)

---

## 🚀 Jak sestavit a spustit aplikaci

### 📍 Lokální spuštění (bez Dockeru)
Otevři terminál v kořenovém adresáři projektu a postupuj následovně:

#### 1️⃣ **Zkompiluj a sestav aplikaci**

```sh
./gradlew build
```

#### 2️⃣ **Spusť aplikaci**

```sh
java -cp "build/libs/*" Main
```

Pokud je spuštění úspěšné, v terminálu se zobrazí:
```arduino
Application is running. Press 'e' to stop.
HTTPServer listening on http://localhost:9400/metrics
```

#### 3️⃣ **Ověř dostupnost metrik**

Linux/macOS:
```sh
wget -q -O - http://localhost:9400/metrics | grep nginxlog_status_group_total
```

Windows (PowerShell):
```powershell
(Invoke-WebRequest -Uri "http://localhost:9400/metrics" -UseBasicParsing).Content -split "`n" | Where-Object {$_ -match "nginxlog_status_group_total" -and $_ -notmatch "# "}
```

### 🐳 Spuštění v Dockeru
Aplikaci je možné zabalit do Docker image a spustit v kontejneru.

#### 1️⃣ Vytvoření Docker image

V hlavním adresáři projektu spusť:
```sh
docker build -t java-nginx-analyzer .
```

📌 Poznámka: 
Build může pár minut trvat, protože Gradle musí stáhnout závislosti a sestavit projekt.

#### 2️⃣ Spuštění aplikace v kontejneru
Po úspěšném buildu spusť kontejner na portu 9400:
```sh
docker run -p 9400:9400 java-nginx-analyzer
```

V terminálu by se mělo zobrazit:
```arduino
Application is running. Press 'e' to stop.
HTTPServer listening on http://localhost:9400/metrics
```

#### 3️⃣ Ověření metrik v Dockeru
Zkontroluj, zda metriky běží:

Linux/macOS:
```sh
wget -q -O - http://localhost:9400/metrics | grep nginxlog_status_group_total
```

Windows (PowerShell):
```powershell
(Invoke-WebRequest -Uri "http://localhost:9400/metrics" -UseBasicParsing).Content -split "`n" | Where-Object {$_ -match "nginxlog_status_group_total" -and $_ -notmatch "# "}
```

---

## 📂 Soubory a konfigurace
### 📝 Dockerfile
Obsahuje instrukce pro sestavení Docker image:
- Použití Eclipse Temurin JDK 21
- Kopírování zdrojového kódu
- Sestavení aplikace pomocí Gradle
- Spuštění aplikace

### 🚫 .dockerignore
Aby se minimalizovala velikost image, ignorují se:
```bash
.git       # Nebude kopírován Git repozitář
build/     # Nebude kopírována složka se zkompilovanými soubory
.gradle/   # Nebude kopírována cache Gradlu
.DS_Store  # Skryté soubory od Finderu (MacOS) nebudou kopírovány
```

### ⚙️ Gradle konfigurace (build.gradle)
#### Použití Shadow pluginu pro sestavení Fat JAR souboru
- Aplikace je distribuována jako Fat JAR, tzn. všechny závislosti jsou zabaleny do jednoho .jar souboru
- Pro sestavení tohoto Fat JAR souboru se používá Shadow plugin
- Konfigurace Main-Class atributu v manifestu umožňuje spustit aplikaci jednoduše pomocí `java -jar`
- Výsledný soubor se nachází v build/libs/java-nginx-analyzer-1.0-SNAPSHOT.jar

#### Definované závislosti:
- Prometheus Metrics – knihovny pro sběr metrik
- SLF4J + Log4J – logging frameworky
- JUnit + Mockito – testovací závislosti

---

## ✅ Shrnutí / Přehled příkazů

| Akce                        | Příkaz |
|:----------------------------|:------------------|
| **Sestavení projektu**      | `./gradlew build` |
| **Spuštění lokálně**        | `java -cp "build/libs/*" Main` |
| **Build Docker image**      | `docker build -t java-nginx-analyzer .` |
| **Spuštění v Dockeru**      | `docker run -p 9400:9400 java-nginx-analyzer` |
| **Ověření metrik (Linux/macOS)** | `wget -q -O - \`<br>`http://localhost:9400/metrics | grep nginxlog_status_group_total` |
| **Ověření metrik (Windows)** | `(Invoke-WebRequest -Uri "http://localhost:9400/metrics" -UseBasicParsing).Content -split "`n" | Where-Object {$_ -match "nginxlog_status_group_total" -and $_ -notmatch "# "}` |