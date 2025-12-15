# HealthCenterMemberSystem
A menu driven Java application for managing Health Centre members. The system allows Personal Trainers to add members, record visits/consultations, and generate statistical reports on member health data (BMI, BP, etc.).

Requirments
1. Java JDK 8 or higher.
2. Microsoft SQL Server (Localhost).
3. MS SQL JDBC Driver (mssql-jdbc.jar) must be added to the classpath.

DataBase
Before running the application, you must set up the database:
Open the included file "HealthCentreDB_Setup.sql" in SSMS.
Run the script to create the 'HealthCentreDB' and populate the 'Members' table.
Ensure SQL Server authentication is enabled.
If your SQL Server credentials differ from standard, please update 
'DBConnector.java' lines 11-12 before running:
Current User: "sa"
Current Pass: "password123"

How to Run
1. Compile all files in the 'HealthCentreMemberSystem' package.
2. Run the main class: 'PTSystem.java'.
3. Follow the console menu prompts to navigate the system.
