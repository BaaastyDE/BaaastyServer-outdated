FROM gradle:7.6.0-jdk17 AS build

COPY . /home/baaastyserver-build
WORKDIR /home/baaastyserver-build

RUN gradle clean shadowJar

FROM azul/zulu-openjdk:17-jre-headless

RUN mkdir -p /baaastyserver
WORKDIR /baaastyserver
VOLUME /baaastyserver

COPY --from=build /home/baaastyserver-build/build/libs/BaaastyServer.jar .
ENTRYPOINT exec java -jar BaaastyServer.jar \
			SERVER_TOKEN_ADMIN=$SERVER_TOKEN_ADMIN \
			ALGORITHM_SECRET=$ALGORITHM_SECRET \
			MARIADB_HOST=$MARIADB_HOST \
			MARIADB_PORT=$MARIADB_PORT \
			MARIADB_DATABASE=$MARIADB_DATABASE \
			MARIADB_USER=$MARIADB_USER \
			MARIADB_PASSWORD=$MARIADB_PASSWORD \
			MARIADB_MAXPOOLSIZE=$MARIADB_MAXPOOLSIZE \
			MARIADB_MINIDLE=$MARIADB_MINIDLE