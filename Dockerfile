# Amazon Corretto 17을 베이스 이미지로 사용
FROM amazoncorretto:17

# 작업 디렉토리를 /docker으로 설정
WORKDIR /docker/src/docker/spring-boot

# Gradle Wrapper를 복사하여 사용
COPY gradlew .
COPY gradle gradle

# Gradle 프로젝트 파일들을 복사
COPY . .

# RUN sleep 30;

# Gradle Wrapper를 초기화하고 종속성을 다운로드
RUN ./gradlew clean build -x test

# 빌드된 JAR 파일을 복사하여 이미지에 추가
ARG JAR_FILE_PATH=build/libs/*.jar
COPY ${JAR_FILE_PATH} auction-server.jar

# 애플리케이션을 실행하기 위한 명령을 지정
ENTRYPOINT ["java", "-jar", "auction-server.jar"]
