# Dev Notes

## Endpoints

Status: http://localhost:8080/status

Swagger: http://localhost:8080/swagger-ui (User: testing, password: testing)


# How Auth to use swagger:

Go to http://localhost:8080/swagger-ui/#/auth-controller/loginUsingPOST

```Swagger
{
"email": "dev@gmail.com",
"password": "password"
}
```
#### Get the token and then do the auth in swagger

## Common Issues
`Click in the project -> Maven -> Reload project`

[Sample Postman collection with domain requests](https://www.postman.com/collections/89f2152703290211f1c8)


```properties
spring_profiles_active=prod
PROD_DB_HOST=containers-us-west-195.railway.app
PROD_DB_PORT=7107
PROD_DB_NAME=railway
PROD_DB_PASSWORD=yMXKKFdJhiNwHyD98gcO
PROD_DB_USERNAME=postgres
```
