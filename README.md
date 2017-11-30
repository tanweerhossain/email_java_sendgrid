#1. Put the project in Tomcat server

#2. refer REST_CALL_FORMAT.json for API call in Rest Client

-----REST  API CALL FORMAT -----

fetch('http://localhost:8080/email/jsonparserservlet', {
      method: 'POST',
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer --SENDGRID API KEY----"
      },
      body:  JSON.stringify({
        "personalizations": [
          {
            "to": [
               "abc@mail.xy"
            ],
            "cc": [
               "abc@mail.xyz"
            ],
            "bcc": [
               "abc@mail.xyz",
               "abc.1996@mail.xyz"
            ],
            "subject": "Mail Send through tomcat8 and sendgrid"
          }
        ],
        "from": "abc@mail.xy",
        "reply_to": "abc@mail.xy",
        "subject": "Mail Send through tomcat8 and sendgrid",
        "content": [
          {
            "type": "text/html",
            "value": "<html><p>Hi Sir,<br /><br />Its Done</p></html>"
          }
        ]
      }),
    }) 