This is Java 21 with Spring BOOT + JPA solution for the management of users, movies and their ratings.

# Building
```
./gradlew clean build
```
## With docker
```
docker build -f src/main/docker/Dockerfile -t sky-movies-api .
```
# Running
## With Gradle
```
./gradlew bootRun
```
## With Docker
### With persistent volume storage
```
docker run -p 8080:8080 -v sky-movies-api-data:/app/data sky-movies-api
```
### No persistent volume (data will be lost on restart)
```
docker run -p 8080:8080 sky-movies-api
```

# Using
## Authentication
I've implemented a custom authentication interceptor. It expects 2 headers: X-Auth-Password and X-Auth-Username.
Interceptor works if controller's method has @RequirePasswordAuth annotation. 
It is possible to set only admin access by using @RequirePasswordAuth(onlyForAdmin = true).
Later, endpoints get a ThreadLocal with user credentials and check if the authenticated user is admin or not and if it is the same user as in the request.


## Endpoints
### POST request (ask for card pngs)
#### CURL
```
curl -X POST "http://localhost:8080/cards" \
-H "Content-Type: application/json" \
-d '{"cardNames": ["Card A", "Card B", "Card C"]}'
```
### GET request (results)
#### CURL
```
id=your-uuid-here
curl -X GET "http://localhost:8080/cards/$id"
```

# Database
- I used H2 database with file as storage.
- Default path defined in application.properties is ./src/main/resources/static/db. This allows quick debugging.
- In the docker image it is stored in data folder.

# Test
I've implemented unit tests for all controllers and services. They test basic happy flow and some edge cases.
I also wrote unit tests for custom auth interceptor which tests each branch. 

# Implementation details

## Limitations and improvements
- Currently, there is only one app admin with username "admin" and password "admin123" which is stored in application.properties _plainly_.
  It is not good to use this in production, but it is enough for this task.
  This admin is not stored in the database, so it is not possible to change it via API. 
- In future it would be good to store users with roles in the database and allow changing them via API and have initial admin set too.