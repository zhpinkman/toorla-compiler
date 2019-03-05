package toorla.ast.expression.binaryExpression;

import toorla.ast.expression.Expression;
import toorla.visitor.Visitor;

public class Plus extends BinaryExpression {

	public Plus(Expression lhs, Expression rhs) {
		super(lhs, rhs);
	}

	public Plus() {
		super( null , null );
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return "Plus";
	}
}