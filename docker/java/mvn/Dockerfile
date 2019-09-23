FROM maven:3-jdk-8-alpine

ARG mirror=mirrors.ustc.edu.cn
ADD modifysource.sh /home/scripts/modifysource.sh
RUN /bin/sh /home/scripts/modifysource.sh $mirror
RUN cat /etc/apk/repositories

RUN apk add --no-cache --update tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo 'Asia/Shanghai' > /etc/timezone

ENV LANG="zh_CN.UTF-8"
RUN echo "export LC_ALL=zh_CN.UTF-8" >> /etc/profile

# TO FIX: The musl libc included with alpine is not compatible with the protoc binary built by google.
# https://github.com/xolstice/protobuf-maven-plugin/issues/23#issuecomment-266098369
RUN apk --no-cache add ca-certificates wget
#RUN wget -q -O /etc/apk/keys/sgerrand.rsa.pub https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub
#RUN wget https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.29-r0/glibc-2.29-r0.apk
#RUN apk add glibc-2.29-r0.apk

WORKDIR /usr/src/app

