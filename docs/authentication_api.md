Authentication API allows you to login and get a token in order to use the FIWARE IoT Stack APIs.

Please, take into account that Device API does not require a token from Authentication API to send observations.

# Login 

You can login using your credentials (username and password) in order to get a token.

```
POST /v3/auth/tokens HTTP/1.1
Host: test.ttcloud.net:5001
Content-Type: application/json

{
    "auth": {
        "identity": {
            "methods": [
                "password"
            ],
            "password": {
                "user": {
                    "domain": {
                        "name": "{{Fiware-Service}}"
                    },
                    "name": "{{user-name}}",
                    "password": "{{user-pass}}"
                }
            }
        },
        "scope": {
            "domain": {
                "name": "{{Fiware-Service}}"
            }
        }
    }
}
```

You will receive an HTTP 201 Created response with a header called X-Subject-Token, this is your {{user-token}} like this:

```
HTTP 201 Created
Content-Type : application/json
Vary : X-Auth-Token
X-Subject-Token : {{user-token}}

{
  "token": {
    "domain": {
      "id": "63e0201c733343bb85e275f32b891be2",
      "name": "{{Fiware-Service}}"
    },
    "methods": [
      "password"
    ],
    "roles": [
      {
        "id": "eb4f224ca3ee4e829790765c30adfbf3",
        "name": "admin"
      }
    ],
    "expires_at": "2018-08-26T08:14:59.632237Z",
    "catalog": [],
    "extras": {},
    "user": {
      "domain": {
        "id": "63e0201c733343bb85e275f32b891be2",
        "name": "{{Fiware-Service}}"
      },
      "id": "f44ae6ecb4ea4310810273b042946505",
      "name": "{{user-name}}"
    },
    "issued_at": "2015-08-27T08:14:59.632260Z"
  }
}
```

Please, be careful pasting your {{user-token}} properly on next steps. This is your API token and it will be valid for 3 years.


# Get token status

Once you get a token you can check its validity anytime as follows:

```
POST /v3/auth/tokens HTTP/1.1
Host: test.ttcloud.net:5001
Content-Type: application/json
X-Auth-Token: {{user-token}}

{
    "auth": {
        "identity": {
            "methods": [
                "token"
            ],
            "token": {
                "id": "{{user-token}}"
            }
        }
    }
}
```

You can check when the token was issued and when it will expire on the response body:

```
{
  "token": {
    "issued_at": "2015-08-27T08:22:39.688387Z",
    "extras": {},
    "methods": [
      "token",
      "password"
    ],
    "expires_at": "2018-08-25T08:30:37.131450Z",
    "user": {
      "domain": {
        "id": "63e0201c733343bb85e275f32b891be2",
        "name": "{{Fiware-Service}}"
      },
      "id": "f44ae6ecb4ea4310810273b042946505",
      "name": "{{user-token}}"
    }
  }
}
```

Token expiration date can not be extended, but you can request a new token anytime and the ones you got before will still be valid.

# In more detail ...
