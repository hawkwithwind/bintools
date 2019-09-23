FROM frolvlad/alpine-java:jdk8-slim
ADD app.jar /app.jar
Add run.sh /run.sh
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENV LOADERPATH="lib/,config/,/config/"
ENTRYPOINT ["sh", "run.sh"]
