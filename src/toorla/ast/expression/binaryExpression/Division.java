package toorla.ast.expression.binaryExpression;

import toorla.ast.expression.Expression;
import toorla.visitor.Visitor;

public class Division extends BinaryExpression {

	public Division(Expression lhs, Expression rhs) {
		super(lhs, rhs);
	}

	public Division() {
		super( null , null );
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return "Div";
	}
}
