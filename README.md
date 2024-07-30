# Tesis-backend

## 👥 Contact
This service is currently supported by Lautaro Osinaga. 

* Email: osinagalj@gmail.com

## Getting Started
1. Make sure you have:
    - PostgresSQL v.x
    - Make sure you are using JDK-11.0.9 from [here](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)  in project settings
2. Clone this project to your local directory
3. Open the project with Intellij
4. Use Spring Boot application configuration, and set options:
5. Run `mvn clean package`
6. You may now run/debug the service in IntelliJ
    1. Navigate to [localhost:8080](http://localhost:8080/) to view the UI


## Common Issues
`Click in the project -> Maven -> Reload project`


##### AB test overrides are not working
Make sure you have a valid java version.

[Sample Postman collection with domain requests](https://www.postman.com/collections/89f2152703290211f1c8)

## Docker

Assuming you have done AWS test account keygen and Vault login, and you are in project folder

```
vault login -no-print -tls-skip-verify -address=https://vault-enterprise.us-west-2.secrets.runtime.test-cts.exp-aws.net -namespace=lab -method=ldap username=${USER}
```

------------
## Release Process
Documentation: 

```properties
spring_profiles_active=prod
PROD_DB_HOST=containers-us-west-195.railway.app
PROD_DB_PORT=7107
PROD_DB_NAME=railway
PROD_DB_PASSWORD=yMXKKFdJhiNwHyD98gcO
PROD_DB_USERNAME=postgres
```
