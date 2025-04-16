FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp
ARG JAR_FILE=target/discovery-server-*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8761
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]