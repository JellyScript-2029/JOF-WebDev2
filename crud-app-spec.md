# Student Directory
 
**Description:** A web-based Student Directory built with Spring Boot, Thymeleaf and Jakarta Bean Validation. It lets users create, read, update and delete student records, which are stored in memory using a Controller-Service-Repository architecture.
 
## Requirements
 
- REQ-1: The user can view a table listing all students (name, email, course, age) with links to add, edit and delete.
- REQ-2: The user can create a new student through a form; the name and email must not be blank, the email must be valid, and the age cannot be negative, otherwise inline error messages are shown.
- REQ-3: The user can edit an existing student's details through a pre-filled form, with the same validation errors shown on invalid input.
- REQ-4: The user can delete a student from the directory, and the student no longer appears in the list afterwards.
 