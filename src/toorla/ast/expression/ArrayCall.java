package toorla.ast.expression;

import toorla.visitor.Visitor;

public class ArrayCall extends Expression {
    private Expression instance;
    private Expression index;
    public ArrayCall(Expression instance , Expression index )
    {
        this.instance = instance;
        this.index = index;
    }

    public Expression getIndex() {
        return index;
    }

    public void setIndex(Expression index) {
        this.index = index;
    }

    public Expression getInstance() {
        return instance;
    }

    public void setInstance(Expression instance) {
        this.instance = instance;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit( this );
    }

    @Override
    public String toString() {
        return "ArrayCall";
    }
}
