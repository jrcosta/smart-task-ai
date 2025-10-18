# Dockerfile para gerar documentacao JavaDoc
# Uso: docker build -f docs/javadoc/Dockerfile -t smart-task-javadoc .
# Depois: docker run -v $(pwd)/docs/javadoc:/app/docs smart-task-javadoc

FROM maven:3.9-eclipse-temurin-25

WORKDIR /app

# Copiar arquivo pom.xml
COPY backend/pom.xml .

# Copiar codigo-fonte
COPY backend/src src/

# Configurar Maven para nao usar plugins interactive
ENV MAVEN_BATCH_MODE=true

# Instalar dependencias
RUN mvn dependency:resolve

# Gerar JavaDoc
RUN mvn clean javadoc:aggregate -DskipTests

# Copiar documentacao gerada para volume
CMD cp -r target/site/apidocs /app/docs/

# Para uso manual, descomente:
# CMD ["/bin/bash"]
