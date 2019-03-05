package toorla.ast.statement;

import toorla.visitor.Visitor;

public class Skip extends Statement {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit( this );
    }

    @Override
    public String toString() {
        return "(Skip)";
    }
}
