# Use an official Ubuntu base image
FROM ubuntu:24.04

# Set environment variable to avoid interactive prompts during apt operations
ENV DEBIAN_FRONTEND=noninteractive
ENV PATH="/usr/games:${PATH}"

# Update the image and install necessary packages (Maven, Java 17, Git, Apache, and UciEngine)
RUN apt-get update && \
    apt-get install -y software-properties-common && \
    add-apt-repository ppa:openjdk-r/ppa && \
    apt-get update && \
    apt-get install -y openjdk-17-jdk apache2 git maven stockfish gnuchess fairy-stockfish fruit openssl && \
    a2enmod proxy proxy_http proxy_wstunnel rewrite ssl && \
    apt-get clean

# Create self-signed SSL certificate for HTTPS
RUN mkdir -p /etc/ssl/certs && mkdir -p /etc/ssl/private && \
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /etc/ssl/private/apache-selfsigned.key -out /etc/ssl/certs/apache-selfsigned.crt -subj "/CN=localhost"

# Set Java 17 as the default version
RUN update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-17-openjdk-amd64/bin/java 1 && \
    update-alternatives --set java /usr/lib/jvm/java-17-openjdk-amd64/bin/java

# Set the working directory for the project
WORKDIR /app

# Clone the first Git repo and build the project
RUN git clone https://github.com/romansen47/spring-annotation-context-initializer-template.git /app/spring-annotation-context-initializer-template && \
    cd /app/spring-annotation-context-initializer-template && \
    mvn clean install

# Clone and build the second Git repo
RUN git clone https://github.com/romansen47/chess.git /app/chess && \
    cd /app/chess && \
    git fetch origin work && \
    git checkout work && \
    mvn clean install

# Clone and build the third Git repo
RUN git clone https://github.com/romansen47/website.git /app/website && \
    cd /app/website && \
    git fetch origin work && \
    git checkout work && \
    mvn clean install

# Copy the Apache configuration files
COPY apache-config.conf /etc/apache2/sites-available/000-default.conf
COPY apache-ssl.conf /etc/apache2/sites-available/default-ssl.conf

# Enable the SSL site in Apache
RUN a2ensite default-ssl.conf

# Expose HTTP (80) and HTTPS (443) ports
EXPOSE 80 443

# Start Apache and run the Spring Boot application
CMD service apache2 start && java -jar /app/website/target/website-0.0.1-SNAPSHOT.jar

