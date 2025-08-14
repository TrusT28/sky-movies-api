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

**Note**: In this documentation I will use real admin username and passwords for convenience of testing the results!

**Note**: Authentication user admin credentials will work for every endpoint that requires authentication!

## Endpoints
### POST request
#### Movies
Create a movie (requires admin access)
```
curl --location 'localhost:8080/movies/' \
--header 'X-Auth-Password: admin123' \
--header 'X-Auth-Username: admin' \
--header 'Content-Type: application/json' \
--data '{
	"name": "Movie1",
    "releaseDate": "2020-02-20"
}'
```

#### Users
Create a user
```
curl --location 'localhost:8080/users/' \
--header 'Content-Type: application/json' \
--data-raw '{
	"email": "user1@email.com",
    "username": "user1",
    "password": "pass1"
}'
```

#### Ratings
Create a rating for movie with id 1 by a user with id 1 (Requires authentication by same user. Otherwise it will fail with 403)
```
curl --location 'localhost:8080/ratings/' \
--header 'X-Auth-Password: pass1' \
--header 'X-Auth-Username: user1' \
--header 'Content-Type: application/json' \
--data '{
  "movieId":1,
  "userId": 1,
  "rating": 2.5
}'
```

### GET request

#### Movies
Get all movies
```
curl --location 'localhost:8080/movies'
```

Get a movie with id 1
```
curl --location 'localhost:8080/movies/1'
```

Get a movie with name Movie1 (case-sensitive)
```
curl --location 'localhost:8080/movies/named/Movie1'
```


#### Users
Get all users (requires admin access)
```
curl --location 'localhost:8080/users' \
--header 'X-Auth-Password: admin123' \
--header 'X-Auth-Username: admin'
```

Get a user by id 3 (requires authentication by same user or admin)
```
curl --location 'localhost:8080/users/3' \
--header 'X-Auth-Password: admin123' \
--header 'X-Auth-Username: admin'
```

Get a user by username user1 (requires authentication by same user or admin)
```
curl --location 'localhost:8080/users/named/user1' \
--header 'X-Auth-Password: admin123' \
--header 'X-Auth-Username: admin'
```


#### Ratings
Get all ratings of a user by userId
```
curl --location 'localhost:8080/users/1/ratings'
```

Get all ratings of a movie by movieId
```
curl --location 'localhost:8080/movies/1/ratings'
```

Get top-rated movies (paginated)
```
curl --location 'localhost:8080/movies/top-rated?page=0&size=10'
```

### DELETE request
I've implemented delete requests for all endpoints. If you delete a user, then all ratings of this user will be deleted too and it will affect movie's average rating.
If you delete a movie, all ratings of the movie will be deleted, and it will affect ratings of users.

#### Movies
Delete a movie with id 2 (requires admin access)
```
curl --location --request DELETE 'localhost:8080/movies/2' \
--header 'X-Auth-Password: admin123' \
--header 'X-Auth-Username: admin'
```

#### Users
Delete a user with id 1 (requires auth by same user or admin access)
```
curl --location --request DELETE 'localhost:8080/users/1' \
--header 'X-Auth-Password: pass1' \
--header 'X-Auth-Username: user1'
```

#### Ratings
Delete a rating of user with id 1 for a movie with id 3 (requires auth by same user)
```
curl --location --request DELETE 'localhost:8080/ratings?movieId=3&userId=1' \
--header 'X-Auth-Password: pass1' \
--header 'X-Auth-Username: user1'
```

# Database
- I used H2 database with file as storage.
- Default path defined in application.properties is ./src/main/resources/static/db. This allows quick debugging.
- In the docker image it is stored in data folder.

# Test
I've implemented unit tests for all controllers and services. They test basic happy flow and some edge cases.
I also wrote unit tests for custom auth interceptor which tests each branch. 

# Implementation details
I didn't create docker compose because it's not necessary with no other dependencies (as database is a file within project).

## Limitations and improvements
- Currently, there is only one app admin with username "admin" and password "admin123" which is stored in application.properties _plainly_.
  It is not good to use this in production, but it is enough for this task.
  This admin is not stored in the database, so it is not possible to change it via API. 
- In future it would be good to store users with roles in the database and allow changing them via API and have initial admin set too.