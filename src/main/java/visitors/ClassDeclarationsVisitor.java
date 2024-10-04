package visitors;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ClassDeclarationsVisitor extends ASTVisitor {
    /* ATTRIBUTS */
    private List<TypeDeclaration> classes = new ArrayList<>();
    
    /* MÉTHODES */
    @Override
    public boolean visit(TypeDeclaration declarationDeClasse) {
        // Ajouter la déclaration de classe à la liste si ce n'est pas une interface
        if (!declarationDeClasse.isInterface()) {
            classes.add(declarationDeClasse);
        }
        
        // Retourne faux pour ne pas visiter les enfants
        return false;
    }
    
    // Méthode getter pour récupérer les déclarations de classes collectées
    public List<TypeDeclaration> obtenirClasses() {
        return classes;
    }
}
