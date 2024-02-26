# Dockerfile-mysql
FROM mysql:8.0

ENV MYSQL_ROOT_PASSWORD=0000
ENV MYSQL_DATABASE=auction_server
# ENV MYSQL_USER=root
# ENV MYSQL_PASSWORD=auction_password

#ADD init.sql /docker-entrypoint-initdb.d
# 초기화 스크립트를 복사하여 컨테이너가 시작될 때 실행되도록 설정
#COPY ./init.sql /docker-entrypoint-initdb.d/