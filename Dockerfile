# Amazon Corretto 17을 베이스 이미지로 사용
FROM amazoncorretto:17

# 개발 환경에서 실행하려면 환경 변수를 설정합니다.
ENV SPRING_PROFILES_ACTIVE=dev

# Gradle Wrapper를 복사하여 사용
COPY gradlew .
COPY gradle gradle

# Gradle 프로젝트 파일들을 복사
COPY . .

# Gradle Wrapper를 초기화하고 종속성을 다운로드
RUN ./gradlew clean build -x test

# 빌드된 JAR 파일을 복사하여 이미지에 추가
ARG JAR_FILE_PATH=build/libs/*.jar
COPY ${JAR_FILE_PATH} auction-server.jar

# 애플리케이션을 실행하기 위한 명령을 지정
ENTRYPOINT ["java", "-jar", "auction-server.jar"]
