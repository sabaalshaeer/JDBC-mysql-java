package projects;

import java.math.BigDecimal;
import java.util.*;

import projects.entity.*;

import projects.exception.DbException;
import projects.service.ProjectService;

public class Main {

	// inject ProjectService
	ProjectService projectService = new ProjectService();
	Project curProject;

	// create Menu Application =>

	// Declare a variable to represent the list of Operations
	// @formatter:off
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a Project",
			"4) Update Project details",
			"5) Delete a project"
	);
	// @formatter:on

	private Scanner scanner = new Scanner(System.in);

	// this method to display the menu selections
	private void processUserSelections() {
		boolean done = false;
		while (!done) {
			try {
				int selection = getUserSelection();
				switch (selection) {
				case -1:
					done = exitMenu();
					break;

				// collect project details and save them in the project table
				case 1:
					createProject();
					break;

				// return a list of projects
				case 2:
					listProject();
					break;

				// select specific project
				case 3:
					SelectProject();
					break;
					
				//update project details
				case 4:
					updateProjectDetails();
					break;
					
				//Delete a project
				case 5:
					deleteProject();
					break;

				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");

				}
			} catch (Exception e) {
				System.out.println("\nError: " + e + " Try again. ");
				e.printStackTrace();
			}
		}
	}

	private void deleteProject() {
		//call list of projects
		listProject();
		//collect project from user by project id
		Integer projectId = getIntInput("Enter the project ID to delete");
		//delete the specific project 
		projectService.deleteProject(projectId);
		System.out.println();
		System.out.println("You have deleted project with Id: "+ projectId);
		//re-call listRecipe method
		listProject();
				
		//if current project is not null and project Id of the current project == to projectId that been pass then set the current project to null
		if(Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId) ){
			curProject = null;
		}
		
		
	}

	private void updateProjectDetails() {
		//if there is no Project selected
		if(Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project.");
			return;
		}
		
		//collect new info of the project from user
		String projectName = getStringInput("Enter the project name ");
		BigDecimal estimatedHours = getDecimalInput("Enter estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter Actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty(1-5)");
		String notes = getStringInput("Enter the project notes ");

		//check if obj is not null then set the values
		if(Objects.nonNull(projectName)) {
		Project project = new Project();
		//set project id to the curProjectId
		project.setProjectId(curProject.getProjectId());
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);;
		project.setNotes(notes);
		//modify the change 
		projectService.modifyProjectDetails(project);
		//reread the current project
		curProject = projectService.fetchProjectById(curProject.getProjectId());
		}
		
	}

	private void SelectProject() {
		// to print a list of projects
		listProject();
		
		// Collect a project ID from the user and assign it to an Integer variable
		Integer project_id = getIntInput("Enter aproject Id to select a project");
		
		// To unselect any currently selected project, set the curProject to null
	    curProject = null;

		// Project object to curProject
		curProject = projectService.fetchProjectById(project_id);

	    
	}

	private void listProject() {
		List<Project> projects = projectService.fetchAllProjects();

		System.out.println("\nProjects: ");
		projects.forEach(
				project -> System.out.println("  " + project.getProjectId() + ": " + project.getProjectName()));

	}

	// method to print operations and then accept user input as an Integer
	private int getUserSelection() {
		// call other method
		printOperations();
		Integer input = getIntInput("Enter a menu selection");// pass a prompt
		return Objects.isNull(input) ? -1 : input;// the value -1 will signal the menu processing method to exit the
													// application
	}

	// method to print each variable selection on a separate line in the console
	private void printOperations() {
		System.out.println();
		System.out.println("\nThere are the available selections. Press the Enter key to quit:");

		operations.forEach(line -> System.out.println(" " + line));

		if (Objects.isNull(curProject)) {
			System.out.println("\nYous are not working with a project.");
		} else {
			System.out.println("\nYou are working with project: " + curProject);

		}

	}

	// method to return the user`s menu selection accept String and return Integer
	// this method accepts input from the user and converts it to an Integer
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		// test the value in the variable input if it is null return null
		if (Objects.isNull(input)) {
			return null;
		}
		// test the value returned by the getStringInput() can be converted to an
		// Integer
		try {
			// convert the value of input from String to an Integer and return it
			return Integer.parseInt(input);
		} catch (NumberFormatException nfe) {
			throw new DbException(input + " is not a valid number. Try again.");
		}
	}

	// method to print the prompt and get the input from the user.This is the lowest
	// level input method
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();

		// test the value of input if blank
		// return input.isBlank() ? null : input.trim();
		if (input.isBlank()) {
			return null;
		} else {
			return input.trim();
		}
	}

	private boolean exitMenu() {
		System.out.println("\nExiting the menu. TTFN!");
		return true;
	}

	// Add new Project here =>
	// collect the project details
	private void createProject() {
		String projectName = getStringInput("Enter the project Name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");

		// create an instance of type Project
		Project project = new Project();

		// call setters on the Project Object to set projectName, estimatedHours,
		// actualHours, difficulty and notes
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);

		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: \n" + dbProject);

	}

	private BigDecimal getDecimalInput(String string) {
		String input = getStringInput(string);
		// test the value in the variable input if it is null return null
		if (Objects.isNull(input)) {
			return null;
		}
		try {
			// create a new BigDecimal object and set the number of decimal places (the
			// scale) to 2.
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException nfe) {
			throw new DbException(input + " is not a valid decimal number.");
		}
	}

	// main method is the entry point to the application
	public static void main(String[] args) {
		// DbConnection.getConnection();
		new Main().processUserSelections();

	}
}
