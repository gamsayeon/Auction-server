# Use the official Logstash image as the base image
FROM docker.elastic.co/logstash/logstash:8.1.1

# Set environment variables
ENV LS_JAVA_OPTS="-Xmx1024m -Xms1024m"
ENV JAVA_HOME="/usr/lib/jvm/corretto-17"
ENV CLASSPATH="/usr/share/logstash/logstash-core/lib/jars/mysql-connector-java.jar"
ENV PATH="$PATH:/usr/share/logstash/jdk/bin"

# Copy Logstash configuration file
COPY ./logstash.yml /usr/share/logstash/config/logstash.yml

# Copy MySQL Connector/J JAR file
COPY ./mysql-connector-j-8.0.33/mysql-connector-j-8.0.33.jar /usr/share/logstash/logstash-core/lib/jars/

# Command to run Logstash
CMD ["-f", "/path/to/your/logstash.conf"]