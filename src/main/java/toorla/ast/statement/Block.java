package toorla.ast.statement;

import toorla.symbolTable.SymbolTable;
import toorla.types.Type;
import toorla.types.singleType.VoidType;
import toorla.visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class Block extends Statement {
	public List<Statement> body;

	public Block() {
		body = new ArrayList<>();
	}

	public void addStatement(Statement s) {
		body.add(s);
	}

	public Block(List<Statement> body) {
		this.body = body;
	}

	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public Type type_check(SymbolTable symbolTable) {
		for (Statement statement: body){
			statement.type_check(symbolTable);
		}
		return new VoidType();
	}

	@Override
	public String toString() {
		return "Block";
	}
}