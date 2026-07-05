class Customer {
    String name;
    int accNo;

    Customer(String name, int accNo) {
        this.name = name;
        this.accNo = accNo;
    }

    void showBasicDetails() {
        System.out.println("Customer Name: " + name);
        System.out.println("Account Number: " + accNo);
    }

    void showAccountDetails(Savings s, Loan l) {
        System.out.println("\n--- Account Details ---");
        s.showBalance();
        l.showBalance();
    }
}