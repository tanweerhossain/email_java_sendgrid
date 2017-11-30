#1. Put the project in Tomcat server

#2. refer REST_CALL_FORMAT.json for API call in Rest Client
<!--REST  API CALL FORMAT
fetch('http://localhost:8080/email/jsonparserservlet', {
      method: 'POST',
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer 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"
      },
      body:  JSON.stringify({
        "personalizations": [
          {
            "to": [
               "tanweer@myanatomy.in"
            ],
            "cc": [
               "tanweerhossainatspacebbsr@hotmail.com"
            ],
            "bcc": [
               "tanweerhossainatspacebbsr@gmail.com",
               "tanweerhossain.1996@gmail.com"
            ],
            "subject": "Mail Send through tomcat8 and sendgrid"
          }
        ],
        "from": "tanweer@myanatomy.in",
        "reply_to": "ajeet@myanatomy.in",
        "subject": "Mail Send through tomcat8 and sendgrid",
        "content": [
          {
            "type": "text/html",
            "value": "<html><p>Hi Sir,<br /><br />Its Done</p></html>"
          }
        ]
      }),
    }) -->