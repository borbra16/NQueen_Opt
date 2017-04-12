FROM ubuntu:16.04

RUN apt-get update -y && \
 apt-get install -y  maven openjdk-8-jdk

EXPOSE 8090

ADD /target/n-damen-0.0.1-SNAPSHOT.war n-damen-0.0.1-SNAPSHOT.war
ENTRYPOINT ["java","-jar","n-damen-0.0.1-SNAPSHOT.war"]

