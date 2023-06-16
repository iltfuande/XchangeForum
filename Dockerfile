FROM openjdk:17-oracle
COPY ./build/libs/*.jar service.jar
CMD ["/usr/bin/java", "-jar", "/service.jar"]
