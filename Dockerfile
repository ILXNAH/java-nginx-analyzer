# Používáme oficiální OpenJDK 21 (Temurin) jako základní Docker image
FROM eclipse-temurin:21-jdk

# Nastavíme pracovní adresář uvnitř kontejneru
WORKDIR /app

# Zkopírování zdrojového kódu (ignorujeme soubory dle .dockerignore)
COPY . .

# Ujistíme se, že gradlew má práva na spuštění
RUN chmod +x ./gradlew

# Kompilujeme aplikaci (spustíme Gradle build s cachováním)
RUN ./gradlew build --no-daemon

# Označíme port, na kterém běží aplikace
EXPOSE 9400

# Spustíme aplikaci ze správného shadowJar verzovaného souboru 
CMD ["java", "-jar", "/app/build/libs/java-nginx-analyzer-1.0-SNAPSHOT.jar"]