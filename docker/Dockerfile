# Verwende ein offizielles Ubuntu-Image als Basis
FROM ubuntu:20.04

# Setze die Umgebungsvariable, um interaktive Eingaben bei apt zu vermeiden
ENV DEBIAN_FRONTEND=noninteractive

# Aktualisiere das Image und installiere die benötigten Pakete (Maven, Java 17, Git, Apache und Stockfish)
RUN apt-get update && \
    apt-get install -y software-properties-common && \
    add-apt-repository ppa:openjdk-r/ppa && \
    apt-get update && \
    apt-get install -y openjdk-17-jdk apache2 git maven stockfish openssl && \
    a2enmod proxy proxy_http proxy_wstunnel rewrite ssl && \
    apt-get clean

# Erstelle das SSL-Zertifikat (selbstsigniert) für HTTPS
RUN mkdir -p /etc/ssl/certs && mkdir -p /etc/ssl/private && \
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /etc/ssl/private/apache-selfsigned.key -out /etc/ssl/certs/apache-selfsigned.crt -subj "/CN=localhost"

# Setze Java 17 als Standard
RUN update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-17-openjdk-amd64/bin/java 1 && \
    update-alternatives --set java /usr/lib/jvm/java-17-openjdk-amd64/bin/java

# Erstelle ein Verzeichnis für das Projekt
WORKDIR /app

# Klone das erste Git-Repo und baue das Projekt in einem einzigen RUN-Schritt
RUN git clone https://github.com/romansen47/spring-annotation-context-initializer-template.git /app/spring-annotation-context-initializer-template && \
    cd /app/spring-annotation-context-initializer-template && \
    mvn clean install

# Klone und baue das zweite Git-Repo
RUN git clone https://github.com/romansen47/chess.git /app/chess && \
    cd /app/chess && \
    git fetch origin work && \
    git checkout work && \
    mvn clean install

# Klone und baue das dritte Git-Repo
RUN git clone https://github.com/romansen47/website.git /app/website && \
    cd /app/website && \
    git fetch origin work && \
    git checkout work && \
    mvn clean install

# Apache-Konfiguration kopieren
COPY apache-config.conf /etc/apache2/sites-available/000-default.conf
COPY apache-ssl.conf /etc/apache2/sites-available/default-ssl.conf

RUN a2ensite default-ssl.conf

# Exponiere HTTP (80) und HTTPS (443)
EXPOSE 80 443

# Starte Apache und führe die JAR-Datei aus
CMD service apache2 start && java -jar /app/website/target/website-0.0.1-SNAPSHOT.jar
