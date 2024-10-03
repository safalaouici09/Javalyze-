package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import collectors.ProjectAnalyser;
import graphs.CallGraph;
import graphs.StaticCallGraph;
import processors.ASTProcessor;

public class StatisticsCollectorMain extends AbstractMain {
    
    private static final String QUIT = "3"; // Assuming QUIT is defined as "3"
    private ASTProcessor processor; // Declare an instance variable for ASTProcessor

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
            if (args.length < 1) {
                setTestProjectPath(inputReader);
            } else {
                verifyTestProjectPath(inputReader, args[0]);
            }

            String userInput = "";
            String projectPath = TEST_PROJECT_PATH; // Assuming TEST_PROJECT_PATH is correctly set

            
            //main.processor = new ASTProcessor(projectPath); 

            do {    
                main.menu();            
                userInput = inputReader.readLine();
                main.processUserInput(userInput); // Call processUserInput with userInput only
                Thread.sleep(3000);
                
            } while (!userInput.equals(QUIT));
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    protected void processUserInput(String userInput) {
        CallGraph callGraph = (CallGraph) processor; // Use the instance variable

        switch (userInput) {

            case "1":
                ProjectAnalyser analyzer = new ProjectAnalyser(TEST_PROJECT_PATH);
                analyzer.analyzeProject(); // Collect statistics without calling CallGraph
                break;

            case "2":
                try {
                    callGraph = StaticCallGraph.createCallGraph(TEST_PROJECT_PATH);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                callGraph.log();
                break;

            case QUIT:
                System.out.println("Bye...");
                return;
                
            default:
                System.err.println("Sorry, wrong input. Please try again.");
                return;
        }
    }
}
