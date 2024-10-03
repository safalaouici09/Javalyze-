package collectors;

import java.io.IOException;
import java.util.List;
import org.eclipse.jdt.core.dom.CompilationUnit;

import parsers.EclipseJDTASTParser;

public class ProjectAnalyser {
    private String projectPath;

    public ProjectAnalyser(String projectPath) {
        this.projectPath = projectPath;
    }

    public void analyzeProject() {
        EclipseJDTASTParser parser = new EclipseJDTASTParser(projectPath);
        parser.configure();

        try {
            // Parse all Java files in the project
            List<CompilationUnit> compilationUnits = parser.parseProject();

            // Initialize the statistics collector
            ASTStatisticsCollector statisticsCollector = new ASTStatisticsCollector();

            // Collect statistics for each CompilationUnit
            for (CompilationUnit cu : compilationUnits) {
                statisticsCollector.collectStatistics(cu);
            }

            // Display the statistics
            statisticsCollector.displayStatistics();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
