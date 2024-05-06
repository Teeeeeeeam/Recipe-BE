FROM openjdk:17-jdk-slim

RUN mkdir "/mnt/images"
WORKDIR /app

COPY build/libs/RecipeRadar-0.0.1-SNAPSHOT.jar /app/recipe.jar

ENTRYPOINT ["java", "-jar","recipe.jar","0.0.0.0:8080"]