package visitors;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

public class MethodInvocationsVisitor extends ASTVisitor {
    /* ATTRIBUTS */
    private List<MethodInvocation> invocationsDeMethode = new ArrayList<>();
    private List<SuperMethodInvocation> invocationsDeSuperMethode = new ArrayList<>();
    
    /* MÉTHODES */
    @Override
    public boolean visit(MethodInvocation invocationDeMethode) {
        invocationsDeMethode.add(invocationDeMethode);
        return super.visit(invocationDeMethode);
    }
    
    @Override
    public boolean visit(SuperMethodInvocation invocationDeSuperMethode) {
        invocationsDeSuperMethode.add(invocationDeSuperMethode);
        return super.visit(invocationDeSuperMethode);
    }
    
    // Méthode getter pour récupérer les invocations de méthode collectées
    public List<MethodInvocation> getInvocationsDeMethode() {
        return invocationsDeMethode;
    }
    
    // Méthode getter pour récupérer les invocations de super méthode collectées
    public List<SuperMethodInvocation> getInvocationsDeSuperMethode() {
        return invocationsDeSuperMethode;
    }
    
    // Vérifie si les listes d'invocations de méthodes sont vides
    public boolean estVide() {
        return invocationsDeMethode.isEmpty() && invocationsDeSuperMethode.isEmpty();
    }
}
