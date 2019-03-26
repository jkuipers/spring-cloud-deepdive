@echo off
call deregister-consul talk-service-8881
call deregister-consul talk-service-8891
call deregister-consul api-gateway-zuul-8882
call deregister-consul api-gateway-cloud-8883
call deregister-consul review-service-8880
call deregister-consul conference-web-8080
