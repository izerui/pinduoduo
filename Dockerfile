FROM openjdk:8-jdk-alpine
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
RUN apk update \
    && apk upgrade \
    && apk add --no-cache \
    tzdata \
    git \
    curl \
    tini \
    jq \
    ttf-dejavu \
    fontconfig \
    && rm -rf /var/cache/apk/* \
    && echo "Asia/Shanghai" > /etc/timezone \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
ENV TZ=Asia/Shanghai
COPY target/pingduoduo-exec.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
