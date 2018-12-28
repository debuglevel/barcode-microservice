## Building stage
FROM openjdk:8-jdk-alpine AS builder
# install fonts on alpine, as they are not available by default and some tests (Batik SVG to PNG) will fail with a NoClassDefFoundError
RUN apk add --no-cache ttf-dejavu

WORKDIR /src/

# cache gradle
COPY gradle /src/gradle
COPY gradlew /src/
# run "gradle --version" to let gradle-wrapper download gradle
RUN ./gradlew --version

# build source
COPY . /src/
RUN ./gradlew build

## Final image
FROM openjdk:8-jre-alpine
RUN mkdir /app
COPY --from=builder /src/build/libs/*-all.jar /app/barcode-microservice.jar

# set the default port to 80
ENV PORT 80
EXPOSE 80

CMD ["java", "-jar", "/app/barcode-microservice.jar"]
