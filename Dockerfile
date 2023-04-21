FROM openjdk:8-jdk-alpine
MAINTAINER xiaochen
ADD target/selenium-0.0.1-SNAPSHOT.jar  /home/openSelenium.jar
CMD java -jar /home/openSelenium.jar