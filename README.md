# GitHub integration

GitHub integration task

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

JDK 1.8 or higher

```
https://www.oracle.com/java/technologies/jdk8-downloads.html
```

Docker

```
https://hub.docker.com/
```
### Installing

Github access token is required

```
Generate https://help.github.com/en/articles/creating-a-personal-access-token-for-the-command-line

With scopes:

user
public_repo
repo
repo_deployment
repo:status
read:repo_hook
read:org
read:public_key
read:gpg_key
```

Set up enviroment variables

```
Replace ${github.app.token} placeholder in main/resources with generated access token
```

To run application:

```
./mvnw.cmd spring-boot:run
```

## Running the tests

Before running test:

```
Replace ${github.app.token} placeholder in test/resources with generated access token

Replace ${github.user.token.test} placeholder in test/resources with generated access token with write access to simulate real user.
Put "Bearer" before token: "Bearer access-token"
```

Run test with:

```
./mvnw.cmd test
```

## Deployment

To build docker image:

```
docker build -t  image-tag .
```

Run with:

```
$ docker run image-tag
```
## Built With

* [Springboot](https://spring.io/projects/spring-boot) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management


## Authors

* **Žymantas Sakalauskas** - *Initial work* - [zhymke](https://github.com/zhymke)
