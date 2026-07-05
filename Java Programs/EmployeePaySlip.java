import java.util.Scanner;

// Base Class
class Employee {
    String emp_name;
    int emp_id;
    String address;
    String mail_id;
    String mobile_no;

    void getEmployeeDetails(Scanner sc) {
        System.out.print("Enter Employee Name: ");
        emp_name = sc.nextLine();

        System.out.print("Enter Employee ID: ");
        emp_id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Address: ");
        address = sc.nextLine();

        System.out.print("Enter Mail ID: ");
        mail_id = sc.nextLine();

        System.out.print("Enter Mobile Number: ");
        mobile_no = sc.nextLine();
    }

    void displayEmployeeDetails() {
        System.out.println("\nEmployee Name: " + emp_name);
        System.out.println("Employee ID: " + emp_id);
        System.out.println("Address: " + address);
        System.out.println("Mail ID: " + mail_id);
        System.out.println("Mobile No: " + mobile_no);
    }
}

// Derived Class
class Programmer extends Employee {
    double BP, DA, HRA, PF, staff_club, gross, net;

    void generatePaySlip(Scanner sc) {
        System.out.print("Enter Basic Pay: ");
        BP = sc.nextDouble();

        DA = 0.97 * BP;
        HRA = 0.10 * BP;
        PF = 0.12 * BP;
        staff_club = 0.001 * BP;

        gross = BP + DA + HRA;
        net = gross - PF - staff_club;

        displayEmployeeDetails();
        System.out.println("\n--- PAY SLIP (Programmer) ---");
        System.out.println("Basic Pay: " + BP);
        System.out.println("DA (97%): " + DA);
        System.out.println("HRA (10%): " + HRA);
        System.out.println("PF (12%): " + PF);
        System.out.println("Staff Club Fund (0.1%): " + staff_club);
        System.out.println("Gross Salary: " + gross);
        System.out.println("Net Salary: " + net);
    }
}

class AssistantProfessor extends Programmer {}
class AssociateProfessor extends Programmer {}
class Professor extends Programmer {}


// Main Class
public class EmployeePaySlip {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("1. Programmer");
        System.out.println("2. Assistant Professor");
        System.out.println("3. Associate Professor");
        System.out.println("4. Professor");
        System.out.print("Choose Employee Type: ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch(choice) {
            case 1:
                Programmer p = new Programmer();
                p.getEmployeeDetails(sc);
                p.generatePaySlip(sc);
                break;

            case 2:
                AssistantProfessor ap = new AssistantProfessor();
                ap.getEmployeeDetails(sc);
                ap.generatePaySlip(sc);
                break;

            case 3:
                AssociateProfessor asp = new AssociateProfessor();
                asp.getEmployeeDetails(sc);
                asp.generatePaySlip(sc);
                break;

            case 4:
                Professor prof = new Professor();
                prof.getEmployeeDetails(sc);
                prof.generatePaySlip(sc);
                break;

            default:
                System.out.println("Invalid Choice");
        }

        sc.close();
    }
}