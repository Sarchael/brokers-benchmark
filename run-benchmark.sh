#!/bin/bash

# Uruchamianie środowiska dla benchmarku brokera RabbitMQ
cd /usr/local/dockerfiles/benchmark/rabbitmq
docker-compose up -d --force-recreate

# Oczekiwanie na uruchomienie aplikacji w kontenerach
sleep 60

# Uruchamianie benchmarku brokera RabbitMQ dla różnych parametrów
cd /usr/local/jarfiles/benchmark
echo "================================== RABBITMQ 01/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 1 -c 1 -s 512 -t 120
echo "================================== RABBITMQ 02/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 2 -c 2 -s 512 -t 120
echo "================================== RABBITMQ 03/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 3 -c 3 -s 512 -t 120
echo "================================== RABBITMQ 04/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 4 -c 4 -s 512 -t 120
echo "================================== RABBITMQ 05/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 5 -c 5 -s 512 -t 120
echo "================================== RABBITMQ 06/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 7 -c 7 -s 512 -t 120
echo "================================== RABBITMQ 07/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 10 -c 10 -s 512 -t 120
echo "================================== RABBITMQ 08/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 2 -p 2 -c 2 -s 512 -t 120
echo "================================== RABBITMQ 09/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 2 -p 4 -c 4 -s 512 -t 120
echo "================================== RABBITMQ 10/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 2 -p 6 -c 6 -s 512 -t 120
echo "================================== RABBITMQ 11/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 2 -p 8 -c 8 -s 512 -t 120
echo "================================== RABBITMQ 12/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 3 -p 3 -c 3 -s 512 -t 120
echo "================================== RABBITMQ 13/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 3 -p 6 -c 6 -s 512 -t 120
echo "================================== RABBITMQ 14/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 3 -p 9 -c 9 -s 512 -t 120
echo "================================== RABBITMQ 15/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 3 -p 12 -c 12 -s 512 -t 120
echo "================================== RABBITMQ 16/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 1 -c 1 -s 128 -t 120
echo "================================== RABBITMQ 17/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 2 -c 2 -s 128 -t 120
echo "================================== RABBITMQ 18/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 3 -c 3 -s 128 -t 120
echo "================================== RABBITMQ 19/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 4 -c 4 -s 128 -t 120
echo "================================== RABBITMQ 20/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 5 -c 5 -s 128 -t 120
echo "================================== RABBITMQ 21/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 7 -c 7 -s 128 -t 120
echo "================================== RABBITMQ 22/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 10 -c 10 -s 128 -t 120
echo "================================== RABBITMQ 23/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 2 -p 2 -c 2 -s 128 -t 120
echo "================================== RABBITMQ 24/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 2 -p 4 -c 4 -s 128 -t 120
echo "================================== RABBITMQ 25/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 2 -p 6 -c 6 -s 128 -t 120
echo "================================== RABBITMQ 26/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 2 -p 8 -c 8 -s 128 -t 120
echo "================================== RABBITMQ 27/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 3 -p 3 -c 3 -s 128 -t 120
echo "================================== RABBITMQ 28/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 3 -p 6 -c 6 -s 128 -t 120
echo "================================== RABBITMQ 29/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 3 -p 9 -c 9 -s 128 -t 120
echo "================================== RABBITMQ 30/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 3 -p 12 -c 12 -s 128 -t 120
echo "================================== RABBITMQ 31/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 1 -c 1 -s 1024 -t 120
echo "================================== RABBITMQ 32/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 2 -c 2 -s 1024 -t 120
echo "================================== RABBITMQ 33/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 3 -c 3 -s 1024 -t 120
echo "================================== RABBITMQ 34/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 4 -c 4 -s 1024 -t 120
echo "================================== RABBITMQ 35/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 5 -c 5 -s 1024 -t 120
echo "================================== RABBITMQ 36/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 7 -c 7 -s 1024 -t 120
echo "================================== RABBITMQ 37/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 1 -p 10 -c 10 -s 1024 -t 120
echo "================================== RABBITMQ 38/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 2 -p 2 -c 2 -s 1024 -t 120
echo "================================== RABBITMQ 39/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 2 -p 4 -c 4 -s 1024 -t 120
echo "================================== RABBITMQ 40/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 2 -p 6 -c 6 -s 1024 -t 120
echo "================================== RABBITMQ 41/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 2 -p 8 -c 8 -s 1024 -t 120
echo "================================== RABBITMQ 42/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 3 -p 3 -c 3 -s 1024 -t 120
echo "================================== RABBITMQ 43/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 3 -p 6 -c 6 -s 1024 -t 120
echo "================================== RABBITMQ 44/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 3 -p 9 -c 9 -s 1024 -t 120
echo "================================== RABBITMQ 45/45 =================================="
java -jar benchmark.jar -b RABBITMQ -l true -q 3 -p 12 -c 12 -s 1024 -t 120

# Wyłączanie środowiska dla benchmarku brokera RabbitMQ
cd /usr/local/dockerfiles/benchmark/rabbitmq
docker-compose down

# Uruchamianie środowiska dla benchmarku brokera Apache Kafka
cd /usr/local/dockerfiles/benchmark/kafka
docker-compose up -d --force-recreate

# Oczekiwanie na uruchomienie aplikacji w kontenerach
sleep 60

# Uruchamianie benchmarku brokera Apache Kafka dla różnych parametrów
cd /usr/local/jarfiles/benchmark
echo "================================== KAFKA 01/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 1 -c 1 -s 512 -t 120
echo "================================== KAFKA 02/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 2 -c 2 -s 512 -t 120
echo "================================== KAFKA 03/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 3 -c 3 -s 512 -t 120
echo "================================== KAFKA 04/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 4 -c 4 -s 512 -t 120
echo "================================== KAFKA 05/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 5 -c 5 -s 512 -t 120
echo "================================== KAFKA 06/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 7 -c 7 -s 512 -t 120
echo "================================== KAFKA 07/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 10 -c 10 -s 512 -t 120
echo "================================== KAFKA 08/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 2 -p 2 -c 2 -s 512 -t 120
echo "================================== KAFKA 09/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 2 -p 4 -c 4 -s 512 -t 120
echo "================================== KAFKA 10/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 2 -p 6 -c 6 -s 512 -t 120
echo "================================== KAFKA 11/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 2 -p 8 -c 8 -s 512 -t 120
echo "================================== KAFKA 12/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 3 -p 3 -c 3 -s 512 -t 120
echo "================================== KAFKA 13/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 3 -p 6 -c 6 -s 512 -t 120
echo "================================== KAFKA 14/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 3 -p 9 -c 9 -s 512 -t 120
echo "================================== KAFKA 15/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 3 -p 12 -c 12 -s 512 -t 120
echo "================================== KAFKA 16/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 1 -c 1 -s 128 -t 120
echo "================================== KAFKA 17/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 2 -c 2 -s 128 -t 120
echo "================================== KAFKA 18/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 3 -c 3 -s 128 -t 120
echo "================================== KAFKA 19/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 4 -c 4 -s 128 -t 120
echo "================================== KAFKA 20/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 5 -c 5 -s 128 -t 120
echo "================================== KAFKA 21/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 7 -c 7 -s 128 -t 120
echo "================================== KAFKA 22/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 10 -c 10 -s 128 -t 120
echo "================================== KAFKA 23/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 2 -p 2 -c 2 -s 128 -t 120
echo "================================== KAFKA 24/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 2 -p 4 -c 4 -s 128 -t 120
echo "================================== KAFKA 25/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 2 -p 6 -c 6 -s 128 -t 120
echo "================================== KAFKA 26/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 2 -p 8 -c 8 -s 128 -t 120
echo "================================== KAFKA 27/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 3 -p 3 -c 3 -s 128 -t 120
echo "================================== KAFKA 28/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 3 -p 6 -c 6 -s 128 -t 120
echo "================================== KAFKA 29/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 3 -p 9 -c 9 -s 128 -t 120
echo "================================== KAFKA 30/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 3 -p 12 -c 12 -s 128 -t 120
echo "================================== KAFKA 31/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 1 -c 1 -s 1024 -t 120
echo "================================== KAFKA 32/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 2 -c 2 -s 1024 -t 120
echo "================================== KAFKA 33/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 3 -c 3 -s 1024 -t 120
echo "================================== KAFKA 34/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 4 -c 4 -s 1024 -t 120
echo "================================== KAFKA 35/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 5 -c 5 -s 1024 -t 120
echo "================================== KAFKA 36/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 7 -c 7 -s 1024 -t 120
echo "================================== KAFKA 37/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 1 -p 10 -c 10 -s 1024 -t 120
echo "================================== KAFKA 38/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 2 -p 2 -c 2 -s 1024 -t 120
echo "================================== KAFKA 39/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 2 -p 4 -c 4 -s 1024 -t 120
echo "================================== KAFKA 40/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 2 -p 6 -c 6 -s 1024 -t 120
echo "================================== KAFKA 41/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 2 -p 8 -c 8 -s 1024 -t 120
echo "================================== KAFKA 42/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 3 -p 3 -c 3 -s 1024 -t 120
echo "================================== KAFKA 43/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 3 -p 6 -c 6 -s 1024 -t 120
echo "================================== KAFKA 44/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 3 -p 9 -c 9 -s 1024 -t 120
echo "================================== KAFKA 45/45 =================================="
java -jar benchmark.jar -b KAFKA -l true -q 3 -p 12 -c 12 -s 1024 -t 120
echo "=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= K O N I E C =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-="

# Wyłączanie środowiska dla benchmarku brokera Apache Kafka
cd /usr/local/dockerfiles/benchmark/kafka
docker-compose down