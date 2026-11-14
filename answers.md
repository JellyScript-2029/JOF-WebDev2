# WEBDEV2 — Week 10 Lab Answers

Name: Jhelianne Figuerres
Student ID: 2510465
GitHub repo: https://github.com/JellyScript-2029/JOF-WebDev2
Date submitted: November 14, 2026

---

## Task 1 — URL Design & Controller Skeleton

**URL design table (method, path, expected status codes):**

| HTTP Method | URL Path | Expected Status Codes |
|---|---|---|
| GET | `/api/pets` | `200` |
| GET | `/api/pets/{id}` | `200`, `404` |
| POST | `/api/pets` | `201`, `400`, `409` |
| PUT | `/api/pets/{id}` | `200`, `404`, `400`, `409` |
| PATCH | `/api/pets/{id}` | `200`, `404`, `400`, `409` |
| DELETE | `/api/pets/{id}` | `204`, `404` |

**Controller skeleton code:**
```java
@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public List<PetDTO> getAll() { return null; }

    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> getById(@PathVariable Long id) { return null; }

    @PostMapping
    public ResponseEntity<PetDTO> create(@RequestBody CreatePetRequest request) { return null; }

    @PutMapping("/{id}")
    public ResponseEntity<PetDTO> update(@PathVariable Long id, @RequestBody UpdatePetRequest request) { return null; }

    @PatchMapping("/{id}")
    public ResponseEntity<PetDTO> patch(@PathVariable Long id, @RequestBody UpdatePetRequest request) { return null; }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { return null; }
}
```

**Evidence (screenshot filename or curl output):**
```http
PS C:\Users\figue\JOF-WebDev2> curl.exe -i http://localhost:8080/api/pets
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 14 Nov 2026 03:39:15 GMT

[{"id":1,"microchipNumber":"MC-0001","name":"Buddy","species":"Dog","breed":"Labrador","ownerId":null},{"id":2,"microchipNumber":"MC-0002","name":"Whiskers","species":"Cat","breed":"Siamese","ownerId":null},{"id":3,"microchipNumber":"MC-0003","name":"Bruno","species":"Dog","breed":"Aspin","ownerId":null},{"id":7,"microchipNumber":"MC-2001","name":"Mochi","species":"Dog","breed":"Shih Tzu","ownerId":1}]
```

**Reflection question:**
_Why do REST URLs use plural nouns (`/api/students`) rather than verb phrases (`/api/getStudents`), and what role does the HTTP method play?_

Your answer:
In REST, URLs are meant to point to the resource or data itself (like `/api/pets`), not the action being done to it. The HTTP method (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`) is what actually acts as the verb and tells the server what to do with that resource. Using nouns keeps the URLs clean, predictable, and consistent instead of having to invent separate action names for every endpoint.

---

## Task 2 — GET Endpoints — List & Get by ID

**Approach / explanation:**
For `GET /api/pets`, the controller calls `petService.findAll()`, which converts every `Pet` entity into a `PetDTO` and returns `200 OK` (or `[]` if there are no records yet). For `GET /api/pets/{id}`, it wraps the result in a `ResponseEntity` so it returns `200 OK` when the pet exists and `404 Not Found` when the ID is missing. Instead of returning the full `Pet` entity with its lazy-loaded `PetOwner` object and `vaccinations` list, `PetDTO` only includes the basic pet fields and the `ownerId`.

**`PetDTO` class code:**
```java
package com.example.dto;

public class PetDTO {
    private Long id;
    private String microchipNumber;
    private String name;
    private String species;
    private String breed;
    private Long ownerId;

    public PetDTO() {}

    public PetDTO(Long id, String microchipNumber, String name, String species, String breed, Long ownerId) {
        this.id = id;
        this.microchipNumber = microchipNumber;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.ownerId = ownerId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMicrochipNumber() { return microchipNumber; }
    public void setMicrochipNumber(String microchipNumber) { this.microchipNumber = microchipNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
```

**`toDTO()` mapping method code:**
```java
public PetDTO toDTO(Pet pet) {
    Long ownerId = (pet.getOwner() != null) ? pet.getOwner().getId() : null;
    return new PetDTO(
            pet.getId(),
            pet.getMicrochipNumber(),
            pet.getName(),
            pet.getSpecies(),
            pet.getBreed(),
            ownerId
    );
}
```

**Evidence (screenshot filename or curl/Postman output):**
```http
PS C:\Users\figue\JOF-WebDev2> curl.exe -i http://localhost:8080/api/pets
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 14 Nov 2026 03:39:15 GMT

[{"id":1,"microchipNumber":"MC-0001","name":"Buddy","species":"Dog","breed":"Labrador","ownerId":null},{"id":2,"microchipNumber":"MC-0002","name":"Whiskers","species":"Cat","breed":"Siamese","ownerId":null},{"id":3,"microchipNumber":"MC-0003","name":"Bruno","species":"Dog","breed":"Aspin","ownerId":null},{"id":7,"microchipNumber":"MC-2001","name":"Mochi","species":"Dog","breed":"Shih Tzu","ownerId":1}]

PS C:\Users\figue\JOF-WebDev2> curl.exe -i http://localhost:8080/api/pets/8
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 14 Nov 2026 03:45:33 GMT

{"id":8,"microchipNumber":"MC-3001","name":"Luna","species":"Cat","breed":"Persian","ownerId":1}

PS C:\Users\figue\JOF-WebDev2> curl.exe -i http://localhost:8080/api/pets/999
HTTP/1.1 404 
Content-Length: 0
Date: Sat, 14 Nov 2026 03:39:23 GMT
```

**Reflection question:**
_What would go wrong if you returned the JPA entity directly from `@GetMapping("/{id}")` instead of a DTO?_

Your answer:
If I returned the `Pet` entity directly, Jackson would try to serialize its lazy-loaded relationships (`owner` and `vaccinations`) after the Hibernate session has already closed, which causes a `LazyInitializationException` and crashes with a `500` error. On top of that, it would expose internal database structure and break the API response whenever a database column is renamed.

---

## Task 3 — POST Endpoint — Create with 201 + Location

**Approach / explanation:**
`CreatePetRequest` only asks for the fields needed to register a new pet (`microchipNumber`, `name`, `species`, `breed`, `ownerId`) without letting the client pass an `id`. Inside `PetService.create()`, the DTO values are copied into a new `Pet` entity, attached to its `PetOwner` if `ownerId` is provided, saved to the database, and mapped back to `PetDTO`. The controller then builds the new URL using `ServletUriComponentsBuilder` and returns `201 Created` with the `Location` header.

**`CreatePetRequest` class code:**
```java
package com.example.dto;

public class CreatePetRequest {
    private String microchipNumber;
    private String name;
    private String species;
    private String breed;
    private Long ownerId;

    public CreatePetRequest() {}

    public String getMicrochipNumber() { return microchipNumber; }
    public void setMicrochipNumber(String microchipNumber) { this.microchipNumber = microchipNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
```

**Controller `create()` method code:**
```java
@PostMapping
public ResponseEntity<PetDTO> create(@RequestBody CreatePetRequest request) {
    PetDTO created = petService.create(request);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.getId())
            .toUri();
    return ResponseEntity.created(location).body(created);
}
```

**Evidence (screenshot filename or curl/Postman output):**
```http
PS C:\Users\figue\JOF-WebDev2> curl.exe -i -X POST http://localhost:8080/api/pets -H "Content-Type: application/json" -d '{\"microchipNumber\":\"MC-3001\",\"name\":\"Luna\",\"species\":\"Cat\",\"breed\":\"Persian\",\"ownerId\":1}'
HTTP/1.1 201 
Location: http://localhost:8080/api/pets/8
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 14 Nov 2026 03:45:18 GMT

{"id":8,"microchipNumber":"MC-3001","name":"Luna","species":"Cat","breed":"Persian","ownerId":1}
```

**Reflection question:**
_Why does the REST contract specify `201 Created` for a successful POST rather than `200 OK`, and why is the `Location` header useful to include?_

Your answer:
Returning `201 Created` clearly tells the client that a brand-new record was actually added to the database, rather than just returning existing data like `200 OK` does. Adding the `Location` header is useful because it hands the client the exact URL (like `http://localhost:8080/api/pets/8`) where they can immediately view or update that new pet without having to construct the URL manually.

---

## Task 4 — PUT, PATCH & DELETE

**Approach / explanation:**
`UpdatePetRequest` holds the fields that can be updated on a pet. In `PetService.update()` (`PUT`), all fields on the existing pet are overwritten with the incoming request values, whereas `PetService.patch()` (`PATCH`) checks each field with `!= null` so only the fields provided in the JSON get updated. For `DELETE`, the first call deletes the pet and returns `204 No Content`, while calling `DELETE` again on the same ID returns `404 Not Found` without throwing an error.

**`UpdatePetRequest` class code:**
```java
package com.example.dto;

public class UpdatePetRequest {
    private String microchipNumber;
    private String name;
    private String species;
    private String breed;
    private Long ownerId;

    public UpdatePetRequest() {}

    public String getMicrochipNumber() { return microchipNumber; }
    public void setMicrochipNumber(String microchipNumber) { this.microchipNumber = microchipNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
```

**Controller code for PUT, PATCH, DELETE:**
```java
@PutMapping("/{id}")
public ResponseEntity<PetDTO> update(@PathVariable Long id, @RequestBody UpdatePetRequest request) {
    return petService.update(id, request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

@PatchMapping("/{id}")
public ResponseEntity<PetDTO> patch(@PathVariable Long id, @RequestBody UpdatePetRequest request) {
    return petService.patch(id, request)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    if (!petService.delete(id)) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.noContent().build();
}
```

**Evidence (screenshot filename or curl/Postman output):**
```http
# PUT full replacement -> 200 OK
PS C:\Users\figue\JOF-WebDev2> curl.exe --% -i -X PUT http://localhost:8080/api/pets/9 -H "Content-Type: application/json" -d "{\"microchipNumber\":\"MC-3002\",\"name\":\"Luna Star\",\"species\":\"Cat\",\"breed\":\"Scottish Fold\",\"ownerId\":1}"
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 14 Nov 2026 03:49:33 GMT

{"id":9,"microchipNumber":"MC-3002","name":"Luna Star","species":"Cat","breed":"Scottish Fold","ownerId":1}

# PATCH partial update (only breed changes, name stays "Luna") -> 200 OK
PS C:\Users\figue\JOF-WebDev2> curl.exe --% -i -X PATCH http://localhost:8080/api/pets/8 -H "Content-Type: application/json" -d "{\"breed\":\"British Shorthair\"}"
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 14 Nov 2026 03:47:42 GMT

{"id":8,"microchipNumber":"MC-3001","name":"Luna","species":"Cat","breed":"British Shorthair","ownerId":1}

# First DELETE -> 204 No Content
PS C:\Users\figue\JOF-WebDev2> curl.exe -i -X DELETE http://localhost:8080/api/pets/8
HTTP/1.1 204 
Date: Sat, 14 Nov 2026 03:47:53 GMT

# Second DELETE (idempotency check) -> 404 Not Found
PS C:\Users\figue\JOF-WebDev2> curl.exe -i -X DELETE http://localhost:8080/api/pets/8
HTTP/1.1 404 
Content-Length: 0
Date: Sat, 14 Nov 2026 03:48:21 GMT
```

**Reflection question:**
_What is the difference between `PUT` and `PATCH` in terms of what the client sends, and what must the service-layer `patch()` method do differently from `update()`?_

Your answer:
With `PUT`, the client has to send the entire object with every field included because it replaces the whole record, meaning any omitted fields get overwritten with `null`. With `PATCH`, the client only sends the specific field(s) they want to change, so the service's `patch()` method has to check each field for `!= null` first and only update the ones that were actually sent, leaving everything else untouched.

---

## Task 5 — Full DTO Layer Audit + Capstone Milestone 3

**DTO class list (names only — confirm no entity class names appear):**
- `CreatePetRequest`
- `UpdatePetRequest`
- `PetDTO`

**409 Conflict scenario:**
In our veterinary clinic system, every pet must have a unique `microchipNumber`. When a `POST /api/pets` request tries to register a microchip number that is already saved in the database, `PetService.create()` throws `DuplicateMicrochipException`, and `PetController` returns `409 Conflict` with an error message.

Request:
```http
POST /api/pets
Content-Type: application/json

{"microchipNumber":"MC-3001","name":"Duplicate","species":"Cat","breed":"Persian","ownerId":1}
```

Response:
```http
HTTP/1.1 409 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 14 Nov 2026 03:45:26 GMT

{"message":"Microchip number already registered: MC-3001"}
```

**Capstone Milestone 3 smoke-check evidence:**
```http
# 1. POST -> 201
PS C:\Users\figue\JOF-WebDev2> curl.exe -i -X POST http://localhost:8080/api/pets -H "Content-Type: application/json" -d '{\"microchipNumber\":\"MC-3001\",\"name\":\"Luna\",\"species\":\"Cat\",\"breed\":\"Persian\",\"ownerId\":1}'
HTTP/1.1 201 
Location: http://localhost:8080/api/pets/8
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 14 Nov 2026 03:45:18 GMT

{"id":8,"microchipNumber":"MC-3001","name":"Luna","species":"Cat","breed":"Persian","ownerId":1}

# 2. GET /{new-id} -> 200
PS C:\Users\figue\JOF-WebDev2> curl.exe -i http://localhost:8080/api/pets/8
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 14 Nov 2026 03:45:33 GMT

{"id":8,"microchipNumber":"MC-3001","name":"Luna","species":"Cat","breed":"Persian","ownerId":1}

# 3. PUT /{id} -> 200
PS C:\Users\figue\JOF-WebDev2> curl.exe --% -i -X PUT http://localhost:8080/api/pets/9 -H "Content-Type: application/json" -d "{\"microchipNumber\":\"MC-3002\",\"name\":\"Luna Star\",\"species\":\"Cat\",\"breed\":\"Scottish Fold\",\"ownerId\":1}"
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 14 Nov 2026 03:49:33 GMT

{"id":9,"microchipNumber":"MC-3002","name":"Luna Star","species":"Cat","breed":"Scottish Fold","ownerId":1}

# 4. PATCH /{id} -> 200 (other fields unchanged)
PS C:\Users\figue\JOF-WebDev2> curl.exe --% -i -X PATCH http://localhost:8080/api/pets/8 -H "Content-Type: application/json" -d "{\"breed\":\"British Shorthair\"}"
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 14 Nov 2026 03:47:42 GMT

{"id":8,"microchipNumber":"MC-3001","name":"Luna","species":"Cat","breed":"British Shorthair","ownerId":1}

# 5. DELETE /{id} -> 204
PS C:\Users\figue\JOF-WebDev2> curl.exe -i -X DELETE http://localhost:8080/api/pets/8
HTTP/1.1 204 
Date: Sat, 14 Nov 2026 03:47:53 GMT

# 6. GET /{deleted-id} -> 404
PS C:\Users\figue\JOF-WebDev2> curl.exe -i http://localhost:8080/api/pets/8
HTTP/1.1 404 
Content-Length: 0
Date: Sat, 14 Nov 2026 03:48:28 GMT
```

- [x] `POST` → `201`
- [x] `GET /{new-id}` → `200`
- [x] `PUT /{id}` → `200`
- [x] `PATCH /{id}` → `200` (other fields unchanged)
- [x] `DELETE /{id}` → `204`
- [x] `GET /{deleted-id}` → `404`

**Reflection question:**
_For one endpoint in your capstone API, state: the method, the URL, what status code it returns on success, what status code it returns when the resource is not found, and why there is no JPA entity in the controller method signature._

Your answer:
For `PATCH /api/pets/{id}`, it returns `200 OK` with the updated `PetDTO` when the update succeeds, and `404 Not Found` if no pet exists with that ID. Neither `Pet` nor `PetOwner` appears in the controller method signature because keeping JPA entities inside the service layer avoids `LazyInitializationException` errors during JSON serialization and keeps our public API contract separate from the database tables.

---

## Self-Check

- [x] All Week 10 work on the `week10` branch (project at branch root, no `week10/` folder)
- [x] This file completed and renamed `answers.md`
- [x] No JPA entity class appears in any controller method signature
- [x] Repository link pasted into Moodle (no files uploaded)