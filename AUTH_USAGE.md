# How to Use an Authenticated User (HTTP Basic Auth)

This guide explains how to authenticate requests to the API using HTTP Basic Authentication, both with Postman and via `curl`.

## User Credentials

- **Username:** `maria@gmail.com`
- **Password:** `123456`

---

## Using Postman

1. **Open Postman** and create a new request.
2. **Set the request method** (e.g., `POST`, `GET`) and the URL (e.g., `http://localhost:8080/transactions`).
3. **Go to the "Authorization" tab.**
4. In the **Type** dropdown, select `Basic Auth`.
5. Enter the credentials:
    - **Username:** `maria@gmail.com`
    - **Password:** `123456`
6. **Set the request body** (if needed) in the "Body" tab, using `raw` and `JSON` format. Example:
    ```json
    {
      "cardNumber": "6549873025634501",
      "cardPassword": "1234",
      "value": 10.00
    }
    ```
7. **Send the request.**

---

## Using curl

You can use the `-u` flag to pass the username and password for HTTP Basic Auth.

### Example:

```
curl -X POST \
  http://localhost:8080/transactions \
  -u maria@gmail.com:123456 \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "6549873025634501",
    "cardPassword": "1234",
    "value": 10.00
  }'
```

- Replace the URL and body as needed for your endpoint.
- The `-u` flag automatically encodes the credentials in the `Authorization: Basic` header.

---

## Notes
- All endpoints that require authentication must include these credentials.
- If authentication fails, the API will return a `401 Unauthorized` response.
- Make sure the user exists in your database and the password is correct.
