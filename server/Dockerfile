FROM openjdk:18.0.1.1-slim
EXPOSE 8001
RUN mkdir /app
WORKDIR /app
ARG jar
ARG keystore
ADD $jar server.jar
RUN mkdir .cert
ADD $keystore .cert/keystore.jks
CMD java -jar server.jar
