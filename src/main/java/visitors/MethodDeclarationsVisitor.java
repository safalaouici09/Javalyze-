package visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodDeclarationsVisitor extends ASTVisitor {
    /* ATTRIBUTS */
    private List<MethodDeclaration> declarationsDeMethode = new ArrayList<>();

    /* MÉTHODES */
    @Override
    public boolean visit(MethodDeclaration declarationDeMethode) {
        declarationsDeMethode.add(declarationDeMethode);
        return false; // Retourne false pour ne pas descendre dans l'arbre d'AST
    }
    
    // Méthode getter pour récupérer toutes les déclarations de méthode collectées
    public List<MethodDeclaration> getDeclarationsDeMethode() {
        return declarationsDeMethode;
    }
}
