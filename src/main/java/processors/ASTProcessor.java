package processors;

import parsers.JdtASTParser;

public abstract class ASTProcessor extends Processor<JdtASTParser>{
	/* CONSTRUCTOR */
	public ASTProcessor(String projectPath) {
		super(projectPath);
	}
	
	/* METHODS */
	@Override
	public void setParser(String projectPath) {
		parser = new JdtASTParser(projectPath);
	}
	
	public void setParser(JdtASTParser parser) {
		this.parser = parser;
	}
}
