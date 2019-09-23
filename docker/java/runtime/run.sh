#!/usr/bin/env sh

#java $JAVA_OPTS -Dloader.path=$LOADERPATH -Djava.security.egd=file:/dev/./urandom -jar "$@" /app.jar
#java $JAVA_OPTS -Dloader.path=$LOADERPATH -Djava.security.egd=file:/dev/./urandom -cp /app.jar com.webot.chatbothub.ChatBotHubClient
java $JAVA_OPTS -Dloader.path=$LOADERPATH -Djava.security.egd=file:/dev/./urandom -jar "$@" /app.jar

