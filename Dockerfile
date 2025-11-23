# Étape 1 : image Maven pour build / run
FROM maven:3.9.5-eclipse-temurin-21 AS dev

# Définir le dossier de travail dans le conteneur
WORKDIR /app

# Copier le pom pour pré-télécharger les dépendances
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copier le code source du module order-service
COPY src ./src

# Exposer le port du microservice
EXPOSE 8083

# Commande pour lancer l'application
CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.fork=false"]
