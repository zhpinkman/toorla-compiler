package toorla.ast.statement;

import toorla.ast.expression.Expression;
import toorla.visitor.Visitor;

public class Assign extends Statement {
	private Expression lvalue;
	private Expression rvalue;

	public Assign(Expression lvalue, Expression rvalue) {
		this.lvalue = lvalue;
		this.rvalue = rvalue;
	}

	public Expression getRvalue() {
		return rvalue;
	}


	public Expression getLvalue() {
		return lvalue;
	}

	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return "Assign";
	}
}