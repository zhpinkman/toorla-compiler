package toorla.ast.expression;


import toorla.symbolTable.SymbolTable;
import toorla.types.Type;
import toorla.visitor.Visitor;

public class NewClassInstance extends Expression {
    private Identifier className;

    public NewClassInstance(Identifier className) {
        this.className = className;
    }

    public Identifier getClassName() {
        return className;
    }


    @Override
    public String toString() {
        return "NewClass";
    }

    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Type type_check(SymbolTable symbolTable) {
        return null;
    }

    @Override
    public Boolean lvalue_check(SymbolTable symbolTable) {
        return true;
    }
}
