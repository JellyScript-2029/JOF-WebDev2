package com.jeehli;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Roster roster = new Roster();

        System.out.println("\nTesting Invalid Input Handling");
        try {
            Student invalidStudent = new Student("Invalid User", -5, "BSIT");
            roster.addStudent(invalidStudent);
        } catch (InvalidAgeException e) {
            System.out.println("Handled Exception: " + e.getMessage());
        }

        System.out.println("\nPopulating Roster");
        roster.addStudent(new Student("Daniel Caesar", 20, "BSIT"));
        roster.addStudent(new Student("Don Toliver", 21, "BSCS"));
        roster.addStudent(new Student("Abel Tesfaye", 21, "BSIS"));
        roster.addStudent(new Student("Taylor Swift", 23, "BSIT"));
        roster.addStudent(new Student("Bae Nara", 21, "BSCS"));
        roster.addStudent(new Student("Frank Ocean", 18, "BSIS"));

        System.out.println("\nFull Student List:");
        for (Student s : roster.getStudents()) {
            System.out.println(s + " | Standing: " + s.computeStanding());
        }

        System.out.println("\nStream-Based Roster Report");
        List<String> adultNames = roster.generateAdultReport();
        System.out.println("Names list: " + adultNames);
    }
}
