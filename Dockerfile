FROM adoptopenjdk:11-jre-hotspot
ADD secure-connect-task.zip .
COPY target/TaskServer-0.0.1-SNAPSHOT.jar /demo.jar
ENTRYPOINT ["java", "-jar", "/demo.jar"]
