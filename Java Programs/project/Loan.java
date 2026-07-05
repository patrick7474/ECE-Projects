class Loan {
    double loanAmount;

    Loan(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    void credit(double amount) {
        loanAmount += amount;
        System.out.println("Loan credited: " + amount);
    }

    void showBalance() {
        System.out.println("Loan Balance: " + loanAmount);
    }
}