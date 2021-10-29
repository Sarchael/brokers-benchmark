FROM openjdk:15
WORKDIR /
ADD /target/rabbitmq-producer-1.0-jar-with-dependencies.jar rabbitmq-producer.jar
CMD java - jar rabbitmq-producer.jar