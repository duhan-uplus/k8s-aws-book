FROM amazoncorretto:11
LABEL maintainer="k8sbook"

# 1. 한글 및 시간대
RUN yum install -y glibc-langpack-ko
ENV LANG ko_KR.UTF8
ENV LC_ALL ko_KR.UTF8
RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime

# 2. 로컬 테스트용 CSV 파일 복사 (프로젝트 루트에 이 파일들이 있어야 함)
RUN mkdir -p /data
COPY ./sample_data/normal/sample_location1.csv /data/sample_location1.csv
COPY ./sample_data/normal/sample_location2.csv /data/sample_location2.csv

# 3. [핵심] 빌드된 JAR 파일 받기
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar

# 4. 실행
ENTRYPOINT ["java", \
 "-verbose:gc", \
 "-Xlog:gc*:stdout:time,uptime,level,tags", \
 "-Djava.security.egd=file:/dev/./urandom", \
 "-jar", \
 "/app.jar"]

