FROM gradle:8.12-jdk21 AS build

WORKDIR /app
COPY . .

RUN gradle clean bootJar -x test --no-daemon


FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

ENV PORT=10000
EXPOSE 10000

CMD ["sh", "-c", "java -Dserver.address=0.0.0.0 -Dserver.port=${PORT} -jar app.jar"]
