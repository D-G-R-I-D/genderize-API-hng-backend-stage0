# HNG i14 - Backend Stage 0: Genderize API Integration

A Spring Boot REST API that integrates with the Genderize.io API to classify names by gender.

## Live URL
https://your-app.up.railway.app

## Endpoint
**GET** `/api/classify?name={name}`

## Success Response (200)
```json
{
  "status": "success",
  "data": {
    "name": "john",
    "gender": "male",
    "probability": 0.99,
    "sample_size": 1234,
    "is_confident": true,
    "processed_at": "2026-04-10T12:00:00Z"
  }
}
```

## Error Responses

| Status | Meaning |
|--------|---------|
| 400 | Missing or empty name parameter |
| 422 | Name is not a valid string |
| 404 | No prediction available for the provided name |
| 502 | Upstream or server failure |

## Tech Stack
- Java 21
- Spring Boot 3.5
- RestClient (Spring 6+)

## How to Run Locally
```bash
./mvnw spring-boot:run
```
Then visit: `http://localhost:8080/api/classify?name=john`

## Test Cases Verified
- ✅ Success with confident name (john)
- ✅ Missing name → 400
- ✅ Empty name → 400
- ✅ No prediction → error message
- ✅ CORS enabled (Access-Control-Allow-Origin: *)

## Author
Divine | HNG i14 Backend Track