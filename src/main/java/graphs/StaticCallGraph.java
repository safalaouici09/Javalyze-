package graphs;

import java.io.IOException;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import utility.Utility;
import visitors.ClassDeclarationsVisitor;
import visitors.MethodDeclarationsVisitor;
import visitors.MethodInvocationsVisitor;

public class StaticCallGraph extends CallGraph {

    /* CONSTRUCTEUR : Initialise le graphique d'appels statique avec le chemin du projet */
    private StaticCallGraph(String projectPath) {
        super(projectPath);
    }
    
    /* MÉTHODE STATIC : Crée un graphique d'appels à partir d'une unité de compilation donnée */
    public static StaticCallGraph createCallGraph(String projectPath, CompilationUnit cUnit) {
        StaticCallGraph graph = new StaticCallGraph(projectPath); // Crée une nouvelle instance de StaticCallGraph
        ClassDeclarationsVisitor classCollector = new ClassDeclarationsVisitor(); // Collecteur de déclarations de classe
        cUnit.accept(classCollector); // Accepte la visite de l'unité de compilation
        
        // Itère sur toutes les classes collectées
        for (TypeDeclaration cls : classCollector.obtenirClasses()) {
            MethodDeclarationsVisitor methodCollector = new MethodDeclarationsVisitor(); // Collecteur de déclarations de méthode
            cls.accept(methodCollector); // Accepte la visite de la classe
            
            // Itère sur toutes les méthodes collectées et les ajoute au graphique
            for (MethodDeclaration method : methodCollector.getDeclarationsDeMethode())
                graph.addMethodAndInvocations(cls, method); // Ajoute les méthodes et leurs invocations
        }
        
        return graph; // Retourne le graphique d'appels créé
    }
    
    /* MÉTHODE STATIC : Crée un graphique d'appels à partir du chemin du projet */
    public static StaticCallGraph createCallGraph(String projectPath) 
            throws IOException {
        StaticCallGraph graph = new StaticCallGraph(projectPath); // Crée une nouvelle instance de StaticCallGraph
        
        // Itère sur toutes les unités de compilation du projet
        for (CompilationUnit cUnit : graph.parser.parseProject()) {
            // Crée un graphique d'appels partiel pour chaque unité de compilation
            StaticCallGraph partial = StaticCallGraph.createCallGraph(projectPath, cUnit);
            graph.addMethods(partial.getMethods()); // Ajoute les méthodes du graphique partiel
            graph.addInvocations(partial.getInvocations()); // Ajoute les invocations du graphique partiel
        }
        
        return graph; // Retourne le graphique d'appels complet
    }
    
    /* MÉTHODE PRIVÉE : Ajoute une méthode et ses invocations au graphique */
    private boolean addMethodAndInvocations(TypeDeclaration cls, MethodDeclaration method) {
        if (method.getBody() != null) { // Vérifie si la méthode a un corps
            String methodName = Utility.getMethodFullyQualifiedName(cls, method); // Récupère le nom pleinement qualifié de la méthode
            this.addMethod(methodName); // Ajoute la méthode au graphique
            
            MethodInvocationsVisitor invocationCollector = new MethodInvocationsVisitor(); // Collecteur d'invocations de méthode
            this.addInvocations(cls, method, methodName, invocationCollector); // Ajoute les invocations de la méthode
            this.addSuperInvocations(methodName, invocationCollector); // Ajoute les invocations de super
        }
        
        return method.getBody() != null; // Retourne vrai si la méthode a un corps, sinon faux
    }
    
    /* MÉTHODE PRIVÉE : Ajoute les invocations de méthode au graphique */
    private void addInvocations(TypeDeclaration cls, MethodDeclaration method, 
            String methodName, MethodInvocationsVisitor invocationCollector) {
        method.accept(invocationCollector); // Accepte la visite de la méthode
        
        // Itère sur toutes les invocations de méthode collectées
        for (MethodInvocation invocation : invocationCollector.getInvocationsDeMethode()) {
            String invocationName = getMethodInvocationName(cls, invocation); // Récupère le nom de l'invocation
            this.addMethod(invocationName); // Ajoute le nom de l'invocation au graphique
            this.addInvocation(methodName, invocationName); // Ajoute la relation entre la méthode et son invocation
        }
    }

    /* MÉTHODE PRIVÉE : Récupère le nom d'une invocation de méthode */
    private String getMethodInvocationName(TypeDeclaration cls, MethodInvocation invocation) {
        Expression expr = invocation.getExpression(); // Récupère l'expression de l'invocation
        String invocationName = ""; // Nom d'invocation initialisé à vide
        
        if (expr != null) { // Vérifie si l'expression n'est pas nulle
            ITypeBinding type = expr.resolveTypeBinding(); // Résout le type de l'expression
            
            if (type != null) 
                // Crée le nom d'invocation en utilisant le type qualifié
                invocationName = type.getQualifiedName() + "::" + invocation.getName().toString();
            else
                // Crée le nom d'invocation avec l'expression
                invocationName = expr + "::" + invocation.getName().toString();
        } else
            // Crée le nom d'invocation en utilisant le nom de la classe
            invocationName = Utility.getClassFullyQualifiedName(cls) 
                + "::" + invocation.getName().toString();
        
        return invocationName; // Retourne le nom d'invocation
    }
    
    /* MÉTHODE PRIVÉE : Ajoute les invocations de super méthode au graphique */
    private void addSuperInvocations(String methodName, MethodInvocationsVisitor invocationCollector) {
        // Itère sur toutes les invocations de super méthode collectées
        for (SuperMethodInvocation superInvocation : invocationCollector.getInvocationsDeSuperMethode()) {
            String superInvocationName = superInvocation.getName().getFullyQualifiedName(); // Récupère le nom de l'invocation de super
            this.addMethod(superInvocationName); // Ajoute l'invocation de super au graphique
            this.addInvocation(methodName, superInvocationName); // Ajoute la relation entre la méthode et son invocation de super
        }
    }
}
