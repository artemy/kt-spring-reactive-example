FROM adoptopenjdk:11-jre-hotspot
RUN mkdir /opt/app
COPY target/external-stub.jar /opt/app
CMD ["java", "-jar", "/opt/app/external-stub.jar"]