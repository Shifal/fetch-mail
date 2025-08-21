# FetchMail Application

FetchMail is a Spring Boot application that connects to Gmail using the Gmail API. It provides REST endpoints to **search emails** with filters (`from`, `to`, `subject`) and fetch **email details by ID**.

---

## Features

- Connect to Gmail via OAuth2
- Search emails with optional filters:
  - `subject`
  - `from`
  - `to`
  - `maxResults`
  - `pageToken`
- Fetch a single email by ID
- Returns clean DTOs (`EmailMessage`) without exposing Gmail internals
- Validates input parameters (`maxResults` between 1-100)
- Optimized for Java 17+ and Spring Boot 3.x

---

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- Gmail API credentials (`credentials.json`) in `src/main/resources/`
- Optional: Google token storage directory configured in `application.properties`:

```properties
app.gmail.tokens-directory=./tokens
app.gmail.user-id=me



## Setup : 
1. Clone the repository:
    git clone https://github.com/your-username/fetchmail.git
    cd fetchmail

2. Add credentials.json to src/main/resources/.
    Follow Gmail API Quickstart to obtain credentials.

3. Configure token storage in application.properties (optional):
    app.gmail.tokens-directory=./tokens
    app.gmail.user-id=me


## API Endpoints 
1. Search Emails
    GET /api/emails


## Notes :
      The application uses @ModelAttribute to automatically map query parameters to GmailQueryCriteria.
      The maxResults field is validated (@Min(1), @Max(100)).
      Tomcat embedded may show warnings about System.load() for JNI libraries. Use the JVM flag above to suppress it.
