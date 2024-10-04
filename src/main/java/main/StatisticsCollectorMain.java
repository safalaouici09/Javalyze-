package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import collectors.ProjectAnalyser;
import graphs.CallGraph;
import graphs.StaticCallGraph;
import processors.ASTProcessor;

public class StatisticsCollectorMain extends AbstractMain {
	  private ASTProcessor processor; // Declare an instance variable for ASTProcessor


    private static final String QUIT = "3"; // Quit option defined as "3"
    private static final String PROJECTS_DIRECTORY = "/home/safa/Bureau/Tp1_partie2/projectsJava"; // Define the path to your projects directory
    
    @Override
    protected void menu() {
        StringBuilder builder = new StringBuilder();
        builder.append("1. Collect Statistics.");
        builder.append("\n2. Display Call Graph.");
        builder.append("\n" + QUIT + ". To quit.");
        System.out.println(builder);
    }

    public static void main(String[] args) {
        StatisticsCollectorMain main = new StatisticsCollectorMain();
        BufferedReader inputReader;

        try {
            inputReader = new BufferedReader(new InputStreamReader(System.in));

            // Check if the projects directory exists
            File projectDir = new File(PROJECTS_DIRECTORY);
            if (!projectDir.exists() || !projectDir.isDirectory()) {
                System.err.println("Error: The projects directory " + PROJECTS_DIRECTORY + " doesn't exist or isn't a valid folder.");
                return;
            }

            // Ask user for project name
            System.out.println("Available projects:");
            listProjectsInDirectory(projectDir); // List available projects
            System.out.println("Please enter the name of the project you want to analyze:");

            String projectName = inputReader.readLine();
            File selectedProject = new File(PROJECTS_DIRECTORY, projectName + "/src");

            // Verify if selected project exists
            while (!selectedProject.exists() || !selectedProject.isDirectory()) {
                System.err.println("Error: The project src directory " + selectedProject.getPath() + " doesn't exist. Please try again:");
                projectName = inputReader.readLine();
                selectedProject = new File(PROJECTS_DIRECTORY, projectName + "/src");
            }

            TEST_PROJECT_PATH = selectedProject.getCanonicalPath(); // Set the valid project path

            String userInput = "";
            do {
                main.menu(); // Show menu
                userInput = inputReader.readLine();
                main.processUserInput(userInput); // Process input
                Thread.sleep(3000);
            } while (!userInput.equals(QUIT));

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected static void listProjectsInDirectory(File projectsDir) {
        File[] projects = projectsDir.listFiles(File::isDirectory);
        if (projects != null) {
            for (File project : projects) {
                System.out.println("- " + project.getName());
            }
        } else {
            System.err.println("Error: Could not list projects in " + projectsDir.getPath());
        }
    }

    @Override
    protected void processUserInput(String userInput) {
            CallGraph callGraph = (CallGraph) processor; 
        // This method should be properly implemented according to your needs
        switch (userInput) {
            case "1":
                System.out.println("Collecting statistics for project at " + TEST_PROJECT_PATH);
               ProjectAnalyser analyzer = new ProjectAnalyser(TEST_PROJECT_PATH);
                analyzer.analyzeProject(); 
                break;

            case "2":
                System.out.println("Displaying Call Graph for project at " + TEST_PROJECT_PATH);
                try {
                    callGraph = StaticCallGraph.createCallGraph(TEST_PROJECT_PATH);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                callGraph.log();
                break;

            case QUIT:
                System.out.println("Bye...");
                break;

            default:
                System.err.println("Sorry, wrong input. Please try again.");
                break;
        }
    }
}
