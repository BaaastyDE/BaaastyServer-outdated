FROM gradle:7.6.0-jdk17 AS build

COPY . /home/baaastyserver-build
WORKDIR /home/baaastyserver-build

RUN gradle clean shadowJar

FROM azul/zulu-openjdk:17-jre-headless

RUN mkdir -p /baaastyserver && mkdir -p /launcher
WORKDIR /baaastyserver
VOLUME /baaastyserver

COPY --from=build /home/baaastyserver-build/build/libs/BaaastyServer.jar /launcher
ENTRYPOINT cp /launcher/BaaastyServer.jar /baaastyserver/BaaastyServer.jar && exec java -jar BaaastyServer.jar