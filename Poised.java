import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
* Java class Poised
* <br>
* This class contains the main method and will be used to create, display information, update project details  and finalize projects
* This class stores project information in MySQL database
* @author Mfundo Temba
* @version 1.00, 09 February 2023
*/
public class Poised {
	/**
	  * This main method is the main menu where all methods are displayed and can be selected
	  * @param args String arguments
	  */
	public static void main(String[] args) {
		try {
			String url = "jdbc:mysql://localhost:3306/poisepms";
			String userName = "poised_mfundo";
			String passWord = "P0ised";
			// Connection object to connect to database
			Connection connection = DriverManager.getConnection(url, userName, passWord);
			// Create a direct line to the database for running our queries
			Statement statement = connection.createStatement();
			System.out.println("Welcome to the Poised Project Manager");
			Scanner sc = new Scanner(System.in);
			
			while(true){
				// Prompt user to enter desired program option
				System.out.println("Please enter one of the following options : ");
				System.out.println("Enter option :\n0. Exit\n1. Add new project\n2. Update project\n3. View incomplete Projects\n4. View Overdue Projects \n5. Search project");
				System.out.println("6. Update Customer Contact details\n7. Finalize project");
				int selection = sc.nextInt();
				sc.nextLine();
				// Enter loop of selected number
				if(selection == 0) {
					System.out.println("Program ended!");
					statement.close();
					connection.close();
					sc.close();
					break;
					
				}else if(selection == 1) {
					addProject(statement, sc);	
					
				}else if(selection == 2) {
					updateProjectDetails(statement, sc);
					
				}else if(selection == 3) {
					findIncompleteProject(statement);
					
				}else if(selection == 4) {
					findOverdueProject(statement);
					
				}else if(selection == 5) {
					searchProject(statement, sc);
					
				}else if(selection == 6) {
					updateCustomerContacts(statement, sc);
					
				}else if(selection == 7) {
					finalizeProject(statement, sc);
					
				}else {
					System.out.println("Invalid input, start again");
				}
			}
		}catch(SQLException err) {
			err.printStackTrace();
		}
	}
	
	/**
	  * This method populates a table row with new project data.
	  * Project information is prompted for user entry and saved to the table in the MySQL database.
	  * @param statement Object
	  * @param input Scanner
	  */
	private static void addProject(Statement statement, Scanner input) {
		try {
			int rowsAffected;
			//Ask user to enter building details
			System.out.println("Enter project number : ");
			int projectNum = input.nextInt();
			input.nextLine();
			System.out.println("Enter project name : ");
			String projectName = input.nextLine().toLowerCase();
			System.out.println("Enter building type (house, apartment, shop etc) : ");
			String buildingType = input.nextLine().toLowerCase();
			System.out.println("Enter building physical address (e.g. 1 forest street): ");
			String buildingAddress = input.nextLine().toLowerCase();
			System.out.println("Enter ERF number : ");
			int projectERF = input.nextInt();
			input.nextLine();
			System.out.println("Enter the total project fee : ");
			double projectFee = input.nextDouble();
			input.nextLine();
			System.out.println("Enter the project amount paid to date : ");
			double amountPaidtoDate = input.nextDouble();
			input.nextLine();
			System.out.println("Enter the project deadline (yyyy-mm-dd) : ");
			String projectDeadline = input.nextLine();
			String projectStatus = "incomplete";
			
			//Add project details to the table
			String newProject = "insert into Project values ("+ projectNum + ", '" + projectName 
				  	 + "', '" + buildingType + "', '" + buildingAddress + "', " + projectERF + ", "
				  	 + projectFee + ", " + amountPaidtoDate + ", '" + projectDeadline + "', '"+ projectStatus + "', NULL)" ; 
			rowsAffected = statement.executeUpdate(newProject);
			System.out.println("Query complete, " + rowsAffected + " rows found.");
			
			//Method for project person details
			projectPerson(statement, projectNum, projectName, buildingType, input);
			
		}catch(Exception e) {
			e.printStackTrace();
		}	
	}

	/**
	 * This method populates a table row with new project data.
	 * Project information is prompted for user entry and saved to the table in the MySQL database.
	 * @param  statement Object connecting to database
	 * @param  projectNumber int
	 * @param  projectName String
	 * @param  buildingType String
	 * @param  input Scanner
	 */
	private static void projectPerson(Statement statement, int projectNumber, String projectName, String buildingType, Scanner input) {
		try {
			String[] roleArray = {"Structural Engineer", "Project Manager", "Architect", "Customer"};
			int rows;
			
			for(int i = 0; i < roleArray.length; i++) {
				// Ask user to enter details for the project people
				int projNumber = projectNumber;
				String role = roleArray[i];
				System.out.println("Enter "+ role + " name : ");
				String name = input.nextLine().toLowerCase();
				System.out.println("Enter "+ role + " surname : ");
				String surname = input.nextLine().toLowerCase();
				System.out.println("Enter "+ role + " phone number : ");
				String phonenumber = input.nextLine();
				System.out.println("Enter "+ role + " E-mail address  : ");
				String email = input.nextLine();
				System.out.println("Enter "+ role + " physical address : ");
				String address = input.nextLine();

				// If project name is blank make name with building type and customer surname
				if(projectName.equals("") && role.equals("customer")) {
					projectName = buildingType + surname ;
					
				}else {
					System.out.println("Project assigned name");
				}	
				// Store details into table
				String newProjectpeople = "insert into ProjectPeople values ("+ projNumber + ", '" + role + "', '" + name 
						+ "', '" + surname + "', '" + phonenumber + "', '" + email + "', '" + address + "')"; 
				rows = statement.executeUpdate(newProjectpeople);
				System.out.println("Query complete, " + rows + " rows found.");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}	
	}
	/**
	  * This method updates selected columns in the project table of the database 
	  * The user is asked to select data to be updated and has to enter new data to replace 
	  * These details are then overwritten on the table for the selected project
	  * @param statement Object
	  * @param sc Scanner
	  */
	private static void updateProjectDetails(Statement statement, Scanner sc) {
		try {
			while(true) {
				// Ask user to enter table values to update
				System.out.println("Choose one of the following: \na. Update Total Fee \nb. Update Amount Paid to date \nc. Update Deadline \nd. Exit");
				String selectOption = sc.nextLine();
				int projectNumber = 0;
				int row;
				
			    // If a is entered then ask user to enter total project fee and update table with new value
				if(selectOption.equals("a")){
					System.out.println("Enter project number: ");
					projectNumber = sc.nextInt();
					sc.nextLine();
					System.out.println("Enter new project fee :");
					double newFee = sc.nextDouble();
					sc.nextLine();
					String updateFee = "UPDATE Project " + "SET TotalFee = " + newFee +  " WHERE ProjectNumber =" + projectNumber;
					row = statement.executeUpdate(updateFee);
					System.out.println("Query complete, " + row + " rows found.");
					
				// If b is entered then ask user to enter total amount paid to date and update table with new value
				}else if(selectOption.equals("b")){
					System.out.println("Enter project number: ");
					projectNumber = sc.nextInt();
					sc.nextLine();
					System.out.println("Enter new amount paid :");
					double newPaid = sc.nextDouble();
					sc.nextLine();
					String updateAmount = "UPDATE Project " + "SET AmountPaid  = " + newPaid + " WHERE ProjectNumber =" + projectNumber;
					row = statement.executeUpdate(updateAmount);
					System.out.println("Query complete, " + row + " rows found.");
				
				// If c is entered then ask user to enter new deadline and update table with new value
				}else if(selectOption.equals("c")) {
					System.out.println("Enter project number: ");
					projectNumber = sc.nextInt();
					sc.nextLine();
					System.out.println("Enter new deadline (yyyy-mm-dd) :");
					String newDeadline = sc.nextLine();
					String updateDeadline = "UPDATE Project " + "SET Deadline = " + "'" + newDeadline + "'" + " WHERE ProjectNumber =" + projectNumber;
					row = statement.executeUpdate(updateDeadline);
					System.out.println("Query complete, " + row + " rows found.");
				
				// If d is entered then return to main menu
				}else if(selectOption.equals("d")) {
					break;
					
				}else {
					System.out.println("Invalid Selection");
				}
			} 
		}catch(SQLException er1) {
			er1.printStackTrace();
		}	
	}
	/**
	  * This method updates selected columns in the project table of the database 
	  * The user is asked to select customer information to be updated and has to enter new information 
	  * This new information overwrites the old one on the table for the customer of selected project
	  * @param statement Object
	  * @param sc Scanner
	  */	
	private static void updateCustomerContacts(Statement statement, Scanner sc) {
		try {
			while(true) {
				// Ask user to enter option to execute 
				System.out.println("Choose one of the following: \na. Update Phone number \nb. Update Email Address \nc. Exit");
				String selectOption = sc.nextLine();
				int row;
				System.out.println("Enter project number");
				int projectNum = sc.nextInt();
				sc.nextLine();
				
				// If a is entered then ask user to enter new phone number and update the table with new input
				if(selectOption.equals("a")){				
					System.out.println("Enter new phone number");
					String newPhone = sc.nextLine();
					String updateDetails = "UPDATE ProjectPeople " + "SET PhoneNumber = " + "'" + newPhone + "'" + " WHERE Role = 'Customer' AND ProjectNumber =" + projectNum;
					row = statement.executeUpdate(updateDetails);
					System.out.println("Query complete, " + row + " rows found.");
					
				// If b is entered then ask user to enter new email address and update the table with new input
				}else if(selectOption.equals("b")){			
					System.out.println("Enter new Email Address :");
					String newEmail = sc.nextLine();
					String updateDetails = "UPDATE ProjectPeople " + "SET EmailAddress = " + "'" + newEmail + "'" + " WHERE Role = 'Customer' AND ProjectNumber =" + projectNum;
					row = statement.executeUpdate(updateDetails);
					System.out.println("Query complete, " + row + " rows found.");
				
				// If c is entered return to main menu
				}else if(selectOption.equals("c")) {
					break;
					
				}else {
					System.out.println("Invalid Selection");
				}
			} 
		}catch(SQLException er1) {
			er1.printStackTrace();
		}
	}
	/**
	  * This method searches for all incomplete projects and displays them on the console 
	  *	@param statement Object
	  */
	private static void findIncompleteProject(Statement statement) {
		try {
			String incompleteProject = "SELECT * FROM Project WHERE CompletionStatus = 'incomplete'" ; 
			ResultSet results = statement.executeQuery(incompleteProject);
				// Print the search results
				while (results.next()) {
					System.out.println(
							results.getInt("ProjectNumber") + ", "+ results.getString("ProjectName") + ", "
									+ results.getString("BuildingType") + ", " + results.getString("ProjectAddress") + ", "
									+ results.getInt("ERF") + ", " + results.getFloat("TotalFee") + ", "
									+ results.getFloat("AmountPaid") + ", " + results.getDate("Deadline") + ", "
									+ results.getString("CompletionStatus") + ", " + results.getDate("CompletionDate"));
				}
		}catch(SQLException er) {
			er.printStackTrace();
		}
	}
	
	/**
	  * This method searches for all incomplete projects which are passed their deadline and displays them on the console 
	  * @param statement Object
	  */	
	private static void findOverdueProject(Statement statement) {
		try {
			// Search for projects where they are both incomplete and the deadline is passed the current date
			String selectProject = "SELECT * FROM Project WHERE CompletionStatus = 'incomplete' AND Deadline < CURDATE()" ; 
			ResultSet results = statement.executeQuery(selectProject);
				// Print the search results
				while (results.next()) {
					System.out.println(
							results.getInt("ProjectNumber") + ", " + results.getString("ProjectName") + ", "
							+ results.getString("BuildingType") + ", " + results.getString("ProjectAddress") + ", "
							+ results.getInt("ERF") + ", " + results.getDouble("TotalFee") + ", "
							+ results.getDouble("AmountPaid") + ", " + results.getDate("Deadline") + ", "
							+ results.getString("CompletionStatus") +  ", " + results.getDate("CompletionDate"));
				}
		}catch(SQLException er) {
			er.printStackTrace();
		}
	}
	
	/**
	  * This method searches for projects in the table 
	  * The user is asked to select project by name or number and the information is retrieved from the database table and displayed
	  * @param statement Object
	  * @param sc Scanner
	  */	
	private static void searchProject(Statement statement, Scanner sc) {
		try {
			String choiceName = "";
			int choiceNum = 0;
			ResultSet results;
			// Prompt user to select whether to find project using project name or project number
			System.out.println("Enter option : \na. To enter project name \nb. To enter project number ");
			String option = sc.nextLine().toLowerCase();

			if(option.equals("a")) {
				System.out.println("Enter project name: ");
				choiceName =  sc.nextLine();
				// Execute SQL command to find project
				String incompleteProject = "SELECT * FROM Project WHERE ProjectName ='" + choiceName +"'" ; 
				results = statement.executeQuery(incompleteProject);
				
				// Print the search results
				while (results.next()) {
					System.out.println(results.getInt("ProjectNumber") + ", " + results.getString("ProjectName") + ", "
									+ results.getString("BuildingType") + ", "+ results.getString("ProjectAddress") + ", "
									+ results.getInt("ERF") + ", " + results.getDouble("TotalFee") + ", "
									+ results.getFloat("AmountPaid") + ", "+ results.getDate("Deadline") + ", "
									+ results.getString("CompletionStatus") + ", "+ results.getDate("CompletionDate"));
				}
					
			}else if(option.equals("b")) {
				System.out.println("Enter project number: ");
				choiceNum = sc.nextInt();
				sc.nextLine();	
				// Execute SQL command to find project
				String incompleteProject = "SELECT * FROM Project WHERE ProjectNumber =" + choiceNum;
				results = statement.executeQuery(incompleteProject);
				
				// Print the search results
				while (results.next()) {
					System.out.println(results.getInt("ProjectNumber") + ", " + results.getString("ProjectName") + ", "
							+ results.getString("BuildingType") + ", "+ results.getString("ProjectAddress") + ", "
							+ results.getInt("ERF") + ", " + results.getDouble("TotalFee") + ", "
							+ results.getFloat("AmountPaid") + ", "+ results.getDate("Deadline") + ", "
							+ results.getString("CompletionStatus") + ", "+ results.getDate("CompletionDate"));
				}
				
			}else {
				System.out.println("Invalid option entered, back to main menu");	
			}		
		}catch(Exception e) {
			e.printStackTrace();
		}	
	}
	

	/**
	  * This method finalizes projects in the table 
	  * The user is asked to select project by name or number and  status is changed from incomplete to finalized and the completion date has to be entered by the user
	  * An invoice is displayed in the project is not fully paid 
	  * @param statement Object
	  * @param sc Scanner
	  */
	private static void finalizeProject(Statement statement, Scanner sc) {
		try {
			String choiceName = "";
			int choiceNum = 0;
			int rowsAffected;
			// Retrieve values
			double sum = 0;
			double total = 0;
			double amount = 0;
			Date date = null;
			String custNum = "";
			String custEmail = "";
						
			// Prompt user to select whether to find project using project name or project number
			System.out.println("Enter option: \na. To enter project name \nb. To enter project number ");
			String option = sc.nextLine().toLowerCase();
			
			if(option.equals("a")) {
				System.out.println("Enter project name: ");
				choiceName =  sc.nextLine();
					
			}else if(option.equals("b")) {
				System.out.println("Enter project number: ");
				choiceNum = sc.nextInt();
				sc.nextLine();
						
			}else {
				System.out.println("Invalid option entered");
			}
			String newStatus = "finalized";
			System.out.println("Enter current date (yyyy-mm-dd): ");
			String setDate = sc.nextLine();
			// Execute SQL command to update project status
			String finalizedStatus = "UPDATE Project SET CompletionStatus = '" + newStatus + "', CompletionDate = '"+ setDate + "' WHERE ProjectName ='" 
										+ choiceName + "' OR ProjectNumber =" + choiceNum;	
			rowsAffected = statement.executeUpdate(finalizedStatus);
			System.out.println("Query complete, " + rowsAffected + " rows found.");
			
			// Execute SQL command to get amounts from project
			String getAmounts = "SELECT * FROM Project WHERE ProjectName ='" + choiceName + "' OR ProjectNumber =" + choiceNum; 
			ResultSet results = statement.executeQuery(getAmounts);
			
			// Get amounts and calculate money owed and get project completion date
			while (results.next()) {
				total = results.getDouble("TotalFee");
				amount = results.getDouble("AmountPaid");
				sum = total - amount;
				date = results.getDate("CompletionDate");
			}
			// Execute SQL command to get amounts from project
			String getCustomerInfo = "SELECT * FROM ProjectPeople WHERE Role = 'Customer' AND ProjectNumber ="+ choiceNum;
			ResultSet result = statement.executeQuery(getCustomerInfo);			

			// Get customer details for invoice
			while (result.next()) {
				custNum = result.getString("PhoneNumber");
				custEmail = result.getString("EmailAddress");
			}
			
			if(sum > 0) {
				System.out.println("-Project Invoice-  \nCustomer Phone number : " + custNum + "\nCustomer E-mail address : " + custEmail 
									+ "\nAmount still owed is : R" + sum + "\nCompletion date : " + date + "" );
			}else {
				System.out.println("Project fully paid");
			}	
		}catch(Exception e) {
			e.printStackTrace();
		}		
	}	
}
