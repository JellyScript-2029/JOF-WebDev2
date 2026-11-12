# Lab 8: Database-Backed CRUD Application

## Task 1: Create the Repository Interface
 
### PetRepository Interface Code
 
```java
public interface PetRepository extends JpaRepository<Pet, Long> {
}
```
 
(Task 3 later adds three query methods to this same interface.)
 
### Smoke-Test Output

Console output:
```text
Hibernate: select count(*) from pet p1_0
Hibernate: insert into pet (breed,microchip_number,name,species) values (?,?,?,?)
Hibernate: insert into pet (breed,microchip_number,name,species) values (?,?,?,?)
Hibernate: insert into pet (breed,microchip_number,name,species) values (?,?,?,?)
Hibernate: select p1_0.id,p1_0.breed,p1_0.microchip_number,p1_0.name,p1_0.species from pet p1_0
Seeded pets: 3
```

MySQL CLI (`mysql -u root -p -P 3306`, then `USE webdev2_week8; SELECT * FROM pet;`):
```text
+----+----------+------------------+----------+---------+
| id | breed    | microchip_number | name     | species |
+----+----------+------------------+----------+---------+
|  1 | Labrador | MC-0001          | Buddy    | Dog     |
|  2 | Siamese  | MC-0002          | Whiskers | Cat     |
|  3 | Aspin    | MC-0003          | Bruno    | Dog     |
+----+----------+------------------+----------+---------+
3 rows in set (0.041 sec)
```
## Task 2: Implement Full CRUD via a Service Layer

### PetService Code

```java
@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet registerPet(Pet p) {
        if (p.getMicrochipNumber() == null || p.getMicrochipNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "microchipNumber is required");
        }
        p.setId(null);
        return petRepository.save(p);
    }

    @Transactional(readOnly = true)
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pet not found: " + id));
    }

    @Transactional
    public Pet updatePet(Long id, Pet updated) {
        Pet existing = getPetById(id);
        existing.setMicrochipNumber(updated.getMicrochipNumber());
        existing.setName(updated.getName());
        existing.setSpecies(updated.getSpecies());
        existing.setBreed(updated.getBreed());
        return petRepository.save(existing);
    }

    @Transactional
    public void removePet(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found: " + id);
        }
        petRepository.deleteById(id);
    }
}
```

### PetController Code

```java
@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pet create(@RequestBody Pet pet) {
        return petService.registerPet(pet);
    }

    @GetMapping
    public List<Pet> getAll() {
        return petService.getAllPets();
    }

    @GetMapping("/{id}")
    public Pet getOne(@PathVariable Long id) {
        return petService.getPetById(id);
    }

    @PutMapping("/{id}")
    public Pet update(@PathVariable Long id, @RequestBody Pet pet) {
        return petService.updatePet(id, pet);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        petService.removePet(id);
    }
}
```

### Request and Response Samples

1. POST `/api/pets` (201 Created)

Request:
```powershell
Set-Content -Path pet.json -Value '{"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Holland Lop"}'
curl.exe -i -X POST http://localhost:8080/api/pets -H "Content-Type: application/json" -d "@pet.json"
```
Response:
```http
HTTP/1.1 201
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 12 Nov 2026 06:53:14 GMT

{"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Holland Lop","id":4}
```

2. GET `/api/pets` (200 OK)

Request:
```powershell
curl.exe -i http://localhost:8080/api/pets
```
Response:
```http
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 12 Nov 2026 06:57:44 GMT

[{"microchipNumber":"MC-0001","name":"Buddy","species":"Dog","breed":"Labrador","id":1},{"microchipNumber":"MC-0002","name":"Whiskers","species":"Cat","breed":"Siamese","id":2},{"microchipNumber":"MC-0003","name":"Bruno","species":"Dog","breed":"Aspin","id":3},{"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Holland Lop","id":4}]
```

3. GET `/api/pets/{id}` (200 OK) and a missing id (404 Not Found)

Request:
```powershell
curl.exe -i http://localhost:8080/api/pets/4
curl.exe -i http://localhost:8080/api/pets/999
```
Responses:
```http
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 12 Nov 2026 06:58:32 GMT

{"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Holland Lop","id":4}

HTTP/1.1 404
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 12 Nov 2026 06:58:40 GMT

{"timestamp":"2026-11-12T06:58:40.765Z","status":404,"error":"Not Found","path":"/api/pets/999"}
```

4. PUT `/api/pets/{id}` (200 OK)

Request:
```powershell
Set-Content -Path pet-update.json -Value '{"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Mini Lop"}'
curl.exe -i -X PUT http://localhost:8080/api/pets/4 -H "Content-Type: application/json" -d "@pet-update.json"
```
Response:
```http
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 12 Nov 2026 07:00:31 GMT

{"microchipNumber":"MC-1005","name":"Bella","species":"Rabbit","breed":"Mini Lop","id":4}
```

5. DELETE `/api/pets/{id}` (204 No Content)

Request:
```powershell
curl.exe -i -X DELETE http://localhost:8080/api/pets/4
```
Response:
```http
HTTP/1.1 204
Date: Thu, 12 Nov 2026 07:01:20 GMT
```

## Task 3: Write Derived and Custom Query Methods

### Updated PetRepository Interface

```java
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findBySpeciesIgnoreCase(String species);

    List<Pet> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT DISTINCT p.species FROM Pet p")
    List<String> findAllSpecies();
}
```

The three methods are wired through `PetService` (`getBySpecies`, `searchByName`, `getAllSpecies`) and `PetController`:

```java
@GetMapping("/species")
public List<Pet> bySpecies(@RequestParam String name) { return petService.getBySpecies(name); }

@GetMapping("/search")
public List<Pet> search(@RequestParam String keyword) { return petService.searchByName(keyword); }

@GetMapping("/species-list")
public List<String> speciesList() { return petService.getAllSpecies(); }
```

### Sample JSON Responses

1. `curl.exe -i "http://localhost:8080/api/pets/species?name=dog"`
```json
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 12 Nov 2026 07:02:06 GMT

[{"microchipNumber":"MC-0001","name":"Buddy","species":"Dog","breed":"Labrador","id":1},{"microchipNumber":"MC-0003","name":"Bruno","species":"Dog","breed":"Aspin","id":3}]
```

2. `curl.exe -i "http://localhost:8080/api/pets/search?keyword=bud"`
```json
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 12 Nov 2026 07:02:19 GMT

[{"microchipNumber":"MC-0001","name":"Buddy","species":"Dog","breed":"Labrador","id":1}]
```

3. `curl.exe -i http://localhost:8080/api/pets/species-list`
```json
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 12 Nov 2026 07:02:26 GMT

["Dog","Cat"]
```
## Task 4: Apply @Transactional and Observe Rollback

### batchRegister Method Code

```java
@Transactional
public void batchRegister(List<Pet> pets) {
    for (Pet p : pets) {
        petRepository.save(p);
    }
}
```

### SQL Log and MySQL Output

Request (two pets with the same microchipNumber):
```powershell
Set-Content -Path batch.json -Value '[{"microchipNumber":"MC-9999","name":"A","species":"Dog","breed":"X"},{"microchipNumber":"MC-9999","name":"B","species":"Cat","breed":"Y"}]'
curl.exe -i -X POST http://localhost:8080/api/pets/batch -H "Content-Type: application/json" -d "@batch.json"
```

Response:
```http
HTTP/1.1 409
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 12 Nov 2026 07:04:20 GMT

{"error":"Constraint violation (e.g. duplicate microchipNumber)"}
```

SQL log:
```text
2026-11-12T15:04:20.502+08:00 DEBUG --- org.hibernate.SQL :
    insert
    into
        pet
        (breed, microchip_number, name, species)
    values
        (?, ?, ?, ?)
2026-11-12T15:04:20.503+08:00 TRACE --- org.hibernate.orm.jdbc.bind : binding parameter (1:VARCHAR) <- [X]
2026-11-12T15:04:20.503+08:00 TRACE --- org.hibernate.orm.jdbc.bind : binding parameter (2:VARCHAR) <- [MC-9999]
2026-11-12T15:04:20.504+08:00 TRACE --- org.hibernate.orm.jdbc.bind : binding parameter (3:VARCHAR) <- [A]
2026-11-12T15:04:20.504+08:00 TRACE --- org.hibernate.orm.jdbc.bind : binding parameter (4:VARCHAR) <- [Dog]
2026-11-12T15:04:20.508+08:00 DEBUG --- org.hibernate.SQL :
    insert
    into
        pet
        (breed, microchip_number, name, species)
    values
        (?, ?, ?, ?)
2026-11-12T15:04:20.509+08:00 TRACE --- org.hibernate.orm.jdbc.bind : binding parameter (1:VARCHAR) <- [Y]
2026-11-12T15:04:20.510+08:00 TRACE --- org.hibernate.orm.jdbc.bind : binding parameter (2:VARCHAR) <- [MC-9999]
2026-11-12T15:04:20.511+08:00 TRACE --- org.hibernate.orm.jdbc.bind : binding parameter (3:VARCHAR) <- [B]
2026-11-12T15:04:20.511+08:00 TRACE --- org.hibernate.orm.jdbc.bind : binding parameter (4:VARCHAR) <- [Cat]
2026-11-12T15:04:20.558+08:00  WARN --- org.hibernate.orm.jdbc.error : HHH000247: ErrorCode: 1062, SQLState: 23000
2026-11-12T15:04:20.559+08:00  WARN --- org.hibernate.orm.jdbc.error : Duplicate entry 'MC-9999' for key 'pet.UKk92of6wx70ej50626uc0vvdm7'
```

MySQL CLI after the failed batch (`USE webdev2_week8; SELECT * FROM pet;`):
```text
mysql> USE webdev2_week8;
Database changed
mysql> SELECT * FROM pet;
+----+----------+------------------+----------+---------+
| id | breed    | microchip_number | name     | species |
+----+----------+------------------+----------+---------+
|  1 | Labrador | MC-0001          | Buddy    | Dog     |
|  2 | Siamese  | MC-0002          | Whiskers | Cat     |
|  3 | Aspin    | MC-0003          | Bruno    | Dog     |
+----+----------+------------------+----------+---------+
3 rows in set (0.013 sec)
```
### Explanation

In the SQL log, the first `INSERT` statement successfully executed for the first pet (`name: "A"`, `microchip_number: "MC-9999"`). When the loop proceeded to the second pet (`name: "B"`), it attempted to insert the exact same `microchip_number` (`"MC-9999"`), which triggered a MySQL `SQLIntegrityConstraintViolationException` (`ErrorCode: 1062, SQLState: 23000: Duplicate entry 'MC-9999'`). Because `batchRegister` is annotated with `@Transactional`, Spring intercepted this unchecked `DataIntegrityViolationException` and automatically rolled back the entire transaction—including the first insert that had already succeeded. When checking the `pet` table in MySQL afterward, only the original 3 seeded pets remained, confirming atomicity.

## Task 5: Debug a Persistence Error

### Defects Found

1. `@Id` has no `@GeneratedValue`, so no primary key is generated.
2. The `species` field is missing from `Pet`, so species queries cannot work.
3. `microchipNumber` is set to null but the column is `nullable = false`.

### Three-Step Debugging Process

**Stack trace:** Running the broken persistence code threw `org.springframework.orm.jpa.JpaSystemException: Identifier of entity 'com.example.demo.model.Pet' must be manually assigned before calling 'persist()'`, caused by `org.hibernate.id.IdentifierGenerationException`. When tested with a manual ID and null microchip number, it threw `org.springframework.dao.DataIntegrityViolationException` caused by `java.sql.SQLIntegrityConstraintViolationException: Column 'microchip_number' cannot be null`.

**SQL log:** For the missing `@GeneratedValue` defect, Hibernate aborted before generating or executing any `INSERT` SQL statement because entity identifier validation failed in memory. Once fixed, attempting to save a `null` `microchipNumber` generated an `insert into pet ...` statement that failed at the database level with MySQL `ErrorCode: 1048, SQLState: 23000` (`Column 'microchip_number' cannot be null`).

**Fix:** Added `@GeneratedValue(strategy = GenerationType.IDENTITY)` to the `id` field so MySQL auto-increments primary keys, restored the `private String species;` field along with its getter and setter in `Pet.java`, and passed a valid non-null `microchipNumber` (`"MC-1006"`) when instantiating `Pet`. After restarting the application, the `INSERT` executed cleanly and persisted Coco (`id: 4`) into the `pet` table.

### Fixed Pet Entity and Service Snippet

```java
@Entity
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String microchipNumber;

    private String name;
    private String species;
    private String breed;

    // constructors, getters, setters
}
```

```java
Pet p = new Pet("MC-1006", "Coco", "Dog", "Poodle");
petRepository.save(p);
```