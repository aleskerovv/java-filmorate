FROM adoptopenjdk/openjdk11:ubi
#ARG JAR_FILE=target/filmorate-0.0.1-SNAPSHOT.jar
WORKDIR /opt/app
COPY filmorate-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]