package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import processors.Processor;

public abstract class AbstractMain {
    public static String TEST_PROJECT_PATH;
    public static final String QUIT = "0";
    public static final String PROJECTS_DIRECTORY = "projectsJava"; // Folder where Java projects are stored

    // Method to display available projects and set the path to the selected project's src/ folder
    protected static void setTestProjectPath(BufferedReader inputReader) throws IOException {
        File projectsFolder = new File(PROJECTS_DIRECTORY);
		System.err.println(projectsFolder);
        if (!projectsFolder.exists() || !projectsFolder.isDirectory()) {
			System.err.println(PROJECTS_DIRECTORY);
            if (!projectsFolder.exists() || !projectsFolder.isDirectory()) {
				System.err.println("Error: The projects directory " + projectsFolder.getAbsolutePath() + " doesn't exist or isn't a valid folder.");
				return;
			}
            return;
        }

        // List all projects (directories) inside projectsJava
        File[] projectFolders = projectsFolder.listFiles(File::isDirectory);
        if (projectFolders == null || projectFolders.length == 0) {
            System.err.println("Error: No Java projects found in " + PROJECTS_DIRECTORY);
            return;
        }

        // Display the available projects
        System.out.println("Available projects:");
        for (int i = 0; i < projectFolders.length; i++) {
            System.out.println((i + 1) + ". " + projectFolders[i].getName());
        }

        // Prompt the user to select a project by its number
        int selectedProjectIndex = -1;
        while (selectedProjectIndex < 1 || selectedProjectIndex > projectFolders.length) {
            System.out.println("Please select a project by typing its number: ");
            try {
                selectedProjectIndex = Integer.parseInt(inputReader.readLine());
            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter a number corresponding to a project.");
            }
        }

        // Set the selected project path to the project's src/ folder
        File selectedProject = projectFolders[selectedProjectIndex - 1];
        File srcFolder = new File(selectedProject, "src");

        // Verify that the src/ folder exists
        while (!srcFolder.exists() || !srcFolder.isDirectory()) {
            System.err.println("Error: " + srcFolder.getPath() + " either doesn't exist or isn't a src/ folder.");
            System.out.println("Please select another project by typing its number: ");
            selectedProjectIndex = Integer.parseInt(inputReader.readLine());
            selectedProject = projectFolders[selectedProjectIndex - 1];
            srcFolder = new File(selectedProject, "src");
        }

        TEST_PROJECT_PATH = srcFolder.getPath();
        System.out.println("Selected project src/ folder: " + TEST_PROJECT_PATH);
    }

    // Method to verify that a provided path is valid
    protected static void verifyTestProjectPath(BufferedReader inputReader, String userInput) throws IOException {
        File projectFolder = new File(userInput);

        while (!projectFolder.exists() || !userInput.endsWith("src/")) {
            System.err.println("Error: " + userInput + " either doesn't exist or isn't a java project src/ folder. Please try again: ");
            userInput = inputReader.readLine();
            projectFolder = new File(userInput);
        }

        TEST_PROJECT_PATH = userInput;
    }

    protected abstract void menu();

    protected void processUserInput(String userInput) {
        System.err.println("processUserInput(String userInput) isn't implemented for " + getClass().getCanonicalName());
    }

    protected void processUserInput(String userInput, Processor<?> processor) {
        System.err.println("processUserInput(String userInput, Processor<?> processor) isn't implemented for " + getClass().getCanonicalName());
    }

    protected void processUserInput(BufferedReader reader, String userInput, Processor<?> processor) {
        System.err.println("processUserInput(BufferedReader reader, String userInput, Processor<?> processor) isn't implemented for " + getClass().getCanonicalName());
    }
}
