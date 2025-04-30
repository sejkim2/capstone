# Step 1: React 앱 빌드
FROM node:18-alpine as builder
WORKDIR /app
COPY ./srcs/ .
RUN npm install && npm run build

# Step 2: Nginx로 정적 파일 서빙
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY ./conf/nginx.conf /etc/nginx/conf.d/default.conf
