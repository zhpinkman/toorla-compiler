package toorla.ast.expression;

import toorla.symbolTable.SymbolTable;
import toorla.types.Type;
import toorla.visitor.Visitor;

public class FieldCall extends Expression {
    private Expression instance;
    private Identifier field;
    public FieldCall(Expression instance , Identifier field )
    {
        this.instance = instance;
        this.field = field;
    }

    public Expression getInstance() {
        return instance;
    }


    public Identifier getField() {
        return field;
    }


    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit( this );
    }

    @Override
    public Type type_check(SymbolTable symbolTable) {
        return null;
    }

    @Override
    public String toString() {
        return "FieldCall";
    }

    @Override
    public Boolean lvalue_check(SymbolTable symbolTable) {
        if (instance.lvalue_check(symbolTable))
            return true;
        else
            return false;
    }
}
