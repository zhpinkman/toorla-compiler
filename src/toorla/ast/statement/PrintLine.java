package toorla.ast.statement;

import toorla.ast.expression.Expression;
import toorla.visitor.Visitor;

public class PrintLine extends Statement {
	private Expression arg;

	public PrintLine(Expression arg) {
		this.arg = arg;
	}

    public Expression getArg() {
        return arg;
    }


	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return "PrintLine";
	}
}