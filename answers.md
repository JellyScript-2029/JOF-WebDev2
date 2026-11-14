# Lab 11 — Answer Template

**Course:** Web Development 2 &nbsp;|&nbsp; **Week:** 11 &nbsp;|&nbsp; **Lab:** Capstone Milestone 3

---

## Student Information

| Field | Value |
|---|---|
| Name | Jhelianne Figuerres |
| Student ID | 2510465 |
| GitHub Repository URL | https://github.com/JellyScript-2029/JOF-WebDev2 |
| Branch (if not `main`) | `week11` |

---

## Task 1 — Input Validation on DTOs

**DTO files with annotations** *(links to files in repo)*:

- `CreatePetRequest.java`: https://github.com/JellyScript-2029/JOF-WebDev2/blob/week11/src/main/java/com/example/dto/CreatePetRequest.java
- `UpdatePetRequest.java`: https://github.com/JellyScript-2029/JOF-WebDev2/blob/week11/src/main/java/com/example/dto/UpdatePetRequest.java

**Evidence of 400 response for missing field:**

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-11-14T04:27:25.175715300Z",
  "errors": [
    {
      "field": "name",
      "message": "Pet name is required"
    },
    {
      "field": "name",
      "message": "Pet name must not exceed 50 characters"
    },
    {
      "field": "contactEmail",
      "message": "Contact email must be a valid email address"
    },
    {
      "field": "microchipNumber",
      "message": "Microchip number must be between 4 and 30 characters"
    },
    {
      "field": "microchipNumber",
      "message": "Microchip number is required"
    }
  ]
}
```

---

## Task 2 — Global Exception Handler

**Files created:**

- `GlobalExceptionHandler.java`: https://github.com/JellyScript-2029/JOF-WebDev2/blob/week11/src/main/java/com/example/exception/GlobalExceptionHandler.java
- `ErrorResponse.java`: https://github.com/JellyScript-2029/JOF-WebDev2/blob/week11/src/main/java/com/example/dto/ErrorResponse.java
- `ResourceNotFoundException.java`: https://github.com/JellyScript-2029/JOF-WebDev2/blob/week11/src/main/java/com/example/exception/ResourceNotFoundException.java

**Evidence — 404 response:**

```json
{
  "status": 404,
  "message": "Pet not found with id: 999",
  "timestamp": "2026-11-14T04:27:39.358683400Z",
  "errors": null
}
```

**Evidence — 400 response with field errors:**

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-11-14T04:27:25.175715300Z",
  "errors": [
    {
      "field": "name",
      "message": "Pet name is required"
    },
    {
      "field": "name",
      "message": "Pet name must not exceed 50 characters"
    },
    {
      "field": "contactEmail",
      "message": "Contact email must be a valid email address"
    },
    {
      "field": "microchipNumber",
      "message": "Microchip number must be between 4 and 30 characters"
    },
    {
      "field": "microchipNumber",
      "message": "Microchip number is required"
    }
  ]
}
```

---

## Task 3 — CORS Configuration

**File created:**

- `WebConfig.java`: https://github.com/JellyScript-2029/JOF-WebDev2/blob/week11/src/main/java/com/example/config/WebConfig.java

**Evidence — CORS headers present:**

```http
> OPTIONS /api/pets HTTP/1.1
> Host: localhost:8080
> Origin: http://localhost:3000
> Access-Control-Request-Method: POST

< HTTP/1.1 200 
< Vary: Origin
< Vary: Access-Control-Request-Method
< Vary: Access-Control-Request-Headers
< Access-Control-Allow-Origin: http://localhost:3000
< Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE,OPTIONS
< Access-Control-Allow-Credentials: true
< Access-Control-Max-Age: 1800
< Allow: GET, HEAD, POST, PUT, DELETE, OPTIONS, PATCH
< Content-Length: 0
```

---

## Task 4 — Pagination and Filtering

**Files updated:**

- Repository method added: `Page<Pet> findBySpeciesIgnoreCase(String species, Pageable pageable)` in https://github.com/JellyScript-2029/JOF-WebDev2/blob/week11/src/main/java/com/example/repository/PetRepository.java
- Service method updated: `Page<PetDTO> findAll(int page, int size, String sortBy, String direction, String species)` in https://github.com/JellyScript-2029/JOF-WebDev2/blob/week11/src/main/java/com/example/service/PetService.java
- Controller method updated: `Page<PetDTO> getAll(...)` in https://github.com/JellyScript-2029/JOF-WebDev2/blob/week11/src/main/java/com/example/controller/PetController.java

**Evidence — paginated response:**

```json
{
  "content": [
    {
      "id": 3,
      "microchipNumber": "MC-0003",
      "name": "Bruno",
      "species": "Dog",
      "breed": "Aspin",
      "ownerId": null
    },
    {
      "id": 1,
      "microchipNumber": "MC-0001",
      "name": "Buddy",
      "species": "Dog",
      "breed": "Labrador",
      "ownerId": null
    },
    {
      "id": 7,
      "microchipNumber": "MC-2001",
      "name": "Mochi",
      "species": "Dog",
      "breed": "Shih Tzu",
      "ownerId": 1
    },
    {
      "id": 2,
      "microchipNumber": "MC-0002",
      "name": "Whiskers",
      "species": "Cat",
      "breed": "Siamese",
      "ownerId": null
    }
  ],
  "empty": false,
  "first": true,
  "last": true,
  "number": 0,
  "numberOfElements": 4,
  "pageable": {
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 5,
    "paged": true,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "unpaged": false
  },
  "size": 5,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "totalElements": 4,
  "totalPages": 1
}
```

**Evidence — filtered response (`GET /api/pets?species=Dog`):**

```json
{
  "content": [
    {
      "id": 1,
      "microchipNumber": "MC-0001",
      "name": "Buddy",
      "species": "Dog",
      "breed": "Labrador",
      "ownerId": null
    },
    {
      "id": 3,
      "microchipNumber": "MC-0003",
      "name": "Bruno",
      "species": "Dog",
      "breed": "Aspin",
      "ownerId": null
    },
    {
      "id": 7,
      "microchipNumber": "MC-2001",
      "name": "Mochi",
      "species": "Dog",
      "breed": "Shih Tzu",
      "ownerId": 1
    }
  ],
  "empty": false,
  "first": true,
  "last": true,
  "number": 0,
  "numberOfElements": 3,
  "pageable": {
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 20,
    "paged": true,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "unpaged": false
  },
  "size": 20,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "totalElements": 3,
  "totalPages": 1
}
```

**Evidence — max size cap (`GET /api/pets?size=999`):**

When requesting `GET /api/pets?size=999`, the service capped `size` at `MAX_PAGE_SIZE = 100`. In the response metadata, `"size"` and `"pageable.pageSize"` were both `100` instead of `999`:

```json
{
  "number": 0,
  "numberOfElements": 4,
  "pageable": {
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 100,
    "paged": true
  },
  "size": 100,
  "totalElements": 4,
  "totalPages": 1
}
```

---

## Task 5 — Documentation and Test Collection

**Files committed:**

- `API.md` location in repo: https://github.com/JellyScript-2029/JOF-WebDev2/blob/week11/API.md
- `api_tests.json` location in repo: https://github.com/JellyScript-2029/JOF-WebDev2/blob/week11/api_tests.json

**Test collection run evidence:**

Imported `api_tests.json` (`Week11 - Jhelianne Figuerres API`) into Postman/Bruno and ran the full 10-request collection against `http://localhost:8080`: **10/10 requests passed** (verifying paginated list, species filtering, max page size cap, pet creation `201`, validation errors `400`, get by ID `200`, missing ID `404`, full update `200`, partial update `200`, and delete `204`).

**List the requests in your collection:**

| Request name | Method | Path | Expected status |
|---|---|---|---|
| 1. GET Paginated Pets List (200) | `GET` | `/api/pets?page=0&size=5&sort=name,asc` | `200` |
| 2. GET Filtered Pets by Species (200) | `GET` | `/api/pets?species=Dog` | `200` |
| 3. GET Max Page Size Cap Check (200) | `GET` | `/api/pets?size=999` | `200` |
| 4. POST Create Pet Happy Path (201) | `POST` | `/api/pets` | `201` |
| 5. POST Validation Error Test (400) | `POST` | `/api/pets` | `400` |
| 6. GET Pet by ID (200) | `GET` | `/api/pets/{{createdPetId}}` | `200` |
| 7. GET Non-Existent Pet (404) | `GET` | `/api/pets/999` | `404` |
| 8. PUT Full Update Pet (200) | `PUT` | `/api/pets/{{createdPetId}}` | `200` |
| 9. PATCH Partial Update Pet (200) | `PATCH` | `/api/pets/{{createdPetId}}` | `200` |
| 10. DELETE Pet (204) | `DELETE` | `/api/pets/{{createdPetId}}` | `204` |

---

## Reflection

> In 2–3 sentences: which of the five topics from Week 11 had the biggest impact on your capstone API? What surprised you most when implementing it?

Implementing the `@RestControllerAdvice` global exception handler together with Bean Validation had the biggest impact on my capstone API because it removed repetitive error-handling code from the controller and guaranteed that every failure (`400`, `404`, `409`, `500`) returns the exact same `ErrorResponse` JSON format. What surprised me most was how seamlessly Spring's `Pageable` and `Page<PetDTO>` handle pagination, sorting, and metadata generation out of the box with just a few lines in the repository and service layers.