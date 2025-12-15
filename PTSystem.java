/*
 * PTSystem.java
 * The main entry point for the Personal Trainer System application.
 */
package HealthCentreMemberSystem;

import java.util.Scanner;
import java.util.List;

public class PTSystem {
    // The "Controller" that handles the logic
    private MemberManager manager;
    // Holds the currently selected member for targeted operations (visits, BP updates)
    private HCMember selectedMember;     
    private Scanner scanner;              
    
    /**
     * Constructor.
     * Initialises the MemberManager and input scanner.
     */
    public PTSystem() {
        manager = new MemberManager(); 
        selectedMember = null;
        scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        PTSystem system = new PTSystem();
        system.runSystem();
    } 
    
    /**
     * The main system loop.
     * Controls the flow of the application until the user chooses to exit.
     */
    public void runSystem() {
        int choice = 0;
        
        do {
            displayMainMenu();
            choice = getMenuChoice(1, 9);
            
            // User choice route
            switch (choice) {
                case 1 -> displayAllMembers();
                case 2 -> selectMember();
                case 3 -> addNewMember();
                case 4 -> showStatsMenu();
                case 5 -> recordVisit();
                case 6 -> updateBloodPressure();
                case 7 -> recordConsultation();
                case 8 -> updateWeightAndAge();
                case 9 -> { 
                    System.out.println("Thanks for using the Personal Trainer Review System!");
                    System.out.println("Goodbye!");
                }
            }
            
            if (choice != 9) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        } while (choice != 9);
    } 
    
    /**
     * Displays the main menu options
     * Shows which member is currently selected 
     */
    private void displayMainMenu() {
        System.out.println("================================================");
        System.out.println("Personal Trainer Review System");
        System.out.println("");
        System.out.println("Main Menu");
        System.out.println("================================================");
        
        //Checks if selectedMember is null
        System.out.println("Current selected Member: " + (selectedMember != null ? selectedMember.getHCNumber() : "None"));
        
        System.out.println("");
        System.out.println("1. Display All Member Details     5 Record HC Member visit");
        System.out.println("2. Display/Select Member detail   6 Update blood pressure");
        System.out.println("3. Add New Health Centre Member   7 Record F2F consultation");
        System.out.println("4 Review HC Members (Stats Menu)  8 Update weight and age");
        System.out.println("                     9 Exit");
        System.out.println("");
        System.out.print("Please enter menu choice = ");
    } 
    
    /**
     *Validated input for menu choice
     */
    private int getMenuChoice(int min, int max) {
        int choice = -1;
        while (choice < min || choice > max) {
            String input = scanner.nextLine().trim();
            if (isNumeric(input)) {
                choice = Integer.parseInt(input);
                if (choice < min || choice > max) {
                    System.out.print("Please enter a number between " + min + " and " + max + ": ");
                }
            } else {
                System.out.print("Please enter a valid number: ");
            }
        }
        return choice;
    } 
    
    /**
     * Prints a single member's details in a nice format.
     * @param member The member object to display.
     */
    private void printMemberDetails(HCMember member) {
        if (member == null) return;
        System.out.println("\n======================================================================");
        System.out.println("              HEALTH CENTRE MEMBER DETAILS");
        System.out.println("====================================================================== ");
        System.out.printf("HC Number:        %s\n", member.getHCNumber());
        System.out.printf("Name:             %s %s\n", member.getForename(), member.getSurname());
        System.out.printf("Gender:           %s\n", member.getGender() == 'M' ? "Male" : "Female");
        System.out.printf("Age:              %d years\n", member.getAge());
        System.out.printf("Weight:           %.1f kg\n", member.getWeight()); 
        System.out.printf("Address:          %s\n", member.getAddress());
        System.out.printf("Blood Pressure:   %s\n", member.getBloodPressure());
        System.out.printf("Free Consultation: %s\n", member.isFConsultation() ? "Completed" : "Due");
        System.out.printf("Centre Visits:    %d\n", member.getVisitTally());
        System.out.println("======================================================================\n");
    }
    
    /**
     * Fetches all members and prints them 
     */
    private void displayAllMembers() {
        System.out.println("================================================");
        System.out.println("\tALL HEALTH CENTRE MEMBERS");
        System.out.println("================================================");
                
        List<HCMember> allMembers = manager.getAllMembers();
        
        if (allMembers.isEmpty()) {
            System.out.println("No members registered in the system.");
            return;
        }

        // Header for list view using formatted columns
        System.out.printf("%-10s %-15s %-15s %-5s %-5s %-10s\n", "ID", "First Name", "Surname", "Sex", "Age", "BP");
        System.out.println("----------------------------------------------------------------");
        
        for (HCMember m : allMembers) {
            System.out.print(m.memberDetails());
        }
        System.out.println("Total members: " + manager.getTotalCount());
    }

    /**
     * Prompts user for an ID and sets the selectedMember
     */
    private void selectMember() {
        System.out.println("================================================");
        System.out.println("\tSELECT MEMBER");
        System.out.println("================================================");
        
        System.out.print("\nEnter HC Number to select: ");
        String hcNumber = scanner.nextLine().trim();
        
        HCMember foundMember = manager.findMemberByHCNumber(hcNumber);
        
        if (foundMember != null) {
            selectedMember = foundMember;
            printMemberDetails(selectedMember);
            System.out.println("Member selected successfully!");
        } else {
            System.out.println("Member not found.");
        }
    } 
    
    /**
     * Collects all data from user (with validation) and creates a new member.
     */
    private void addNewMember() {
        System.out.println("================================================");
        System.out.println("\tADD NEW HEALTH CENTRE MEMBER");
        System.out.println("================================================");
        
        // Validation methods 
        String forename = getValidName("forename");
        String surname = getValidName("surname");
        char gender = getValidGender();
        int age = getValidAge();
        double weight = getValidWeight();
        String address = getValidAddress(); 
      
        HCMember newMember = manager.addMember(forename, surname, gender, age, weight, address);
        
        System.out.println("New member added successfully. ID: " + newMember.getHCNumber());
    } 
    
    // =========================================================================
    // Input Validation 
    // =========================================================================
    
    /**
     * valid gender character from the user.
     * Loops indefinitely until 'M' or 'F' is entered.
     * @return The character 'M' or 'F'.
     */
    private char getValidGender() {
        char gender = ' ';
        while (gender != 'M' && gender != 'F') {
            System.out.print("Enter gender (M/F): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.length() == 1 && (input.charAt(0) == 'M' || input.charAt(0) == 'F')) {
                gender = input.charAt(0);
            } else {
                System.out.println("Please enter 'M' for Male or 'F' for Female.");
            }
        }
        return gender;
    } 
    
    /**
     * Valid age integer.
     * Validates input is within MIN_AGE and MAX_AGE.
     * @return A valid integer age.
     */
    private int getValidAge() {
        int age = -1;
        while (age == -1) {
            // Fetch min/max from Model to ensure prompt matches logic
            System.out.print("Enter age (" + HCMember.getMinAge() + "-" + HCMember.getMaxAge() + "): ");
            String input = scanner.nextLine().trim();
            if (isNumeric(input)) {
                int tempAge = Integer.parseInt(input);
                if (HCMember.isValidAge(tempAge)) {
                    age = tempAge;
                } else {
                    System.out.println("Invalid age range.");
                }
            } else {
                System.out.println("Enter a valid number.");
            }
        }
        return age;
    } 
    /**
     * Valid weight (Double).
     * @return A valid double (weight in kg).
     */
    private double getValidWeight() {
        double weight = -1.0;
        while (weight == -1.0) {
            System.out.print("Enter weight in kg (" + (int)HCMember.getMinWeight() + "-" + (int)HCMember.getMaxWeight() + "): ");
            String input = scanner.nextLine().trim();
            if (isValidDouble(input)) {
                double tempWeight = Double.parseDouble(input);
                if (HCMember.isValidWeight(tempWeight)) {
                    weight = tempWeight;
                } else {
                    System.out.println(HCMember.getWeightValidationMessage());
                }
            } else {
                System.out.println("Please enter a valid number.");
            }
        }
        return weight;
    } 
    
    /**
     * Valid Systolic blood pressure.
     * @return validated integer.
     */
    private int getValidSystolic() {
        int systolic = -1;
        while (systolic == -1) {
            System.out.print("Enter systolic pressure (" + HCMember.getMinSystolic() + "-" + HCMember.getMaxSystolic() + "): ");
            String input = scanner.nextLine().trim();
            if (isNumeric(input)) {
                int tempSystolic = Integer.parseInt(input);
                if (HCMember.isValidSystolic(tempSystolic)) {
                    systolic = tempSystolic;
                } else {
                    System.out.println(HCMember.getSystolicValidationMessage());
                }
            } else {
                System.out.println("Please enter a valid number.");
            }
        }
        return systolic;
    } 
    
    /**
     * Valid Diastolic blood pressure.
     * @return validated integer.
     */
    private int getValidDiastolic() {
        int diastolic = -1;
        while (diastolic == -1) {
            System.out.print("Enter diastolic pressure (" + HCMember.getMinDiastolic() + "-" + HCMember.getMaxDiastolic() + "): ");
            String input = scanner.nextLine().trim();
            if (isNumeric(input)) {
                int tempDiastolic = Integer.parseInt(input);
                if (HCMember.isValidDiastolic(tempDiastolic)) {
                    diastolic = tempDiastolic;
                } else {
                    System.out.println(HCMember.getDiastolicValidationMessage());
                }
            } else {
                System.out.println("Please enter a valid number.");
            }
        }
        return diastolic;
    } 
    
   private String getValidName(String fieldName) {
        String name = "";
        boolean validName = false;
        while (!validName) {
            System.out.print("Enter " + fieldName + " (minimum " + HCMember.getMinNameLength() + " characters): ");
            name = scanner.nextLine().trim();
            if (HCMember.isValidName(name)) {
                validName = true;
            } else {
                System.out.println(HCMember.getNameValidationMessage(fieldName));
            }
        }
        return name;
    }
   
    /**
     * Validates an address string.
     * @return Valid address string.
     */
    private String getValidAddress() {
        String address = "";
        boolean validAddress = false;
        while (!validAddress) {
            System.out.print("Enter address (" + HCMember.getMinAddressLength() + "-" + HCMember.getMaxAddressLength() + " characters): ");
            address = scanner.nextLine().trim();
            if (HCMember.isValidAddress(address)) {
                validAddress = true;
            } else {
                System.out.println(HCMember.getAddressValidationMessage());
            }
        }
        return address;
    } 

    /**
     * Checks if a string contains only digits.
     */
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) return false;
        }
        return true;
    } 

    /**
     * Checks if a string is a valid double.
     */
    private boolean isValidDouble(String str) {
       if (str == null || str.isEmpty()) return false;
        boolean hasDecimal = false;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '.') {
                if (hasDecimal) return false;
                hasDecimal = true;
            } else if (!Character.isDigit(ch)) {
                return false;
            }
        }
        return true;
    }
    
    // =========================================================================
    // Statistics Menu Logic
    // =========================================================================

    /**
     * Sub-menu loop for statistics reports.
     */
    private void showStatsMenu() {
        int choice = 0;
        do {
            displayStatsMenu();
            choice = getMenuChoice(1, 6);
            
            switch (choice) {
                case 1: displayMembersByGender(); break;
                case 2: displayHighBloodPressureMembers(); break;
                case 3: displayMembersWithoutConsultation(); break;
                case 4: displayLowVisitMembers(); break;
                case 5: resetAllConsultations(); break;
                case 6: System.out.println("Returning to main menu..."); break;
            }
            if (choice != 6) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        } while (choice != 6);
    } 
    
    /**
     * Prints the statistical menu options.
     */
    private void displayStatsMenu() {
        System.out.println("================================================");
        System.out.println("Stats Menu");
        System.out.println("================================================");
        System.out.println("1. Display HC Members by gender");
        System.out.println("2. Display all HC Members with high blood pressure");
        System.out.println("3. Display all HC Members without a yearly F2F consultation");
        System.out.println("4. Display all HC Members that have visited the centre less than 5 times");
        System.out.println("------------------------------------------------");
        System.out.println("5. Reset all HC members F2F consultation to false");
        System.out.println("------------------------------------------------");
        System.out.println("6. Return to main menu");
        System.out.println("");
        System.out.print("Please enter menu choice = ");
    } 
    
    /**
     * Filter Report: Members by Gender
     */
    private void displayMembersByGender() {
        System.out.println("================================================");
        System.out.println("\tMEMBERS BY GENDER:");
        System.out.println("================================================");
        char gender = getValidGender();

        List<HCMember> result = manager.getMembersByGender(gender);
        
        if (result.isEmpty()) {
            System.out.println("No " + (gender == 'M' ? "male" : "female") + " members found.");
        } else {
            for(HCMember m : result) {
                System.out.print(m.memberDetails());
            }
        }
    } 
    
    /**
     * Filter Report: Members with High Blood Pressure
     */
    private void displayHighBloodPressureMembers() {
        System.out.println("================================================");
        System.out.println("\tMEMBERS WITH HIGH BLOOD PRESSURE");
        System.out.println("================================================");
        
        List<HCMember> result = manager.getHighBloodPressureMembers();
        
        if (result.isEmpty()) {
            System.out.println("No members with high blood pressure found.");
        } else {
            for(HCMember m : result) {
                System.out.print(m.memberDetails());
            }
        }
    } 
    
    /**
     * Filter Report: Members due for consultation
     */
    private void displayMembersWithoutConsultation() {
        System.out.println("================================================");
        System.out.println("\tMEMBERS WITHOUT YEARLY F2F CONSULTATION");
        System.out.println("================================================");
        
        List<HCMember> result = manager.getMembersDueForConsultation();
        
        if (result.isEmpty()) {
             System.out.println("All members have completed their yearly consultation.");
        } else {
            for(HCMember m : result) {
                System.out.print(m.memberDetails());
            }
        }
    } 
    
    /**
     * Filter Report: Members with fewer than 5 visits
     */
    private void displayLowVisitMembers() {
        System.out.println("================================================");
        System.out.println("\tMEMBERS WITH LESS THAN 5 VISITS");
        System.out.println("================================================");
        
        List<HCMember> result = manager.getMembersWithLowVisits(5);
        
        if (result.isEmpty()) {
             System.out.println("All members have 5 or more visits.");
        } else {
            for(HCMember m : result) {
                System.out.print(m.memberDetails());
            }
        }
    } 
    
    /**
     * Reset all consultations
     * Includes a confirmation step
     */
    private void resetAllConsultations() {
        System.out.println("================================================");
        System.out.println("\tRESET ALL CONSULTATIONS");
        System.out.println("================================================");
        
        System.out.print("Are you sure you want to reset ALL consultations? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        
        if (confirm.equals("Y")) {
            manager.resetAllConsultations();
            System.out.println("All member consultations have been reset to false.");
        } else {
            System.out.println("Operation has been cancelled.");
        }
    } 

    /**
     * Operational Method: Increments visit count for selected member
     */
    private void recordVisit() {
        if (!checkMemberSelected()) return;
        System.out.println("================================================");
        System.out.println("\tRECORD MEMBER VISIT");
        System.out.println("================================================");
        selectedMember.visitedCentre();
    } 
    
    /**
     * Operational Method: Updates BP for selected member
     * Calculates new category immediately 
     */
    private void updateBloodPressure() {
        if (!checkMemberSelected()) return; 
        System.out.println("================================================");
        System.out.println("\tUPDATE BLOOD PRESSURE");
        System.out.println("================================================");
        
        int systolic = getValidSystolic();
        int diastolic = getValidDiastolic();
        String result = selectedMember.calcBloodPressure(systolic, diastolic);
        
        if (!result.equals("Invalid reading")) {
            System.out.println("Blood pressure updated successfully!");
            System.out.println("New blood pressure classification: " + result.toUpperCase());
        } else {
            System.out.println("Invalid blood pressure readings provided.");
        }
    } 
    
    /**
     * Operational Method: Marks consultation as done
     */
    private void recordConsultation() {
        if (!checkMemberSelected()) return;
        System.out.println("================================================");
        System.out.println("\tRECORD F2F CONSULTATION");
        System.out.println("================================================");
        
        selectedMember.setFConsultation(true);
        System.out.println("F2F consultation recorded for " + selectedMember.getForename() + " " + selectedMember.getSurname());
    } 
    
    /**
     * Operational Method: Updates Age/Weight
     */
    private void updateWeightAndAge() {
        if (!checkMemberSelected()) return;
        System.out.println("================================================");
        System.out.println("\tUPDATE WEIGHT AND AGE");
        System.out.println("================================================");
        System.out.println("\nCurrent weight: " + selectedMember.getWeight() + " kg");
        System.out.println("Current age: " + selectedMember.getAge() + " years");
        
        double newWeight = getValidWeight();
        int newAge = getValidAge();
        
        if (selectedMember.setWeight(newWeight) && selectedMember.setAge(newAge)) {
            System.out.println("Weight and age updated successfully!");
        }
    } 
    
    /**
     * Condition check.
     * Ensures a member is selected before attempting operations that require one.
     * @return true if a member is currently selected, false otherwise.
     */
    private boolean checkMemberSelected() {
        if (selectedMember == null) {
            System.out.println("\nNo member selected. Please use option 2 to select a member first.");
            return false;
        }
        return true;
    } 
}