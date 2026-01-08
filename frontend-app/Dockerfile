# 기반 이미지는 가벼운 Nginx (Alpine Linux 버전)
FROM nginx:alpine
LABEL maintainer="tahiti80"

# 1. 시간대 설정 (Asia/Seoul)
# Alpine 리눅스는 tzdata 패키지를 설치해야 시간대 변경 가능
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

# 2. Nginx 설정 파일 덮어쓰기 (SPA 라우팅용)
# (프로젝트 루트에 nginx.conf 파일이 있어야 함)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 3. 빌드 결과물 복사
# GitHub Actions에서 이미 빌드한 'dist' 폴더를 컨테이너 안으로 복사
COPY dist /usr/share/nginx/html

# 4. 포트 노출
EXPOSE 80

# 5. 실행 명령 (Nginx 데몬 실행)
CMD ["nginx", "-g", "daemon off;"]

