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
          System.out.println("Welcome to the Accounting Ledger!");
          homeScreen(scanner);
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

              formatSpaces(); //  prints clear spaces

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
                      System.exit(0);
                      break;

                  default:
                      waitAndContinue(scanner, "Incorrect Option Entered");


              }

          }
      }

          public static void addDeposit (Scanner scanner){
              System.out.println("Add Deposit");

              System.out.print("Enter Description:");
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

          public static void makePayment (Scanner scanner){
              System.out.println("Make a payment (Debit)");

              System.out.print("Enter Description:");
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

          public static void ledgerScreen (Scanner scanner) {
              boolean run = true;
              while (run) {
                  System.out.println("Select from the following options");
                  System.out.println("\t1) All");
                  System.out.println("\t2) Deposits");
                  System.out.println("\t3) Payments");
                  System.out.println("\t4) Reports");
                  System.out.println("\t0) Go back to Home Screen");
                  System.out.println("\tH) Home");
                  System.out.print("Enter your selection: ");
                  String userOption = scanner.nextLine().trim().toUpperCase();

                  formatSpaces(); // blank lines to clear screen

                  switch (userOption) {
                      case "1":
                          displayEntries(loadTransactions(), scanner);
                          break;
                      case "2":
                          ArrayList<Transaction> deposits = new ArrayList<>();
                          for (Transaction t : loadTransactions()) {
                              if (t.getAmount() > 0) deposits.add(t);
                          }
                          displayEntries(deposits, scanner);
                          break;
                      case "3":
                          ArrayList<Transaction> payments = new ArrayList<>();
                          for (Transaction t : loadTransactions()) {
                              if (t.getAmount() < 0) payments.add(t);
                          }
                          displayEntries(payments, scanner);
                          break;
                      case "4":
                          reportsScreen(scanner);
                          break;
                      case "0":
                          run = false;
                          break;
                      case "H":
                          homeScreen(scanner);
                          return;
                      default:
                          waitAndContinue(scanner, "Incorrect Option Entered");
                  }

              }

          }



          public static void reportsScreen (Scanner scanner){
              boolean run = true;
              while (run) {
                  System.out.println("Select from the following options: ");
                  System.out.println(("\t1) Month to date"));
                  System.out.println("\t2) Previous Month");
                  System.out.println("\t3) Year to Date");
                  System.out.println("\t4) Previous Year");
                  System.out.println("\t5) Search by Vendor ");
                  System.out.println("\t0) Go back to Ledger Screen");
                  System.out.println("\tH) Home");
                  System.out.print("Enter your selection: ");
                  String userOption = scanner.nextLine().trim().toUpperCase();


                  formatSpaces();

                  LocalDate today = LocalDate.now();

                  switch (userOption) {
                      case "1":  //month to date
                          filterByDateRange(today.withDayOfMonth(1), today, scanner);
                          break;
                      case "2":  //previous month
                          LocalDate prevMonthStart = today.minusMonths(1).withDayOfMonth(1);
                          LocalDate prevMonthEnd = prevMonthStart.withDayOfMonth(prevMonthStart.lengthOfMonth());
                          filterByDateRange(prevMonthStart, prevMonthEnd, scanner);
                          break;
                      case "3":  //year to date
                          filterByDateRange(today.withDayOfYear(1), today, scanner);
                          break;
                      case "4":  //previous year
                          LocalDate prevYearStart = today.minusYears(1).withDayOfYear(1);
                          LocalDate prevYearEnd = prevYearStart.withDayOfYear(prevYearStart.lengthOfYear());
                          filterByDateRange(prevYearStart, prevYearEnd, scanner);
                          break;
                      case "5":  //search by vendor
                          searchByVendor(scanner);
                          break;
                      case "0":  //go back to ledger screen
                          run = false;
                          break;
                      case "H":
                          homeScreen(scanner);
                          return;
                      default:
                          waitAndContinue(scanner, "Incorrect Option Entered");



              }

          }
      }


      public static ArrayList<Transaction> loadTransactions() {     // loading transactions here
          ArrayList<Transaction> transactions = new ArrayList<>();


          File file = new File("src/com/pluralsight/transactions.csv");
          if (!file.exists()) return transactions;

          try {
              BufferedReader buffReader = new BufferedReader(new FileReader(file));
              buffReader.readLine();
              String transactionItem;
              while ((transactionItem = buffReader.readLine()) != null) {
                  String[] splitTransactionItem = transactionItem.split(Pattern.quote("|"));

                  String date = splitTransactionItem[0].trim();
                  String time = splitTransactionItem[1].trim();
                  String description = splitTransactionItem[2].trim();
                  String vendor = splitTransactionItem[3].trim();
                  double amount = Double.parseDouble(splitTransactionItem[4].trim());

                  Transaction transaction = new Transaction(date, time, description, vendor, amount);
                  transactions.add(transaction);
              }
              buffReader.close();
          } catch (IOException e) {
              e.printStackTrace();
          }
          return transactions;   // returns after list is loaded

      }

      public static void saveTransaction(Transaction transaction) {    //saving transactions here
          try {
              BufferedWriter buffWriter = new BufferedWriter(new FileWriter("src/com/pluralsight/transactions.csv", true));
              buffWriter.write(csvFormat(transaction));
              buffWriter.newLine();
              buffWriter.close();
          } catch (IOException e) {
              e.printStackTrace();
          }

      }

      public static void displayEntries(ArrayList<Transaction> transactions, Scanner scanner) {   //shows all transactions on screen
          if (transactions.isEmpty()) {
              waitAndContinue(scanner, "No transactions found. ");
              return;

          }

          for (int i = transactions.size() - 1; i >= 0; i--) {
              formatTransactionPrint(transactions.get(i));

          }

          waitAndContinue(scanner, "\nFinished Viewing: ");
      }

      public static void filterByDateRange(LocalDate start, LocalDate end, Scanner scanner) {   //only transaction between two dates
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

          formatSpaces();

          ArrayList<Transaction> result = new ArrayList<>();

          for (Transaction t : loadTransactions()) {
              if (t.getVendor().toLowerCase().contains(searchValue)) {
                  result.add(t);

              }
          }

          displayEntries(result, scanner); //display what is matched

      }

      public static String csvFormat(Transaction t) {
          return String.format("%s|%s|%s|%s|%.2f", t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());


      }

      public static void formatTransactionPrint(Transaction t) {
          System.out.printf("Date: %s - Time: %s - Description: %s - Vendor: %s - Amount: $%.2f%n", t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());

      }

      public static void waitAndContinue(Scanner scanner, String message) {
          System.out.println(message + "(press Enter to continue)");
          scanner.nextLine();
          formatSpaces();

      }

      public static void formatSpaces() {
          System.out.println("\n\n\n\n");
      }
  }
