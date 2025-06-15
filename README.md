# Instructions to Run the Application for Testing

## 0. Go to the project root folder

Where `pom.xml` and `docker-compose.yml` are located.

---

## 1. Start the PostgreSQL Database

Start the required database container:

```
docker compose up -d
```

## 2. Start the Spring Boot Application

With the database running, start the app using:

```
./mvnw spring-boot:run
```
Or use your IDE’s Run button:

## 3. Stop and Remove the Database Container

When finished, stop and remove the database container:
```
docker compose down
```
Tip:
You only need to run docker compose up -d once per session (the container stays running in the background).
Use docker compose down when you’re done to clean up.
