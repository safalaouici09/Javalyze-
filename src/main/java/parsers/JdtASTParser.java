package parsers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class JdtASTParser extends Parser<ASTParser> {
    
    /* CONSTRUCTEUR : Initialise le parser AST avec le chemin du projet */
    public JdtASTParser(String projectPath) {
        super(projectPath); // Appelle le constructeur de la classe parent avec le chemin du projet
    }
    
    /* MÉTHODE : Configure le parser AST avec les paramètres spécifiés */
    public void setParser(int level, int kind, boolean resolveBindings, 
            boolean bindingsRecovery, String encoding) {
        parser = ASTParser.newParser(level); // Crée un nouveau parser AST avec le niveau spécifié
        parser.setKind(kind); // Définit le type d'unité à parser (ex: unité de compilation)
        parser.setResolveBindings(resolveBindings); // Définit si les liaisons doivent être résolues
        parser.setBindingsRecovery(bindingsRecovery); // Définit si la récupération des liaisons doit être effectuée
        parser.setCompilerOptions(JavaCore.getOptions()); // Définit les options du compilateur
        parser.setUnitName(""); // Définit le nom de l'unité (peut être laissé vide)
        parser.setEnvironment(new String[] {getJREPath()}, // Définit l'environnement de compilation (JRE)
                new String[] {getProjectPath()}, // Définit le chemin du projet
                new String[] {encoding}, true); // Définit l'encodage des fichiers source
    }
    
    /* MÉTHODE : Parse un fichier source Java et retourne l'unité de compilation correspondante */
    public CompilationUnit parse(File sourceFile) throws IOException {
        Charset platformCharset = null; // Initialisation du jeu de caractères, peut être défini si nécessaire
        // Lit le contenu du fichier source et définit le code source pour le parser
        parser.setSource(FileUtils.readFileToString(sourceFile, platformCharset).toCharArray());
        
        // Crée et retourne l'unité de compilation à partir du code source
        return (CompilationUnit) parser.createAST(null);
    }
    
    /* MÉTHODE : Parse tous les fichiers Java du projet et retourne une liste d'unités de compilation */
    public List<CompilationUnit> parseProject() throws IOException {
        
        List<CompilationUnit> cUnits = new ArrayList<>(); // Liste pour stocker les unités de compilation
        
        // Itère sur tous les fichiers Java du projet et les parse
        for (File sourceFile : listJavaProjectFiles())
            cUnits.add(parse(sourceFile)); // Ajoute l'unité de compilation à la liste
        
        return cUnits; // Retourne la liste des unités de compilation
    }

    /* MÉTHODE OVERRIDE : Configure le parser avec des valeurs par défaut */
    @Override
    public void configure() {
        // Configure le parser pour le niveau JLS4 avec des options par défaut
        setParser(AST.JLS4, ASTParser.K_COMPILATION_UNIT, true, true, "UTF-8");
    }
}
