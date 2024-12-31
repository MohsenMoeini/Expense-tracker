the gateway service is added as a  client (resource server) in master realm of KC Auth server

to get access token from Auth server, run the following command: 

curl --location 'http://localhost:8080/realms/master/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'username=admin' \
--data-urlencode 'password=admin123' \
--data-urlencode 'scope=openid email profile' \
--data-urlencode 'client_secret=E1L1o42Mp3Fq5bAzj04QrGD4sV9cWwEo' \
--data-urlencode 'client_id=gateway'


use the token in Authorization header for following requests