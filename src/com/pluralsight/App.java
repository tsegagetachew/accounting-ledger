package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;


public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        splashScreen();
        homeScreen(scanner);
    }

    public static void splashScreen() {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║                                              ║");
        System.out.println("║   💰  WELCOME TO YOUR PERSONAL ACCOUNTING    ║");
        System.out.println("║                  LEDGER  💰                  ║");
        System.out.println("║                                              ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();
    }


    public static void homeScreen(Scanner scanner) {
        boolean run = true;
        while (run) {
            System.out.println("Select from the following options: ");
            System.out.println("\t1) Add Deposit");
            System.out.println("\t2) Make Payment (Debit)");
            System.out.println("\t3) Ledger");
            System.out.println("\t0) Exit");
            System.out.print("Enter your selection: ");
            int userOption = scanner.nextInt();
            scanner.nextLine();

            formatSpaces();

            switch (userOption) {
                case 1:
                    addDeposit(scanner);
                    break;
                case 2:
                    makePayment(scanner);
                    break;
                case 3:
                    ledgerScreen(scanner);
                    break;
                case 0:
                    System.out.println("Thank you for using the Accounting Ledger!");
                    scanner.close();
                    run = false;
                    break;
                default:
                    waitAndContinue(scanner, "Incorrect Option Entered");
            }
        }
    }

    public static void addDeposit(Scanner scanner) {
        System.out.println("Add Deposit");

        System.out.print("Enter Description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Enter Vendor: ");
        String vendor = scanner.nextLine().trim();

        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        Transaction newTransaction = new Transaction(date, time, description, vendor, Math.abs(amount));
        saveTransaction(newTransaction);

        waitAndContinue(scanner, "Deposit added! ");
    }

    public static void makePayment(Scanner scanner) {
        System.out.println("Make a payment (Debit)");

        System.out.print("Enter Description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Enter Vendor: ");
        String vendor = scanner.nextLine().trim();

        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        Transaction newTransaction = new Transaction(date, time, description, vendor, -Math.abs(amount));
        saveTransaction(newTransaction);

        waitAndContinue(scanner, "Payment Recorded! ");
    }

    public static void ledgerScreen(Scanner scanner) {
        boolean run = true;
        while (run) {
            System.out.println("Select from the following options");
            System.out.println("\t1) All");
            System.out.println("\t2) Deposits");
            System.out.println("\t3) Payments");
            System.out.println("\t4) Reports");
            System.out.println("\t0) Go back to Home Screen");
            System.out.print("Enter your selection: ");
            int userOption = scanner.nextInt();
            scanner.nextLine();

            formatSpaces();

            switch (userOption) {
                case 1:
                    displayEntries(loadTransactions(), scanner);
                    break;
                case 2:
                    ArrayList<Transaction> deposits = new ArrayList<>();
                    for (Transaction t : loadTransactions()) {
                        if (t.getAmount() > 0) deposits.add(t);
                    }
                    displayEntries(deposits, scanner);
                    break;
                case 3:
                    ArrayList<Transaction> payments = new ArrayList<>();
                    for (Transaction t : loadTransactions()) {
                        if (t.getAmount() < 0) payments.add(t);
                    }
                    displayEntries(payments, scanner);
                    break;
                case 4:
                    reportsScreen(scanner);
                    break;
                case 0:
                    run = false;
                    break;
                default:
                    waitAndContinue(scanner, "Incorrect Option Entered");
            }
        }
    }

    public static void reportsScreen(Scanner scanner) {
        boolean run = true;
        while (run) {
            System.out.println("Select from the following options: ");
            System.out.println("\t1) Month to date");
            System.out.println("\t2) Previous Month");
            System.out.println("\t3) Year to Date");
            System.out.println("\t4) Previous Year");
            System.out.println("\t5) Search by Vendor");
            System.out.println("\t6) Custom Search");
            System.out.println("\t0) Go back to Ledger Screen");
            System.out.print("Enter your selection: ");
            int userOption = scanner.nextInt();
            scanner.nextLine();

            formatSpaces();

            LocalDate today = LocalDate.now();

            switch (userOption) {
                case 1:   //month to date
                    filterByDateRange(today.withDayOfMonth(1), today, scanner);
                    break;
                case 2:    //previous month
                    LocalDate prevMonthStart = today.minusMonths(1).withDayOfMonth(1);
                    LocalDate prevMonthEnd = prevMonthStart.withDayOfMonth(prevMonthStart.lengthOfMonth());
                    filterByDateRange(prevMonthStart, prevMonthEnd, scanner);
                    break;
                case 3:  //year to date
                    filterByDateRange(today.withDayOfYear(1), today, scanner);
                    break;
                case 4: //previous year
                    LocalDate prevYearStart = today.minusYears(1).withDayOfYear(1);
                    LocalDate prevYearEnd = prevYearStart.withDayOfYear(prevYearStart.lengthOfYear());
                    filterByDateRange(prevYearStart, prevYearEnd, scanner);
                    break;
                case 5: //search by vendor
                    searchByVendor(scanner);
                    break;
                case 6: //customer search
                    customSearch(scanner);
                case 0:   //go back to ledger screen
                    run = false;
                    break;
                default:
                    waitAndContinue(scanner, "Incorrect Option Entered");
            }
        }
    }

    public static ArrayList<Transaction> loadTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();

        File file = new File("src/com/pluralsight/transactions.csv");
        if (!file.exists()) return transactions;

        try {
            BufferedReader buffReader = new BufferedReader(new FileReader(file));
            buffReader.readLine();
            String transactionItem;
            while ((transactionItem = buffReader.readLine()) != null) {

                if (transactionItem.trim().isEmpty()) continue;

                String[] splitTransactionItem = transactionItem.split(Pattern.quote("|"));

                if (splitTransactionItem.length < 5) continue;

                String date        = splitTransactionItem[0].trim();
                String time        = splitTransactionItem[1].trim();
                String description = splitTransactionItem[2].trim();
                String vendor      = splitTransactionItem[3].trim();
                double amount      = Double.parseDouble(splitTransactionItem[4].trim());

                transactions.add(new Transaction(date, time, description, vendor, amount));
            }
            buffReader.close();
        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }

        return transactions;
    }

    public static void saveTransaction(Transaction transaction) {
        try {
            BufferedWriter buffWriter = new BufferedWriter(
                    new FileWriter("src/com/pluralsight/transactions.csv", true));
            buffWriter.write(csvFormat(transaction));
            buffWriter.newLine();
            buffWriter.flush();
            buffWriter.close();
        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());

        }
    }

    public static void displayEntries(ArrayList<Transaction> transactions, Scanner scanner) {
        if (transactions.isEmpty()) {
            waitAndContinue(scanner, "No transactions found. ");
            return;
        }

        for (int i = transactions.size() - 1; i >= 0; i--) {
            formatTransactionPrint(transactions.get(i));
        }

        waitAndContinue(scanner, "\nFinished Viewing: ");
    }

    public static void filterByDateRange(LocalDate start, LocalDate end, Scanner scanner) {
        ArrayList<Transaction> result = new ArrayList<>();

        for (Transaction t : loadTransactions()) {
            LocalDate tDate = LocalDate.parse(t.getDate());
            if (!tDate.isBefore(start) && !tDate.isAfter(end)) {
                result.add(t);
            }
        }

        displayEntries(result, scanner);
    }

    public static void searchByVendor(Scanner scanner) {
        System.out.print("Enter Vendor Name: ");
        String searchValue = scanner.nextLine().trim().toLowerCase();

        ArrayList<Transaction> result = new ArrayList<>();

        for (Transaction t : loadTransactions()) {
            if (t.getVendor().toLowerCase().contains(searchValue)) {
                result.add(t);
            }
        }

        displayEntries(result, scanner);
    }

    public static void customSearch(Scanner scanner) {
        System.out.println("Custom Search (press Enter to skip any field)");

        System.out.print("Start Date (yyyy-MM-dd): ");
        String startInput = scanner.nextLine().trim();

        System.out.print("End Date (yyyy-MM-dd): ");
        String endInput = scanner.nextLine().trim();

        System.out.print("Description: ");
        String descInput = scanner.nextLine().trim().toLowerCase();

        System.out.print("Vendor: ");
        String vendorInput = scanner.nextLine().trim().toLowerCase();

        System.out.print("Amount (leave blank to skip): ");
        String amountInput = scanner.nextLine().trim();

        LocalDate startDate = startInput.isEmpty() ? null : LocalDate.parse(startInput);
        LocalDate endDate   = endInput.isEmpty()   ? null : LocalDate.parse(endInput);
        Double amount       = amountInput.isEmpty() ? null : Double.parseDouble(amountInput);

        ArrayList<Transaction> result = new ArrayList<>();

        for (Transaction t : loadTransactions()) {
            LocalDate tDate = LocalDate.parse(t.getDate());

            if (startDate != null && tDate.isBefore(startDate)) continue;
            if (endDate   != null && tDate.isAfter(endDate))    continue;
            if (!descInput.isEmpty()   && !t.getDescription().toLowerCase().contains(descInput))  continue;
            if (!vendorInput.isEmpty() && !t.getVendor().toLowerCase().contains(vendorInput))     continue;
            if (amount != null && t.getAmount() != amount)                                        continue;

            result.add(t);
        }

        displayEntries(result, scanner);
    }


    public static String csvFormat(Transaction t) {
        return String.format("%s|%s|%s|%s|%.2f",
                t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
    }

    public static void formatTransactionPrint(Transaction t) {
        System.out.printf("Date: %s - Time: %s - Description: %s - Vendor: %s - Amount: $%.2f%n",
                t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
    }

    public static void waitAndContinue(Scanner scanner, String message) {
        System.out.println(message + " (press Enter to continue)");
        scanner.nextLine();
    }

    public static void formatSpaces() {
        System.out.println("\n\n");
    }
}