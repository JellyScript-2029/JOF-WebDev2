# WEBDEV2 — Week 9 Lab Answers

Name: Jhelianne Figuerres
Student ID: 2510465
GitHub repo: https://github.com/JellyScript-2029/JOF-WebDev2
Date submitted: November 12, 2026

---

## Task 1 — One-to-One + One-to-Many Mappings

**Approach / explanation:**
`User` owns the one-to-one through `@JoinColumn(name = "profile_id")`, and `UserProfile` mirrors it with `mappedBy = "profile"` (the Java field name). In `Employee`, the `@ManyToOne` with `@JoinColumn(name = "department_id", nullable = false)` owns the foreign key, and `Department` mirrors it with `@OneToMany(mappedBy = "department")`. `Department.addEmployee()` and `removeEmployee()` update both sides so the foreign key is never null.

**Code:**
```java
// User (owning 1:1)
@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JoinColumn(name = "profile_id", unique = true)
private UserProfile profile;

// UserProfile (inverse 1:1)
@OneToOne(mappedBy = "profile", fetch = FetchType.LAZY)
private User user;

// Employee (owning N:1)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "department_id", nullable = false)
private Department department;

// Department (inverse 1:N). Started as cascade = ALL for Task 1; narrowed in Task 4.
@OneToMany(mappedBy = "department",
           cascade = {CascadeType.PERSIST, CascadeType.MERGE},
           orphanRemoval = true)
private List<Employee> employees = new ArrayList<>();

public void addEmployee(Employee e) { employees.add(e); e.setDepartment(this); }
public void removeEmployee(Employee e) { employees.remove(e); e.setDepartment(null); }
```

**Evidence:**
Seeder log (Hibernate inserts, then "Week 9 seed done"):
```text
insert into user_profile (bio) values (?)     -- bio = Alice's profile
insert into app_user (profile_id, username) values (?, ?)   -- profile_id = 1, username = alice
insert into department (name) values (?)      -- Engineering
insert into employee (department_id, name) values (?, ?)    -- department_id = 1, Ana
insert into employee (department_id, name) values (?, ?)    -- department_id = 1, Ben
Week 9 seed done: users=1, departments=1, students=2, courses=2
```
`curl.exe -i http://localhost:8080/api/week9/departments`
```text
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 12 Nov 2026 08:49:27 GMT

[{"id":1,"name":"Engineering","employees":[{"id":1,"name":"Ana"},{"id":2,"name":"Ben"}]}]
```
`curl.exe -i http://localhost:8080/api/week9/users`
```text
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 12 Nov 2026 08:49:36 GMT

[{"id":1,"username":"alice","profileBio":"Alice's profile"}]
```

**Reflection question:**
Your answer:
The `Employee` entity owns the `Department`–`Employee` relationship because the foreign key `department_id` lives in the employee table, on the "many" side. Only the owning side writes the FK. `mappedBy` must contain the Java field name in `Employee` that points back to the department, which is `"department"`. It must not be the column name `department_id`.

---

## Task 2 — Many-to-Many Join Table + Association Entity

**Approach / explanation:**
I first mapped `Student`–`Course` with a direct `@ManyToMany` and `@JoinTable(name = "student_course")`, which created a hidden link table. Because an enrollment needs its own data (a date and a grade), I replaced it with an `Enrollment` entity that has two `@ManyToOne` mappings. A unique constraint `uk_student_course` on `(student_id, course_id)` blocks duplicate enrollments.

**Code:**
```java
// Step 1: direct ManyToMany (Student, owning side)
@ManyToMany
@JoinTable(name = "student_course",
           joinColumns = @JoinColumn(name = "student_id"),
           inverseJoinColumns = @JoinColumn(name = "course_id"))
private List<Course> courses = new ArrayList<>();
// Course (inverse): @ManyToMany(mappedBy = "courses") private List<Student> students;

// Step 2: association entity
@Entity
@Table(name = "enrollment",
       uniqueConstraints = @UniqueConstraint(name = "uk_student_course",
                                             columnNames = {"student_id", "course_id"}))
public class Enrollment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private LocalDate enrollmentDate;
    private String grade;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "student_id", nullable = false) private Student student;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "course_id", nullable = false) private Course course;
}
```

**Evidence:**
Schema / SQL log showing `student_course` (step 1):
```text
create table student_course (
    student_id bigint not null,
    course_id bigint not null
) engine=InnoDB

alter table student_course 
   add constraint FKejrkh4gv8iqgmspsanaji90ws 
   foreign key (course_id) 
   references course (id)

alter table student_course 
   add constraint FKq7yw2wg9wlt2cnj480hcdn6dq 
   foreign key (student_id) 
   references student (id)
```
Schema showing `enrollment` with the unique key (step 2), from the Hibernate startup log:
```text
create table enrollment (
    id bigint not null auto_increment,
    enrollment_date date,
    grade varchar(255),
    course_id bigint not null,
    student_id bigint not null,
    primary key (id)
) engine=InnoDB

alter table enrollment
   add constraint uk_student_course unique (student_id, course_id)

alter table enrollment
   add constraint FKbhhcqkw1px6yljqg92m0sh2gt
   foreign key (course_id)
   references course (id)

alter table enrollment
   add constraint FKio7fsy3vhvfgv7c0gjk15nyk4
   foreign key (student_id)
   references student (id)
```
Successful enroll:
`curl.exe -i -X POST "http://localhost:8080/api/week9/enrollments?studentId=1&courseId=1&grade=A"`
```text
HTTP/1.1 201
Content-Length: 0
Date: Thu, 12 Nov 2026 08:50:42 GMT
```
Duplicate rejected (same command again):
```text
HTTP/1.1 409
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 12 Nov 2026 08:50:52 GMT

{"error":"Constraint violation (e.g. duplicate enrollment)"}
```
Console line for the duplicate:
```text
2026-11-12T16:50:52.108+08:00 DEBUG --- org.hibernate.SQL :
    insert
    into
        enrollment
        (course_id, enrollment_date, grade, student_id)
    values
        (?, ?, ?, ?)
2026-11-12T16:50:52.111+08:00 TRACE --- org.hibernate.orm.jdbc.bind : binding parameter (1:BIGINT) <- [1]
2026-11-12T16:50:52.111+08:00 TRACE --- org.hibernate.orm.jdbc.bind : binding parameter (2:DATE) <- [2026-11-12]
2026-11-12T16:50:52.111+08:00 TRACE --- org.hibernate.orm.jdbc.bind : binding parameter (3:VARCHAR) <- [A]
2026-11-12T16:50:52.112+08:00 TRACE --- org.hibernate.orm.jdbc.bind : binding parameter (4:BIGINT) <- [1]
2026-11-12T16:50:52.126+08:00  WARN --- org.hibernate.orm.jdbc.error : HHH000247: ErrorCode: 1062, SQLState: 23000
2026-11-12T16:50:52.128+08:00  WARN --- org.hibernate.orm.jdbc.error : Duplicate entry '1-1' for key 'enrollment.uk_student_course'
```

**Reflection question:**
Your answer:
A many-to-many must be modeled as an association entity when the link itself carries data, such as a grade, date, role or quantity, or when you need to query or constrain the link. A plain `@ManyToMany` hides the join table, so there is nowhere to put those columns. In my test, the unique constraint on `(student_id, course_id)` stopped the same student from being enrolled in the same course twice.

---

## Task 3 — Fetch Strategies Experiment — LAZY vs EAGER

**Approach / explanation:**
All associations are LAZY, and I set `spring.jpa.open-in-view=false` so the session closes after the repository call. Calling `employee.getDepartment().getName()` in a non-transactional service method then throws `LazyInitializationException`. I fixed it two ways: (a) `@Transactional(readOnly = true)` on the service method, and (b) a repository query with `JOIN FETCH e.department`.

**Code:**
```java
// broken: no @Transactional
public String departmentNameBroken(Long empId) {
    Employee e = employeeRepository.findById(empId).orElseThrow();
    return e.getDepartment().getName();   // LazyInitializationException
}

// fix (a)
@Transactional(readOnly = true)
public String departmentNameTransactional(Long empId) { /* same body */ }

// fix (b)
@Query("SELECT e FROM Employee e JOIN FETCH e.department WHERE e.id = :id")
Optional<Employee> findWithDepartmentById(@Param("id") Long id);
```

**Evidence:**
`curl.exe -i "http://localhost:8080/api/week9/employees/1/department-name?mode=broken"` (expect 500):
```text
HTTP/1.1 500
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 12 Nov 2026 09:02:19 GMT
Connection: close

{"timestamp":"2026-11-12T09:02:19.849Z","status":500,"error":"Internal Server Error","path":"/api/week9/employees/1/department-name"}
```
Stack trace excerpt from the console:
```text
2026-11-12T17:02:19.752+08:00 TRACE 508 --- [io-8080-exec-10] org.hibernate.orm.jdbc.bind : binding parameter (1:BIGINT) <- [1]
2026-11-12T17:02:19.778+08:00 ERROR 508 --- [io-8080-exec-10] o.a.c.c.C.[.[.[/].[dispatcherServlet] : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed: org.hibernate.LazyInitializationException: Could not initialize proxy [com.example.entity.Department#1] - no session] with root cause

org.hibernate.LazyInitializationException: Could not initialize proxy [com.example.entity.Department#1] - no session
        at org.hibernate.proxy.AbstractLazyInitializer.initialize(AbstractLazyInitializer.java:172)
        at org.hibernate.proxy.AbstractLazyInitializer.getImplementation(AbstractLazyInitializer.java:326)
        at org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor.intercept(ByteBuddyInterceptor.java:42)
        at com.example.entity.Department$HibernateProxy.getName(Unknown Source)
        at com.example.service.Week9Service.departmentNameBroken(Week9Service.java:130)
        at com.example.controller.Week9Controller.departmentName(Week9Controller.java:74)
        ... (framework frames omitted)
```
The SELECT that ran before the error (employee only, no department query):
```text
select e1_0.id, e1_0.department_id, e1_0.name from employee e1_0 where e1_0.id=?
```
`mode=tx` response and SQL log:
```text
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 12 Nov 2026 09:11:02 GMT

{"department":"Engineering"}

2026-11-12T17:11:02.875+08:00 DEBUG --- org.hibernate.SQL :
    select
        e1_0.id,
        e1_0.department_id,
        e1_0.name
    from
        employee e1_0
    where
        e1_0.id=?
2026-11-12T17:11:02.878+08:00 TRACE --- org.hibernate.orm.jdbc.bind : binding parameter (1:BIGINT) <- [1]
2026-11-12T17:11:02.883+08:00 DEBUG --- org.hibernate.SQL :
    select
        d1_0.id,
        d1_0.name
    from
        department d1_0
    where
        d1_0.id=?
2026-11-12T17:11:02.884+08:00 TRACE --- org.hibernate.orm.jdbc.bind : binding parameter (1:BIGINT) <- [1]
```
`mode=fetch` response and SQL log:
```text
HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 12 Nov 2026 09:42:09 GMT

{"department":"Engineering"}

2026-11-12T17:42:09.918+08:00 DEBUG 508 --- [nio-8080-exec-4] org.hibernate.SQL                        : 
    select
        e1_0.id,
        e1_0.department_id,
        d1_0.id,
        d1_0.name,
        e1_0.name 
    from
        employee e1_0 
    join
        department d1_0 
            on d1_0.id=e1_0.department_id 
    where
        e1_0.id=?
2026-11-12T17:42:09.932+08:00 TRACE 508 --- [nio-8080-exec-4] org.hibernate.orm.jdbc.bind              : binding parameter (1:BIGINT) <- [1]
```

**Observation:**
The lazy failure ran 1 query and then threw an exception; the transactional version ran 2 queries (employee, then department); the fetch-join version ran 1 query with a join. Blanket EAGER on collections is avoided because it forces Hibernate to load unnecessary related data on every query, causing large joins and N+1 query performance issues.

**Reflection question:**
Your answer:
`LazyInitializationException` happens when a lazy association is read after the persistence context has closed, so Hibernate has no open session to run the deferred SELECT. Preferring LAZY and fetching explicitly (a fetch join or `@EntityGraph`) loads only what each use case needs. Marking everything EAGER makes every query load related data it may not use, which causes large joins and N+1 queries.

---

## Task 4 — Cascade + orphanRemoval — Safe vs. Dangerous

**Approach / explanation:**
For the safe composition case, `Department.employees` uses `cascade = {PERSIST, MERGE}` with `orphanRemoval = true`. Removing an employee from the collection deletes its row. For the shared `Student`–`Course` path, I keep cascade to PERSIST/MERGE only and never use REMOVE or `orphanRemoval`. Deleting a student removes its enrollment rows first and leaves the `Course` row alone.

**Code:**
```java
// Safe: private children
@OneToMany(mappedBy = "department",
           cascade = {CascadeType.PERSIST, CascadeType.MERGE},
           orphanRemoval = true)
private List<Employee> employees = new ArrayList<>();

// Shared: no REMOVE, no orphanRemoval
@OneToMany(mappedBy = "student", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
private List<Enrollment> enrollments = new ArrayList<>();

// Dangerous (do NOT use): @ManyToMany(cascade = CascadeType.ALL) on Student.courses
// deleting one Student would also delete the shared Course rows.
```

**Evidence:**
(a) Orphan delete. Before, `SELECT * FROM employee;`:
```text
mysql> SELECT * FROM employee;
+----+------+---------------+
| id | name | department_id |
+----+------+---------------+
|  1 | Ana  |             1 |
|  2 | Ben  |             1 |
+----+------+---------------+
2 rows in set (0.044 sec)
```
`curl.exe -i -X DELETE http://localhost:8080/api/week9/departments/1/employees/2`:
```text
HTTP/1.1 204
Date: Thu, 12 Nov 2026 10:06:01 GMT
```
After, `SELECT * FROM employee;` and the SQL log line for the delete:
```text
mysql> SELECT * FROM employee;
+----+------+---------------+
| id | name | department_id |
+----+------+---------------+
|  1 | Ana  |             1 |
+----+------+---------------+
1 row in set (0.015 sec)

2026-11-12T18:06:01.662+08:00 DEBUG 508 --- [nio-8080-exec-6] org.hibernate.SQL                        : 
    delete 
    from
        employee 
    where
        id=?
2026-11-12T18:06:01.662+08:00 TRACE 508 --- [nio-8080-exec-6] org.hibernate.orm.jdbc.bind              : binding parameter (1:BIGINT) <- [2]
```
(b) Shared Course survives. Before, `SELECT * FROM student;` and `SELECT * FROM course;`:
```text
mysql> SELECT * FROM student;
+----+-------+
| id | name  |
+----+-------+
|  1 | Carla |
|  2 | Dan   |
+----+-------+
2 rows in set (0.030 sec)

mysql> SELECT * FROM course;
+----+---------+
| id | title   |
+----+---------+
|  1 | CS101   |
|  2 | MATH101 |
+----+---------+
2 rows in set (0.023 sec)
```
`curl.exe -i -X DELETE http://localhost:8080/api/week9/students/1`:
```text
HTTP/1.1 204 
Date: Thu, 12 Nov 2026 10:08:23 GMT
```
After, `SELECT * FROM student;` and `SELECT * FROM course;` (the courses are still there):
```text
mysql> SELECT * FROM student;
+----+------+
| id | name |
+----+------+
|  2 | Dan  |
+----+------+
1 row in set (0.012 sec)

mysql> SELECT * FROM course;
+----+---------+
| id | title   |
+----+---------+
|  1 | CS101   |
|  2 | MATH101 |
+----+---------+
2 rows in set (0.010 sec)
```

**Reflection question:**
Your answer:
`CascadeType.REMOVE` deletes the children when the parent itself is deleted, while `orphanRemoval = true` deletes a child when it is removed from the parent's collection. Both fit a private child that can't exist without its parent, such as an order item or a department's employee record. Both are dangerous on shared entities such as a course, because removing a link or deleting one student could delete data other students still use.

---

## Task 5 — Capstone Milestone 2 — ERD to JPA Entity Model

**Approach / explanation:**
From my Veterinary Clinic Milestone 1 ERD, I modeled four core entities: `PetOwner`, `Pet`, `Vaccine`, and `PetVaccination`. Because one owner can register multiple pets, `Pet` is the owning side of the 1:N relationship and holds the `owner_id` foreign key via `@JoinColumn(name = "owner_id")`, while `PetOwner` mirrors it with `mappedBy = "owner"` and `addPet()`/`removePet()` helpers. Because a pet can receive multiple vaccines and each immunization event stores extra link attributes (`administeredDate` and `nextDueDate`), I modeled the N:M relationship as an association entity `PetVaccination` holding two `@ManyToOne` foreign keys (`pet_id`, `vaccine_id`) and a composite unique constraint `uk_pet_vaccine`.

**ERD → entity table:**
| ERD entity | Class | Relationship | Cardinality | Owning side | FK / join table | Fetch | Cascade (+ reason) |
|---|---|---|---|---|---|---|---|
| `PetOwner` | `PetOwner` | `PetOwner` — `Pet` | 1:N (inverse) | `Pet` | `pet.owner_id` | `LAZY` (collections lazy by default) | `PERSIST, MERGE` (saving owner saves new pets; no `REMOVE` so deleting an owner doesn't wipe pet records) |
| `Pet` | `Pet` | `Pet` — `PetOwner` | N:1 (owning) | `Pet` | `@JoinColumn(name = "owner_id")` | `LAZY` (avoid eager parent join on every pet query) | None (child never cascades up to parent) |
| `Pet` | `Pet` | `Pet` — `PetVaccination` | 1:N (inverse) | `PetVaccination` | `pet_vaccination.pet_id` | `LAZY` (collections lazy by default) | `PERSIST, MERGE` + `orphanRemoval = true` (vaccination logs belong privately to a pet) |
| `Vaccine` | `Vaccine` | `Vaccine` — `PetVaccination` | 1:N (inverse) | `PetVaccination` | `pet_vaccination.vaccine_id` | `LAZY` (avoid loading all clinic records per vaccine) | `PERSIST, MERGE` only (shared catalog item; must survive pet/record deletion) |
| `PetVaccination` | `PetVaccination` | Association (`Pet` ↔ `Vaccine`) | N:1 + N:1 (owning) | `PetVaccination` | `@JoinColumn(name = "pet_id")`, `@JoinColumn(name = "vaccine_id")`, `uk_pet_vaccine` | `LAZY` on both `@ManyToOne` links (load explicitly per query) | None (links to parents; never cascades deletes to shared `Pet` or `Vaccine`) |

**Code:**
```java
@Entity
@Table(name = "pet_owner")
public class PetOwner {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String fullName;
    @Column(nullable = false, unique = true) private String email;

    @OneToMany(mappedBy = "owner", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Pet> pets = new ArrayList<>();

    public void addPet(Pet pet) { pets.add(pet); pet.setOwner(this); }
    public void removePet(Pet pet) { pets.remove(pet); pet.setOwner(null); }
}

@Entity
public class Pet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false) private String microchipNumber;
    private String name;
    private String species;
    private String breed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private PetOwner owner;

    @OneToMany(mappedBy = "pet", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<PetVaccination> vaccinations = new ArrayList<>();
}

@Entity
public class Vaccine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String name;
    private String manufacturer;

    @OneToMany(mappedBy = "vaccine", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<PetVaccination> vaccinations = new ArrayList<>();
}

@Entity
@Table(name = "pet_vaccination",
       uniqueConstraints = @UniqueConstraint(name = "uk_pet_vaccine", columnNames = {"pet_id", "vaccine_id"}))
public class PetVaccination {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private LocalDate administeredDate;
    private LocalDate nextDueDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vaccine_id", nullable = false)
    private Vaccine vaccine;
}

// Repositories
public interface PetOwnerRepository extends JpaRepository<PetOwner, Long> {}
public interface PetRepository extends JpaRepository<Pet, Long> {}
public interface VaccineRepository extends JpaRepository<Vaccine, Long> {}
public interface PetVaccinationRepository extends JpaRepository<PetVaccination, Long> {
    List<PetVaccination> findByPetId(Long petId);
}

// Smoke test (CapstoneSmokeTestRunner)
@Component
public class CapstoneSmokeTestRunner implements CommandLineRunner {
    @Override
    @Transactional
    public void run(String... args) {
        if (ownerRepository.count() > 0) return;

        PetOwner owner = new PetOwner("Jhelianne Figuerres", "jhelianne@example.com");
        Pet pet = new Pet("MC-2001", "Mochi", "Dog", "Shih Tzu");
        owner.addPet(pet);
        ownerRepository.save(owner);

        Vaccine rabies = vaccineRepository.save(new Vaccine("Anti-Rabies", "Zoetis"));
        PetVaccination record = new PetVaccination(
                pet, rabies, LocalDate.now(), LocalDate.now().plusYears(1));
        vaccinationRepository.save(record);

        PetOwner savedOwner = ownerRepository.findById(owner.getId()).orElseThrow();
        PetVaccination savedRecord = vaccinationRepository.findByPetId(pet.getId()).get(0);

        System.out.println("Capstone smoke test passed: owner=" + savedOwner.getFullName()
                + ", pets=" + savedOwner.getPets().size()
                + ", petName=" + savedRecord.getPet().getName()
                + ", vaccine=" + savedRecord.getVaccine().getName()
                + ", nextDue=" + savedRecord.getNextDueDate());
    }
}
```

**Evidence:**
ERD (image or link):
Veterinary Clinic ERD: `PetOwner` (1) ──< (N) `Pet` (1) ──< (N) `PetVaccination` (N) >── (1) `Vaccine`

Smoke-test output:
```text
Hibernate: 
    insert 
    into
        pet_owner
        (email, full_name) 
    values
        (?, ?)
Hibernate: 
    insert 
    into
        pet
        (breed, microchip_number, name, owner_id, species) 
    values
        (?, ?, ?, ?, ?)
Hibernate: 
    insert 
    into
        vaccine
        (manufacturer, name) 
    values
        (?, ?)
Hibernate: 
    insert 
    into
        pet_vaccination
        (administered_date, next_due_date, pet_id, vaccine_id) 
    values
        (?, ?, ?, ?)
Hibernate: 
    select
        pv1_0.id,
        pv1_0.administered_date,
        pv1_0.next_due_date,
        pv1_0.pet_id,
        pv1_0.vaccine_id 
    from
        pet_vaccination pv1_0 
    where
        pv1_0.pet_id=?
Capstone smoke test passed: owner=Jhelianne Figuerres, pets=1, petName=Mochi, vaccine=Anti-Rabies, nextDue=2027-11-12
```

**Reflection question:**
Your answer:
In the `Pet`–`Vaccine` relationship, the cardinality is many-to-many (N:M), modeled via the `PetVaccination` association entity so each immunization can store `administeredDate` and `nextDueDate`. `PetVaccination` is the owning side and holds both foreign keys (`pet_id` and `vaccine_id`) in the `pet_vaccination` table along with a composite unique constraint `uk_pet_vaccine`. Both `@ManyToOne` links use `FetchType.LAZY` so queries do not automatically join unnecessary parent rows, and `Vaccine` restricts cascade to `PERSIST` and `MERGE` (without `REMOVE`) because vaccines are shared catalog records that must never be deleted when a single pet's vaccination record is removed.

---

## Self-Check

- [x] All Week 9 work on the `week9` branch (project at branch root, no `week9/` folder)
- [x] This file completed and renamed `answers.md`
- [x] Repository link pasted into Moodle (no files uploaded)