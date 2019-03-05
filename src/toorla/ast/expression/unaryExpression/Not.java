package toorla.ast.expression.unaryExpression;

import toorla.ast.expression.Expression;
import toorla.visitor.Visitor;

public class Not extends UnaryExpression {

	public Not(Expression expr) {
		super(expr);
	}

	public Not() {
		super( null );
	}

	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return "Not";
	}
}