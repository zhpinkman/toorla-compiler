package toorla.ast.expression;


import toorla.visitor.Visitor;

public class NewClassInstance extends Expression {
    private Identifier className;

    public NewClassInstance(Identifier className) {
        this.className = className;
    }

    public Identifier getClassName() {
        return className;
    }

    public void setClassName(Identifier className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "NewClass";
    }

    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}
