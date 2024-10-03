package visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class FieldDeclarationCollector extends ASTVisitor {
	/* ATTRIBUTES */
	private List<FieldDeclaration> fieldDeclarations = new ArrayList<>();

	/* METHODS */
	@Override
	public boolean visit(FieldDeclaration fieldDeclaration) {
		// Add the encountered FieldDeclaration to the list
		fieldDeclarations.add(fieldDeclaration);
		return super.visit(fieldDeclaration);
	}
	
	// Getter method to retrieve all collected field declarations
	public List<FieldDeclaration> getFieldDeclarations() {
		return fieldDeclarations;
	}
	
	// Check if the fieldDeclarations list is empty
	public boolean isEmpty() {
		return fieldDeclarations.isEmpty();
	}

	public List<FieldDeclaration> getFields() {
		return fieldDeclarations;
	}
}
