FROM openjdk:11
WORKDIR /
ADD /target/brokers-benchmark-1.0-jar-with-dependencies.jar brokers-benchmark.jar
CMD ["java","-jar","brokers-benchmark.jar"]