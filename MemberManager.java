/*
 * MemberManager.java
 * Handles the storage and retrieval of Member objects.
 */
package HealthCentreMemberSystem;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class MemberManager {
    /** The list holding all members. */
    private ArrayList<HCMember> members;
    /** Counter to ensure every new member gets a unique ID. */
    private int nextHcNum;

    /**
     * Constructor.
     * Initialises the list and sets the starting Health Centre Number.
     */
    public MemberManager() {
        this.members = new ArrayList<>();
        this.nextHcNum = 100001; // IDs start from 100001
        loadMembersFromDatabase();
        
        // Determine the next ID based on the last loaded member
        if (members.isEmpty()) {
            this.nextHcNum = 100001;
        } else {
            // Simple logic: grab the last member's ID and add 1
            String lastId = members.get(members.size() - 1).getHCNumber();
            this.nextHcNum = Integer.parseInt(lastId) + 1;
        }
    }
    
    private void loadMembersFromDatabase() {
        String sql = "SELECT * FROM Members";
        
        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Reconstruct the object from the database row
                HCMember m = new HCMember(
                    rs.getString("HCNumber"),
                    rs.getString("Forename"),
                    rs.getString("Surname"),
                    rs.getString("Gender").charAt(0),
                    rs.getInt("Age"),
                    rs.getDouble("Weight"),
                    rs.getString("Address")
                );
                
                // Set the non-constructor fields manually
                // Note: You may need to add setters for these in HCMember if they are private
                // m.setBloodPressure(rs.getString("BloodPressure")); 
                // m.setVisitTally(rs.getInt("VisitTally"));
                m.setFConsultation(rs.getBoolean("FConsultation"));
                
                members.add(m);
            }
            System.out.println("Database loaded: " + members.size() + " members found.");

        } catch (SQLException e) {
            System.out.println("Error loading from database: " + e.getMessage());
        }
    }
    
    

    /**
     * Saves a new member to databse after creation.
     * @param forename Member's first name.
     * @param surname  Member's surname.
     * @param gender   Member's gender.
     * @param age      Member's age.
     * @param weight   Member's weight.
     * @param address  Member's address.
     * @return The newly created HCMember object.
     */
    public HCMember addMember(String forename, String surname, char gender, int age, double weight, String address) {
        String id = String.valueOf(nextHcNum++);
        HCMember newMember = new HCMember(id, forename, surname, gender, age, weight, address);
        
        // 1. Add to local memory 
        members.add(newMember);
        // 2. Add to SQL Database
        saveToDatabase(newMember);
        
        return newMember;
    }

    private void saveToDatabase(HCMember m) {
        String sql = "INSERT INTO Members (HCNumber, Forename, Surname, Gender, Age, Weight, Address, BloodPressure, VisitTally, FConsultation) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, m.getHCNumber());
            pstmt.setString(2, m.getForename());
            pstmt.setString(3, m.getSurname());
            pstmt.setString(4, String.valueOf(m.getGender()));
            pstmt.setInt(5, m.getAge());
            pstmt.setDouble(6, m.getWeight());
            pstmt.setString(7, m.getAddress());
            pstmt.setString(8, m.getBloodPressure());
            pstmt.setInt(9, m.getVisitTally());
            pstmt.setBoolean(10, m.isFConsultation());
            
            pstmt.executeUpdate();
            System.out.println("Member saved to SQL Server.");
            
        } catch (SQLException e) {
            System.out.println("Error saving to database: " + e.getMessage());
        }
    }
   

    /**
     * Searches for a member by their unique HC Number.
     * * @param hcNumber The ID string to search for.
     * @return The HCMember object if found, or null if not found.
     */
    public HCMember findMemberByHCNumber(String hcNumber) {
        for (HCMember m : members) {
            if (m.getHCNumber().equals(hcNumber)) {
                return m;
            }
        }
        return null; // Search failed
    }

    /**
     * Returns the full list of members.
     * @return A List of all HCMember objects.
     */
    public List<HCMember> getAllMembers() {
        return members;
    }

    // =========================================================================
    // Filter Methods
    // =========================================================================

    /**
     * Retrieves a list of members from a specific gender.
     * @param gender 'M' or 'F'.
     * @return A filtered list of members.
     */
    public List<HCMember> getMembersByGender(char gender) {
        List<HCMember> filteredList = new ArrayList<>();
        // Loop through every member in the main list
        for (HCMember m : members) {
            // Check if the gender matches
            if (m.getGender() == gender) {
                filteredList.add(m);
            }
        }
        return filteredList;
    }

    /**
     * Retrieves a list of members with High blood pressure.
     * @return A filtered list of members.
     */
    public List<HCMember> getHighBloodPressureMembers() {
        List<HCMember> filteredList = new ArrayList<>();
        
        for (HCMember m : members) {
            // Check if blood pressure is "High" 
            if ("High".equalsIgnoreCase(m.getBloodPressure())) {
                filteredList.add(m);
            }
        }
        return filteredList;
    }
    
    /**
     * Retrieves members who haven't hadthier consultaion.
     * @return A list of members due for consultation.
     */
    public List<HCMember> getMembersDueForConsultation() {
        List<HCMember> filteredList = new ArrayList<>();
        
        for (HCMember m : members) {
            // Check if consultation status is false
            if (!m.isFConsultation()) {
                filteredList.add(m);
            }
        }
        return filteredList;
    }
    
    /**
     * Retrieves members with a visit tally below 5.
     * @param threshold The number of visits.
     * @return A list of members with low visits.
     */
    public List<HCMember> getMembersWithLowVisits(int threshold) {
        List<HCMember> filteredList = new ArrayList<>();
        
        for (HCMember m : members) {
            if (m.getVisitTally() < threshold) {
                filteredList.add(m);
            }
        }
        return filteredList;
    }

    /**
     * Resets the consultation status of ALL members to false.
     */
    public void resetAllConsultations() {
        for (HCMember m : members) {
            m.setFConsultation(false);
        }
    }

    /**
     * Gets the total number of registered members.
     * @return integer count.
     */
    public int getTotalCount() {
        return members.size();
    }
}