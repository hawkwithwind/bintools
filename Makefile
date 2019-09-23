RUNTIME_IMAGE=hawkwithwind/bintool

docker-compose-build: put-jar
	docker-compose build

put-jar: build-jar
	cp target/gs-rest-service-0.1.0.jar docker/java/runtime/app.jar

build-jar: build-mvn-image
	docker run --rm \
	  -v ~/.m2:/root/.m2 \
	  -v $(shell pwd):/usr/src/app \
	  $(RUNTIME_IMAGE):mvn \
	  mvn package

build-mvn-image:
	docker build -t $(RUNTIME_IMAGE):mvn docker/java/mvn

