FROM nginx:1.21.6

WORKDIR /usr/share/nginx/html

EXPOSE 443

RUN rm -rf ./*

COPY ./build ./
COPY ./.cert/localhost.pem /etc/nginx/ssl/localhost.crt
COPY ./.cert/localhost.key /etc/nginx/ssl/localhost.key
COPY ./docker-nginx-config.conf /etc/nginx/conf.d/default-ssl.conf

ENTRYPOINT ["nginx", "-g", "daemon off;"]
