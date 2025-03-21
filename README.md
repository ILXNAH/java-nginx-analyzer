# Java Nginx Analyzer (Prometheus Metrics Counter)

## Obsah
1. 🎯 [Popis projektu](#popis-projektu)
    - 📌 [Poznámka k implementaci](#poznámka-k-implementaci)
2. 📂 [Obsah repozitáře](#obsah-repozitáře)
3. 🛠 [Požadavky](#požadavky)
4. 🚀 [Jak sestavit a spustit aplikaci](#jak-sestavit-a-spustit-aplikaci)
    - 📍 [Lokální spuštění (bez Dockeru)](#lokální-spuštění-bez-dockeru)
        - 🏗️ [Kompilace a sestavení aplikace](#1️⃣-kompilace-a-sestavení-aplikace)
        - 🏃 [Spuštění aplikace](#2️⃣-spuštění-aplikace)
        - 🔍 [Ověření dostupnosti metrik](#3️⃣-ověření-dostupnosti-metrik)
    - 🐳 [Spuštění v Dockeru](#spuštění-v-dockeru)
        - 🏗️ [Vytvoření Docker image](#1️⃣-vytvoření-docker-image)
        - 🏃 [Spuštění aplikace v kontejneru](#2️⃣-spuštění-aplikace-v-kontejneru)
        - 🔍 [Ověření metrik v Dockeru](#3️⃣-ověření-metrik-v-dockeru)
5. 📂 [Soubory a konfigurace](#soubory-a-konfigurace)
    - 📝 [Dockerfile](#dockerfile)
    - 🚫 [.dockerignore](#dockerignore)
    - ⚙️ [Gradle konfigurace](#gradle-konfigurace-buildgradle)
6. 📋 [Přehled příkazů](#přehled-příkazů)

---

## Popis projektu
Tento projekt je jednoduchý analyzátor Nginx logů, který vystavuje Prometheus metriky. 
Aplikace běží na **Java 21** a je možné ji spustit jak lokálně, tak v Docker kontejneru. Metriky jsou dostupné na: http://localhost:9400/metrics

### Poznámka k implementaci
Tento projekt nebyl vytvořen od základu mnou. Výchozí implementace byla poskytnuta jako součást úlohy v rámci výběrového řízení na DevOps roli. Moje práce spočívala v implementaci požadovaných úprav:
- Úprava zpracování metrik – agregace HTTP kódů do skupin 2xx, 3xx, 4xx, 5xx
- Vytvoření Docker image s kompilací a spuštěním aplikace
- Psaní dokumentace a příprava repozitáře pro snadné sestavení a spuštění

## Obsah repozitáře
```
java-nginx-analyzer/
├── .dockerignore
├── .gitattributes
├── .gitignore
├── Dockerfile
├── README.md
├── build.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
└── src/
    └── main/
        └── java/
            ├── Main.java
            ├── NginxDataReader.java
            └── NginxLogEntry.java
```

## Požadavky
Před spuštěním projektu je nutné mít nainstalované:
- **Java 21** (např. OpenJDK / Temurin)
- **Docker Engine** (např. Docker Desktop)

---

## Jak sestavit a spustit aplikaci

### Lokální spuštění (bez Dockeru)
Otevři terminál v kořenovém adresáři projektu a postupuj následovně:

#### 1️⃣ **Kompilace a sestavení aplikace**

```sh
./gradlew build
```

#### 2️⃣ **Spuštění aplikace**

```sh
java -cp "build/libs/*" Main
```

Pokud je spuštění úspěšné, v terminálu se zobrazí:
```arduino
Application is running. Press 'e' to stop.
HTTPServer listening on http://localhost:9400/metrics
```

#### 3️⃣ **Ověření dostupnosti metrik**

Linux/macOS:
```sh
wget -q -O - http://localhost:9400/metrics | grep nginxlog_status_group_total
```

Windows (PowerShell):
```powershell
(Invoke-WebRequest -Uri "http://localhost:9400/metrics" -UseBasicParsing).Content -split "`n" | Where-Object {$_ -match "nginxlog_status_group_total" -and $_ -notmatch "# "}
```

👉 Alternativně můžeš metriky zobrazit i přímo v prohlížeči: [http://localhost:9400/metrics](http://localhost:9400/metrics)

### Spuštění v Dockeru
Aplikaci je možné zabalit do Docker image a spustit v kontejneru.

#### 1️⃣ Vytvoření Docker image

V hlavním adresáři projektu spusť:
```sh
docker build -t java-nginx-analyzer .
```

Pozn.: 
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

👉 Alternativně můžeš metriky zobrazit i přímo v prohlížeči: [http://localhost:9400/metrics](http://localhost:9400/metrics)

---

## Soubory a konfigurace
### Dockerfile
Obsahuje instrukce pro sestavení Docker image:
- Použití Eclipse Temurin JDK 21
- Kopírování zdrojového kódu
- Sestavení aplikace pomocí Gradle
- Spuštění aplikace

### .dockerignore
Aby se minimalizovala velikost image, ignorují se:
```bash
.git       # Nebude kopírován Git repozitář
build/     # Nebude kopírována složka se zkompilovanými soubory
.gradle/   # Nebude kopírována cache Gradlu
.DS_Store  # Skryté soubory od Finderu (MacOS) nebudou kopírovány
```

### Gradle konfigurace (build.gradle)
#### Použití Shadow pluginu pro sestavení Fat JAR souboru
- Aplikace je distribuována jako Fat JAR, tzn. všechny závislosti jsou zabaleny do jednoho .jar souboru
- Pro sestavení tohoto Fat JAR souboru se používá Shadow plugin
- Konfigurace Main-Class atributu v manifestu umožňuje spustit aplikaci jednoduše pomocí `java -jar`
- Výsledný soubor se nachází v `build/libs/java-nginx-analyzer-1.0-SNAPSHOT.jar`

#### Definované závislosti:
- Prometheus Metrics – knihovny pro sběr metrik
- SLF4J + Log4J – logging frameworky
- JUnit + Mockito – testovací závislosti

---

## Přehled příkazů

| Akce                        | Příkaz |
|:----------------------------|:------------------|
| **Sestavení projektu**      | `./gradlew build` |
| **Spuštění lokálně**        | `java -cp "build/libs/*" Main` |
| **Build Docker image**      | `docker build -t java-nginx-analyzer .` |
| **Spuštění v Dockeru**      | `docker run -p 9400:9400 java-nginx-analyzer` |
| **Ověření metrik (Linux/macOS)** | ```wget -q -O - http://localhost:9400/metrics \| grep nginxlog_status_group_total``` |
| **Ověření metrik (Windows)** | ```(Invoke-WebRequest -Uri "http://localhost:9400/metrics" -UseBasicParsing).Content -split "`n" \| Where-Object {$_ -match "nginxlog_status_group_total" -and $_ -notmatch "# "}``` |