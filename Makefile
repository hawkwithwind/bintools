RUNTIME_IMAGE=hawkwithwind/bintool

docker-compose-build: put-jar
	docker-compose build

put-jar: build-jar
	cp target/gs-rest-service-0.1.0.jar docker/java/runtime/app.jar

build-jar: build-mvn-image package-frontend
	docker run --rm \
	  -v ~/.m2:/root/.m2 \
	  -v $(shell pwd):/usr/src/app \
	  $(RUNTIME_IMAGE):mvn \
	  mvn package

build-mvn-image:
	docker build -t $(RUNTIME_IMAGE):mvn docker/java/mvn

# 暂时不用 docker 打包因为 node:8-stretch-slim(linux) 会和 OSX node_modules yarn 冲突
#
# build-nodejs-image:
# 	docker build -t $(RUNTIME_IMAGE):nodejs docker/nodejs
#
# package-frontend: build-nodejs-image
# 	docker run --rm -v `pwd`/frontend:/home/work $(RUNTIME_IMAGE):nodejs yarn install && \
# 	docker run --rm -v `pwd`/frontend:/home/work $(RUNTIME_IMAGE):nodejs yarn build

package-frontend:
	cd frontend && \
	yarn && \
	yarn build && \
	cd - && \
	rm -rf src/main/resources/static/* && \
	cp -R frontend/dist/ src/main/resources/static