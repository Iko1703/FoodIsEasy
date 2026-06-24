# Frontend (встраивается в Spring static — один URL :8080)
FROM node:22-alpine AS frontend-build
WORKDIR /fe
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ .
ENV VITE_API_URL=
RUN npm run build

# Backend
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY FoodIsEasy/.mvn/settings.xml /root/.m2/settings.xml
COPY FoodIsEasy/pom.xml .
COPY FoodIsEasy/src ./src
RUN rm -rf ./src/main/resources/static/*
COPY --from=frontend-build /fe/dist ./src/main/resources/static/
RUN mvn -B package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
