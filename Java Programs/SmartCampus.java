import java.time.LocalDate;
import java.util.*;

public class SmartCampus {

    private static final Scanner scanner = new Scanner(System.in);
    private static final LocalDate DEADLINE = LocalDate.of(2026, 1, 31);

    private static Map<String, Student> students = new HashMap<>();
    private static Map<String, Course> courses = new HashMap<>();
    private static Map<String, List<Registration>> registrations = new HashMap<>();

    // ====================== INNER CLASSES ======================

    static class Student {
        String id;
        String name;
        String email;
        String category;

        Student(String id, String name, String email, String category) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.category = category;
        }
    }

    static class Course {
        String code;
        String title;
        int credits;
        double feePerCredit;
        int capacity;
        int enrolled;

        Course(String code, String title, int credits, double feePerCredit, int capacity) {
            this.code = code;
            this.title = title;
            this.credits = credits;
            this.feePerCredit = feePerCredit;
            this.capacity = capacity;
            this.enrolled = 0;
        }
    }

    static class Registration {
        String courseCode;
        LocalDate registrationDate;

        Registration(String courseCode) {
            this.courseCode = courseCode;
            this.registrationDate = LocalDate.now();
        }
    }

    // ====================== MAIN METHOD ======================

    public static void main(String[] args) {

        while (true) {
            printMenu();
            int choice = getIntegerInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    addCourse();
                    break;
                case 3:
                    registerStudent();
                    break;
                case 4:
                    dropCourse();
                    break;
                case 5:
                    viewStudentSummary();
                    break;
                case 6:
                    generateBill();
                    break;
                case 7:
                    exportNotification();
                    break;
                case 8:
                    System.out.println("Exiting program...");
                    return;
                default:
                    System.out.println("Invalid menu choice. Please try again.");
            }
        }
    }
 
    // ====================== MENU ======================

    private static void printMenu() {
        System.out.println("\n========== SMART CAMPUS MENU ==========");
        System.out.println("1. Add Student");
        System.out.println("2. Add Course");
        System.out.println("3. Register Student for Course");
        System.out.println("4. Drop Course");
        System.out.println("5. View Student Summary");
        System.out.println("6. Generate Bill");
        System.out.println("7. Export Notification");
        System.out.println("8. Exit");
    }

    // ====================== ADD STUDENT ======================

    private static void addStudent() {

        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().trim();

        if (students.containsKey(id)) {
            System.out.println("Error: Student ID already exists.");
            return;
        }

        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();

        if (!isValidEmail(email)) {
            System.out.println("Error: Invalid email format.");
            return;
        }

        System.out.print("Enter Category (Regular / Scholarship / International): ");
        String category = scanner.nextLine().trim();

        if (!isValidCategory(category)) {
            System.out.println("Error: Invalid category.");
            return;
        }

        students.put(id, new Student(id, name, email, category));
        registrations.put(id, new ArrayList<>());

        System.out.println("Student added successfully.");
    }

    // ====================== ADD COURSE ======================

    private static void addCourse() {

        System.out.print("Enter Course Code: ");
        String code = scanner.nextLine().trim();

        if (courses.containsKey(code)) {
            System.out.println("Error: Course code already exists.");
            return;
        }

        System.out.print("Enter Course Title: ");
        String title = scanner.nextLine().trim();

        int credits = getPositiveInteger("Enter Credits: ");
        double fee = getPositiveDouble("Enter Fee Per Credit: ");
        int capacity = getPositiveInteger("Enter Capacity: ");

        courses.put(code, new Course(code, title, credits, fee, capacity));

        System.out.println("Course added successfully.");
    }

    // ====================== REGISTER STUDENT ======================

    private static void registerStudent() {

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine().trim();

        if (!students.containsKey(studentId)) {
            System.out.println("Error: Invalid student ID.");
            return;
        }

        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine().trim();

        if (!courses.containsKey(courseCode)) {
            System.out.println("Error: Invalid course code.");
            return;
        }

        Course course = courses.get(courseCode);
        List<Registration> studentRegistrations = registrations.get(studentId);

        // Prevent duplicate registration
        for (Registration r : studentRegistrations) {
            if (r.courseCode.equals(courseCode)) {
                System.out.println("Error: Student already registered in this course.");
                return;
            }
        }

        int currentCredits = calculateTotalCredits(studentId);

        if (currentCredits + course.credits > 24) {
            System.out.println("Error: Credit limit exceeded (Maximum 24 credits).");
            return;
        }

        if (course.enrolled >= course.capacity) {
            System.out.println("Error: Course capacity is full.");
            return;
        }

        studentRegistrations.add(new Registration(courseCode));
        course.enrolled++;

        System.out.println("Registration successful.");
    }

    // ====================== DROP COURSE ======================

    private static void dropCourse() {

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine().trim();

        if (!students.containsKey(studentId)) {
            System.out.println("Error: Invalid student ID.");
            return;
        }

        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine().trim();

        List<Registration> studentRegistrations = registrations.get(studentId);
        Course course = courses.get(courseCode);

        if (course == null) {
            System.out.println("Error: Invalid course code.");
            return;
        }

        boolean removed = studentRegistrations.removeIf(r -> r.courseCode.equals(courseCode));

        if (removed) {
            course.enrolled--;
            System.out.println("Course dropped successfully.");
        } else {
            System.out.println("Error: Student is not registered in this course.");
        }
    }

    // ====================== VIEW SUMMARY ======================

    private static void viewStudentSummary() {

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine().trim();

        if (!students.containsKey(studentId)) {
            System.out.println("Error: Invalid student ID.");
            return;
        }

        Student student = students.get(studentId);
        List<Registration> studentRegistrations = registrations.get(studentId);

        System.out.println("\n------ STUDENT SUMMARY ------");
        System.out.println("ID: " + student.id);
        System.out.println("Name: " + student.name);
        System.out.println("Email: " + student.email);
        System.out.println("Category: " + student.category);

        int totalCredits = 0;

        System.out.println("Registered Courses:");
        for (Registration r : studentRegistrations) {
            Course c = courses.get(r.courseCode);
            System.out.println("- " + c.code + " | " + c.title + " | Credits: " + c.credits);
            totalCredits += c.credits;
        }

        System.out.println("Total Credits: " + totalCredits);
    }

    // ====================== GENERATE BILL ======================

    private static void generateBill() {

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine().trim();

        if (!students.containsKey(studentId)) {
            System.out.println("Error: Invalid student ID.");
            return;
        }

        Student student = students.get(studentId);
        List<Registration> studentRegistrations = registrations.get(studentId);

        double tuition = 0;
        boolean lateRegistration = false;

        for (Registration r : studentRegistrations) {
            Course c = courses.get(r.courseCode);
            tuition += c.credits * c.feePerCredit;

            if (r.registrationDate.isAfter(DEADLINE)) {
                lateRegistration = true;
            }
        }

        // Apply category rules
        if (student.category.equalsIgnoreCase("Scholarship")) {
            tuition *= 0.5;
        } else if (student.category.equalsIgnoreCase("International")) {
            tuition *= 1.2;
        }

        double total = tuition + 200; // Service charge

        if (lateRegistration) {
            total += 500;
        }

        System.out.println("\n------ BILL DETAILS ------");
        System.out.println("Tuition: ₹" + tuition);
        System.out.println("Service Charge: ₹200");

        if (lateRegistration) {
            System.out.println("Late Registration Fee: ₹500");
        }

        System.out.println("Total Payable: ₹" + total);
    }

    // ====================== EXPORT NOTIFICATION ======================

    private static void exportNotification() {

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine().trim();

        if (!students.containsKey(studentId)) {
            System.out.println("Error: Invalid student ID.");
            return;
        }

        System.out.println("\n========== REGISTRATION NOTIFICATION ==========");
        viewStudentSummary();
        generateBill();
        System.out.println("===============================================");
    }

    // ====================== HELPER METHODS ======================

    private static boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    private static boolean isValidCategory(String category) {
        return category.equalsIgnoreCase("Regular")
                || category.equalsIgnoreCase("Scholarship")
                || category.equalsIgnoreCase("International");
    }

    private static int getIntegerInput(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid number. Please enter again.");
            }
        }
    }

    private static int getPositiveInteger(String message) {
        int value;
        do {
            value = getIntegerInput(message);
            if (value <= 0) {
                System.out.println("Value must be positive.");
            }
        } while (value <= 0);
        return value;
    }

    private static double getPositiveDouble(String message) {
        while (true) {
            try {
                System.out.print(message);
                double value = Double.parseDouble(scanner.nextLine());
                if (value > 0) {
                    return value;
                }
                System.out.println("Value must be positive.");
            } catch (Exception e) {
                System.out.println("Invalid number. Please enter again.");
            }
        }
    }

    private static int calculateTotalCredits(String studentId) {
        int total = 0;
        for (Registration r : registrations.get(studentId)) {
            total += courses.get(r.courseCode).credits;
        }
        return total;
    }
}
