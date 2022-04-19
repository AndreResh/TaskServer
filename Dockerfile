FROM adoptopenjdk/openjdk11:jdk-11.0.1.13-alpine

ADD secure-connect-task.zip .
#RUN ./mvnw package -DskipTests
COPY target/task-server-demo-0.0.1-SNAPSHOT.jar /demo.jar
ENTRYPOINT ["java","-XX:+UseContainerSupport","-Xmx256m", "-Xss512k","-XX:MetaspaceSize=100m", "-jar", "/demo.jar"]

#FROM adoptopenjdk/openjdk11:jdk-11.0.1.13-alpine
#
#ARG JAR_FILE
#
#COPY target/${JAR_FILE} /app.jar
##COPY ./entrypoint.sh /apps/entrypoint.sh
#
##RUN #chmod +x /apps/entrypoint.sh
#ENTRYPOINT ["java", "-jar", "/app.jar"]