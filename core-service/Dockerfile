FROM eclipse-temurin:22-jdk

WORKDIR "/campus-policy-rag/backend"

COPY ./mvnw ./
COPY ./.mvn ./.mvn
COPY pom.xml ./

RUN chmod +x mvnw

CMD ["./mvnw", "spring-boot:run"]

EXPOSE 8080