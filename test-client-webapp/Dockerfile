FROM nginx:1.15.3

RUN mkdir -p /test-client-webapp/
WORKDIR test-client-webapp/

COPY ./build /test-client-webapp

RUN rm /etc/nginx/conf.d/*
COPY ./nginx-configuration/default.conf /etc/nginx/conf.d/

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]