FROM openjdk:8
ARG PROJECTS_API=target/*.jar
COPY ${PROJECTS_API} projects-api.jar
ENTRYPOINT ["java", "-jar", "./projects-api.jar"]
