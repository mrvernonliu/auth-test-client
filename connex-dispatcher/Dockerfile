FROM nginx:1.15.3

RUN rm -R /etc/nginx/nginx.conf
COPY ./nginx-configuration/nginx.conf /etc/nginx

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]