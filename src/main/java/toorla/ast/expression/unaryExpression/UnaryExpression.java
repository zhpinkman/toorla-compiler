package toorla.ast.expression.unaryExpression;

import toorla.ast.expression.Expression;
import toorla.visitor.Visitor;

public abstract class UnaryExpression extends Expression {
    protected Expression expr;

    public UnaryExpression(Expression expr) {
        this.expr = expr;
    }

    public UnaryExpression() {
    }

    public abstract <R> R accept(Visitor<R> visitor);

    public Expression getExpr() {
        return expr;
    }

    public void setExpr(Expression expr) {
        this.expr = expr;
    }
}