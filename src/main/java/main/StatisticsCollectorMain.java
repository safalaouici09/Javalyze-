package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import collectors.ProjectAnalyser;
import graphs.CallGraph;
import graphs.StaticCallGraph;
import processors.ASTProcessor;

public class StatisticsCollectorMain extends DefaultMain {
    private ASTProcessor processor; // Déclare une variable d'instance pour ASTProcessor

    private static final String PROJECTS_DIRECTORY = "/home/safa/Bureau/Tp1_partie2/projectsJava"; 

    @Override
    protected void menu() {
        StringBuilder builder = new StringBuilder();
        builder.append("1. Collecter les statistiques.");
        builder.append("\n2. Afficher le graphique d'appel.");
        builder.append("\n3. Quitter.");
        System.out.println(builder);
    }

    public static void main(String[] args) {
        StatisticsCollectorMain main = new StatisticsCollectorMain();
        BufferedReader inputReader;

        try {
            inputReader = new BufferedReader(new InputStreamReader(System.in));

            // Vérifier si le répertoire des projets existe
            File projectDir = new File(PROJECTS_DIRECTORY);
            if (!projectDir.exists() || !projectDir.isDirectory()) {
                System.err.println("Erreur : Le répertoire des projets " + PROJECTS_DIRECTORY + " n'existe pas ou n'est pas un dossier valide.");
                return;
            }

            // Demander à l'utilisateur le nom du projet
            System.out.println("Projets disponibles :");
            listProjectsInDirectory(projectDir); // Lister les projets disponibles
            System.out.println("Veuillez entrer le nom du projet que vous souhaitez analyser :");

            String projectName = inputReader.readLine();
            File selectedProject = new File(PROJECTS_DIRECTORY, projectName + "/src");

            // Vérifier si le projet sélectionné existe
            while (!selectedProject.exists() || !selectedProject.isDirectory()) {
                System.err.println("Erreur : Le répertoire src du projet " + selectedProject.getPath() + " n'existe pas. Veuillez réessayer :");
                projectName = inputReader.readLine();
                selectedProject = new File(PROJECTS_DIRECTORY, projectName + "/src");
            }

            TEST_PROJECT_PATH = selectedProject.getCanonicalPath(); // Définir le chemin du projet valide

            String userInput = "";
            do {
                main.menu(); // Afficher le menu
                userInput = inputReader.readLine();
                main.processUserInput(userInput); // Traiter l'entrée
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
            System.err.println("Erreur : Impossible de lister les projets dans " + projectsDir.getPath());
        }
    }

    @Override
    protected void processUserInput(String userInput) {
        CallGraph callGraph = (CallGraph) processor; 
        // Cette méthode doit être correctement implémentée selon vos besoins
        switch (userInput) {
            case "1":
                System.out.println("Collecte des statistiques pour le projet situé à " + TEST_PROJECT_PATH);
                ProjectAnalyser analyzer = new ProjectAnalyser(TEST_PROJECT_PATH);
                analyzer.analyzeProject(); 
                break;

            case "2":
                System.out.println("Affichage du graphique d'appel pour le projet situé à " + TEST_PROJECT_PATH);
                try {
                    callGraph = StaticCallGraph.createCallGraph(TEST_PROJECT_PATH);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                callGraph.log();
                break;

            case "3":  // Option de quitter
                System.out.println("Merci d'avoir utilisé le programme ! Au revoir !");
                System.exit(0);  // Quitte le programme
                break;

            default:
                System.err.println("Désolé, entrée incorrecte. Veuillez réessayer.");
                break;
        }
    }
}
