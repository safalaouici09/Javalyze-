package visitors;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;

public class FieldDeclarationVisitor extends ASTVisitor {
    /* ATTRIBUTS */
    private List<FieldDeclaration> declarationsDeChamps = new ArrayList<>();

    /* MÉTHODES */
    @Override
    public boolean visit(FieldDeclaration declarationDeChamp) {
        // Ajouter la FieldDeclaration rencontrée à la liste
        declarationsDeChamps.add(declarationDeChamp);
        return super.visit(declarationDeChamp);
    }
    
    // Méthode getter pour récupérer toutes les déclarations de champs collectées
    public List<FieldDeclaration> getDeclarationsDeChamps() {
        return declarationsDeChamps;
    }
    
    // Vérifier si la liste des déclarations de champs est vide
    public boolean estVide() {
        return declarationsDeChamps.isEmpty();
    }

    // Renommer pour garder la cohérence
    public List<FieldDeclaration> obtenirChamps() {
        return declarationsDeChamps;
    }
}
