FROM openjdk
ADD target/signature-0.0.1-SNAPSHOT.jar /data/signature-0.0.1-SNAPSHOT.jar
ADD src/main/resources/signature.yml /data/signature.yml
CMD java -jar /data/signature-0.0.1-SNAPSHOT.jar server /data/signature.yml
EXPOSE 3000