PGPORT ?= 5432
PROJECT_PREFIX = tesis-backend
PROJECT = $(PROJECT_PREFIX)-$(PGPORT)
JAVA_HOME = /Users/lautaroosinaga/Library/Java/JavaVirtualMachines/corretto-11.0.30/Contents/Home
DOCKER_BUILDKIT = 1
export JAVA_HOME DOCKER_BUILDKIT

default: up

db:
	docker-compose -p $(PROJECT) up -d db
	sleep 5

app:
	docker-compose -p $(PROJECT) up -d --build app

down:
	docker-compose -p $(PROJECT) down

up:
	docker-compose -p $(PROJECT) up -d --build
