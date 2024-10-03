package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import collectors.ProjectAnalyser;
import processors.ASTProcessor;

public class StatisticsCollectorMain extends AbstractMain {
    
    private static final String QUIT = "3"; // Assuming QUIT is defined as "3"

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
            if (args.length < 1)
                setTestProjectPath(inputReader);
            else
                verifyTestProjectPath(inputReader, args[0]);

            String userInput = "";
            
            do {    
                main.menu();            
                userInput = inputReader.readLine();
                main.processUserInput(userInput);
                Thread.sleep(3000);
                
            } while (!userInput.equals(QUIT));
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    protected void processUserInput(String userInput) {
        switch (userInput) {
		    case "1":
		        ProjectAnalyser analyzer = new ProjectAnalyser(TEST_PROJECT_PATH);
		        analyzer.analyzeProject(); // Collect statistics without calling CallGraph
		        break;

		    case "2":
		        // If you need to implement displaying a call graph, handle that here
		        System.out.println("Call graph display functionality is not implemented.");
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
