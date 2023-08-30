FROM openjdk:11

COPY ./target/practice-apache-camel-0.0.1-SNAPSHOT.jar practice-apache-camel-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/practice-apache-camel-0.0.1-SNAPSHOT.jar"]