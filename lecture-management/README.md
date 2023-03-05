### For Backend

- JDK 17 Require
- Gradle Require

```
cd api
```

Create Database:
```
docker run --name postgres-0 -e POSTGRES_PASSWORD=password -d -p 5432:5432 postgres
```
Run Command:
```
gradle bootRun
```

[Postman Collection](https://github.com/sametakbal/tutorials/blob/master/lecture-management/Lecture%20Management.postman_collection.json)

### For Frontend

```
cd frontend
```
Load node_modules:
```
npm install
```
Run Command:
```
npm start
```
