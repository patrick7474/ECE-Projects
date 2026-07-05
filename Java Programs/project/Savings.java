class Savings {
    double balance;

    Savings(double balance) {
        this.balance = balance;
    }

    void deposit(double amount) {
        balance += amount;
        System.out.println("Deposited: " + amount);
    }

    void withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawn: " + amount);
        } else {
            System.out.println("Insufficient balance!");
        }
    }

    void interest() {
        double interest = balance * 0.04;
        balance += interest;
        System.out.println("Interest added: " + interest);
    }

    void showBalance() {
        System.out.println("Current Balance: " + balance);
    }
}