FROM eclipse-temurin:17-jre-alpine-3.23
COPY target/api-0.0.1-SNAPSHOT.jar /nhd.jar
CMD ["java", "-jar", "nhd.jar"]