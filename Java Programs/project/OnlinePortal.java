import java.util.Scanner;

public class OnlinePortal {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Login
        System.out.print("Enter username: ");
        String user = sc.next();
        System.out.println("Welcome " + user + "!\n");

        // Objects
        Customer c = new Customer("Patrick", 10101);
        Savings s = new Savings(2000);
        Loan l = new Loan(8000);

        int choice;

        do {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Savings");
            System.out.println("2. Loan");
            System.out.println("3. Customer Details");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    System.out.println("\n--- Savings Menu ---");
                    System.out.println("1. Deposit");
                    System.out.println("2. Withdraw");
                    System.out.println("3. Interest");
                    System.out.println("4. Balance");
                    System.out.print("Enter choice: ");
                    int sChoice = sc.nextInt();

                    switch (sChoice) {
                        case 1:
                            System.out.print("Enter amount: ");
                            s.deposit(sc.nextDouble());
                            break;

                        case 2:
                            System.out.print("Enter amount: ");
                            s.withdraw(sc.nextDouble());
                            break;

                        case 3:
                            s.interest();
                            break;

                        case 4:
                            s.showBalance();
                            break;

                        default:
                            System.out.println("Invalid choice");
                    }
                    break;

                case 2:
                    System.out.println("\n--- Loan Menu ---");
                    System.out.println("1. Credit Loan");
                    System.out.println("2. Balance");
                    System.out.print("Enter choice: ");
                    int lChoice = sc.nextInt();

                    switch (lChoice) {
                        case 1:
                            System.out.print("Enter loan amount: ");
                            l.credit(sc.nextDouble());
                            break;

                        case 2:
                            l.showBalance();
                            break;

                        default:
                            System.out.println("Invalid choice");
                    }
                    break;

                case 3:
                    c.showBasicDetails();
                    c.showAccountDetails(s, l);
                    break;

                case 4:
                    System.out.println("Thank you!");
                    break;

                default:
                    System.out.println("Invalid choice");
            }

        } while (choice != 4);

        sc.close();
    }
}