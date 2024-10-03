package collectors;
import org.eclipse.jdt.core.dom.*;

import visitors.ClassDeclarationsCollector;
import visitors.FieldDeclarationCollector;
import visitors.MethodDeclarationsCollector;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ASTStatisticsCollector {
	private List<TypeDeclaration> allClasses = new ArrayList<>();
    private List<MethodDeclaration> allMethods = new ArrayList<>();
    private List<SingleVariableDeclaration> allParameters = new ArrayList<>();
    private Map<TypeDeclaration, Integer> classMethodCount = new HashMap<>();
    private Map<TypeDeclaration, Integer> classFieldCount = new HashMap<>();
    private Set<String> packages = new HashSet<>();

    private int totalLinesOfCode = 0;

    public void collectStatistics(CompilationUnit ast) {
        // Collect packages
        String packageName = ast.getPackage() != null ? ast.getPackage().getName().getFullyQualifiedName() : "";
        if (!packageName.isEmpty()) {
            packages.add(packageName);
        }

        // Collect classes
        ast.accept(new ASTVisitor() {
            @Override
            public boolean visit(TypeDeclaration node) {
                allClasses.add(node);
                classMethodCount.put(node, 0);
                classFieldCount.put(node, 0);
                return super.visit(node);
            }

            @Override
            public void endVisit(TypeDeclaration node) {
                // After visiting a class, count its methods and fields
                int methodCount = 0;
                int fieldCount = 0;
                for (MethodDeclaration method : allMethods) {
                    if (method.getParent() == node) {
                        methodCount++;
                    }
                }
                classMethodCount.put(node, methodCount);
                classMethodCount.put(node, methodCount);

                // Count fields
                for (Object obj : node.bodyDeclarations()) {
                    if (obj instanceof FieldDeclaration) {
                        fieldCount += ((FieldDeclaration) obj).fragments().size();
                    }
                }
                classFieldCount.put(node, fieldCount);
            }

            @Override
            public boolean visit(MethodDeclaration node) {
                allMethods.add(node);
                return super.visit(node);
            }

            @Override
            public boolean visit(SingleVariableDeclaration node) {
                allParameters.add(node);
                return super.visit(node);
            }
        });
        
        // Count total lines of code
        totalLinesOfCode += countLinesOfCode(ast);
    }

    public void displayStatistics() {
        // 1. Nombre de classes
        System.out.println("1. Nombre de classes : " + allClasses.size());

        // 2. Nombre total de lignes de code
        System.out.println("2. Nombre total de lignes de code : " + totalLinesOfCode);

        // 3. Nombre total de méthodes
        System.out.println("3. Nombre total de méthodes : " + allMethods.size());

        // 4. Nombre total de packages
        System.out.println("4. Nombre total de packages : " + packages.size());

        // 5. Nombre moyen de méthodes par classe
        double avgMethodsPerClass = (double) allMethods.size() / allClasses.size();
        System.out.println("5. Nombre moyen de méthodes par classe : " + avgMethodsPerClass);

        // 6. Nombre moyen de lignes de code par méthode
        double avgLinesPerMethod = (double) totalLinesOfCode / allMethods.size();
        System.out.println("6. Nombre moyen de lignes de code par méthode : " + avgLinesPerMethod);

        // 7. Nombre moyen d'attributs par classe
        int totalFields = classFieldCount.values().stream().mapToInt(Integer::intValue).sum();
        double avgFieldsPerClass = (double) totalFields / allClasses.size();
        System.out.println("7. Nombre moyen d'attributs par classe : " + avgFieldsPerClass);

        // 8. Les 10 % des classes qui possèdent le plus grand nombre de méthodes
        displayTopClassesByMethodCount();

        // 9. Les 10 % des classes qui possèdent le plus grand nombre d'attributs
        displayTopClassesByFieldCount();

        // 10. Les classes qui font partie en même temps des deux catégories précédentes
        displayIntersectionClasses();

        // 11. Les classes qui possèdent plus de X méthodes
        displayClassesWithMoreThanXMethods(10); // Replace 10 with your desired value of X

        // 12. Les 10 % des méthodes qui possèdent le plus grand nombre de lignes de code
        displayTopMethodsByLineCount();

        // 13. Le nombre maximal de paramètres par rapport à toutes les méthodes
        displayMaxParametersCount();
    }

    private int countLinesOfCode(CompilationUnit ast) {
        String source = ast.toString();
        return source.split("\\n").length;
    }

    private void displayTopClassesByMethodCount() {
        List<Map.Entry<TypeDeclaration, Integer>> sortedClasses = new ArrayList<>(classMethodCount.entrySet());
        sortedClasses.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        int topCount = (int) Math.ceil(allClasses.size() * 0.1);
        System.out.println("8.Top 10 % des classes avec le plus grand nombre de méthodes :");
        for (int i = 0; i < topCount && i < sortedClasses.size(); i++) {
            Map.Entry<TypeDeclaration, Integer> entry = sortedClasses.get(i);
            System.out.println("Classe : " + entry.getKey().getName() + " | Méthodes : " + entry.getValue());
        }
    }

    private void displayTopClassesByFieldCount() {
        List<Map.Entry<TypeDeclaration, Integer>> sortedClasses = new ArrayList<>(classFieldCount.entrySet());
        sortedClasses.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        int topCount = (int) Math.ceil(allClasses.size() * 0.1);
        System.out.println("9. Top 10 % des classes avec le plus grand nombre d'attributs :");
        for (int i = 0; i < topCount && i < sortedClasses.size(); i++) {
            Map.Entry<TypeDeclaration, Integer> entry = sortedClasses.get(i);
            System.out.println("Classe : " + entry.getKey().getName() + " | Attributs : " + entry.getValue());
        }
    }

    private void displayIntersectionClasses() {
        Set<TypeDeclaration> topMethodClasses = classMethodCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(allClasses.size() / 10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Set<TypeDeclaration> topFieldClasses = classFieldCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(allClasses.size() / 10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        topMethodClasses.retainAll(topFieldClasses);

        System.out.println("10. classes dans les deux catégories (Méthodes et Attributs) : ");
        for (TypeDeclaration cls : topMethodClasses) {
            System.out.println(cls.getName());
        }
    }

    private void displayClassesWithMoreThanXMethods(int x) {
        System.out.println("11. Classes avec plus de " + x + " méthodes :");
        classMethodCount.entrySet().stream()
                .filter(entry -> entry.getValue() > x)
                .forEach(entry -> System.out.println("Classe : " + entry.getKey().getName() + " | Méthodes : " + entry.getValue()));
    }

    private void displayTopMethodsByLineCount() {
        List<MethodDeclaration> sortedMethods = new ArrayList<>(allMethods);
        
        // Filter out methods without a body before sorting
        sortedMethods.removeIf(method -> method.getBody() == null);

        // Sort methods by the number of lines in their body
        sortedMethods.sort((m1, m2) -> {
            int lineCount1 = m1.getBody() != null ? m1.getBody().toString().split("\\n").length : 0;
            int lineCount2 = m2.getBody() != null ? m2.getBody().toString().split("\\n").length : 0;
            return Integer.compare(lineCount2, lineCount1); // Sort in descending order
        });

        int topCount = (int) Math.ceil(sortedMethods.size() * 0.1); // Top 10%
        System.out.println("12. Top 10 % des méthodes avec le plus de lignes de code :");
        for (int i = 0; i < topCount && i < sortedMethods.size(); i++) {
            MethodDeclaration method = sortedMethods.get(i);
            int lineCount = method.getBody() != null ? method.getBody().toString().split("\\n").length : 0;
            System.out.println("Méthode : " + method.getName() + " | Lignes : " + lineCount);
        }
    }


    private void displayMaxParametersCount() {
        int maxParams = allMethods.stream()
                .mapToInt(method -> method.parameters().size())
                .max()
                .orElse(0);

        System.out.println("13. Le nombre maximal de paramètres parmi toutes les méthodes : " + maxParams);
    }

   
}
