package toorla.ast.expression;

import toorla.visitor.Visitor;

public class Self extends Expression {
    @Override
    public String toString() {
        return "(Self)";
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}
