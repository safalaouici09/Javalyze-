package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public abstract class DefaultMain {
    public static String TEST_PROJECT_PATH;
    public static final String QUIT = "0";
    public static final String PROJECTS_DIRECTORY = "projectsJava"; // Dossier où les projets Java sont stockés

    // Méthode pour définir le chemin du projet de test
    protected static void setTestProjectPath(BufferedReader inputReader) throws IOException {
        File projectsFolder = new File(PROJECTS_DIRECTORY);
        
        if (!projectsFolder.exists() || !projectsFolder.isDirectory()) {
            System.err.println("Erreur : Le répertoire des projets " + projectsFolder.getAbsolutePath() + " n'existe pas ou n'est pas un dossier valide.");
            return;
        }

        // Lister tous les projets (répertoires) à l'intérieur de projectsJava
        File[] projectFolders = projectsFolder.listFiles(File::isDirectory);
        if (projectFolders == null || projectFolders.length == 0) {
            System.err.println("Erreur : Aucun projet Java trouvé dans " + PROJECTS_DIRECTORY);
            return;
        }

        // Afficher les projets disponibles
        System.out.println("Projets disponibles :");
        for (int i = 0; i < projectFolders.length; i++) {
            System.out.println((i + 1) + ". " + projectFolders[i].getName());
        }

        // Demander à l'utilisateur de sélectionner un projet par son numéro
        int selectedProjectIndex = -1;
        while (selectedProjectIndex < 1 || selectedProjectIndex > projectFolders.length) {
            System.out.println("Veuillez sélectionner un projet en tapant son numéro : ");
            try {
                selectedProjectIndex = Integer.parseInt(inputReader.readLine());
            } catch (NumberFormatException e) {
                System.err.println("Entrée invalide. Veuillez entrer un numéro correspondant à un projet.");
            }
        }

        // Définir le chemin du projet sélectionné sur le dossier src/
        File selectedProject = projectFolders[selectedProjectIndex - 1];
        File srcFolder = new File(selectedProject, "src");

        // Vérifier que le dossier src/ existe
        while (!srcFolder.exists() || !srcFolder.isDirectory()) {
            System.err.println("Erreur : " + srcFolder.getPath() + " n'existe pas ou n'est pas un dossier src/.");
            System.out.println("Veuillez sélectionner un autre projet en tapant son numéro : ");
            selectedProjectIndex = Integer.parseInt(inputReader.readLine());
            selectedProject = projectFolders[selectedProjectIndex - 1];
            srcFolder = new File(selectedProject, "src");
        }

        TEST_PROJECT_PATH = srcFolder.getPath();
        System.out.println("Dossier src/ du projet sélectionné : " + TEST_PROJECT_PATH);
    }

    // Méthode pour vérifier qu'un chemin fourni est valide
    protected static void verifyTestProjectPath(BufferedReader inputReader, String userInput) throws IOException {
        File projectFolder = new File(userInput);

        while (!projectFolder.exists() || !userInput.endsWith("src/")) {
            System.err.println("Erreur : " + userInput + " n'existe pas ou n'est pas un dossier src/ d'un projet Java. Veuillez réessayer : ");
            userInput = inputReader.readLine();
            projectFolder = new File(userInput);
        }

        TEST_PROJECT_PATH = userInput;
    }

    protected abstract void menu();

    protected void processUserInput(String userInput) {
        System.err.println("processUserInput(String userInput) n'est pas implémentée pour " + getClass().getCanonicalName());
    }
}
