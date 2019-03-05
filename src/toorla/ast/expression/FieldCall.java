package toorla.ast.expression;

import toorla.visitor.Visitor;

public class FieldCall extends Expression {
    private Expression instance;
    private Identifier field;
    public FieldCall( Expression instance , Identifier field )
    {
        this.instance = instance;
        this.field = field;
    }

    public Expression getInstance() {
        return instance;
    }

    public void setInstance(Expression instance) {
        this.instance = instance;
    }

    public Identifier getField() {
        return field;
    }

    public void setField(Identifier field) {
        this.field = field;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit( this );
    }

    @Override
    public String toString() {
        return "FieldCall";
    }
}
