FROM azul/zulu-openjdk:21
WORKDIR /spring

RUN apt-get update && \
    apt-get install -y openjdk-21-jdk &
    
COPY ./build/libs/*.jar ./server.jar
ENTRYPOINT ["java", "-jar", "server.jar","--debug"]