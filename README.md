### Authentication and Authorization
This guide explains how to get and refresh an access token
and how to use it to authenticate in gateway service.
          

#### step 1: getting the token
prerequisites
ensure the keycloak is running

Token Endpoint

keycloak provides an endpoint to obtain an access token, use the following parameters in a POST request
             
    curl --location 'http://localhost:8080/realms/master/protocol/openid-connect/token' \
    --header 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode 'grant_type=password' \
    --data-urlencode 'username=admin' \
    --data-urlencode 'password=admin123' \
    --data-urlencode 'scope=openid email profile' \
    --data-urlencode 'client_secret=E1L1o42Mp3Fq5bAzj04QrGD4sV9cWwEo' \
    --data-urlencode 'client_id=gateway-resource-server'
                          
client_id: your keycloak client id
client_secret: your keycloak client secret

now you have both refresh and access tokens

    {
      "access_token": "<access_token>",
      "refresh_token": "<refresh_token>", 
      "expires_in": 300,
      "refresh_expires_in": 
      "token_type": "Bearer"
    }


#### step3: refresh the access token
when an access token expires, use the refresh token to get a new token

    curl --location 'http://localhost:8080/realms/master/protocol/openid-connect/token' \    
    --header 'Content-Type: application/x-www-form-urlencoded' \                             
    --data-urlencode 'grant_type=refresh_token' \                                                 
    --data-urlencode 'refresh_token=<refresh_token>' \                                          
    --data-urlencode 'client_secret=E1L1o42Mp3Fq5bAzj04QrGD4sV9cWwEo' \                      
    --data-urlencode 'client_id=gateway-resource-server'                                     
                                

                 

                                        