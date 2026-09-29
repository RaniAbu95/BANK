# שלב בנייה: מקמפל את השרת לקובץ jar
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package -DskipTests

# שלב הרצה: JRE בלבד, כדי שהתמונה תהיה קטנה
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# המסלול החינמי ב-Render נותן 512MB — מגבילים את ה-heap כדי לא לחרוג
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k"
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
