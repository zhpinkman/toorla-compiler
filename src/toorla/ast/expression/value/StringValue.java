package toorla.ast.expression.value;

import toorla.symbolTable.SymbolTable;
import toorla.types.Type;
import toorla.types.singleType.StringType;
import toorla.visitor.Visitor;

public class StringValue extends Value {
    private String constant;

    public StringValue(String constant) {
        this.constant = constant;
    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

    @Override
    public Type getType() {
        return new StringType();
    }

    @Override
    public String toString() {
        return "(StringValue," + constant + ")";
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit( this );
    }

    @Override
    public Type type_check(SymbolTable symbolTable) {
        return new StringType();
    }

    @Override
    public Boolean lvalue_check(SymbolTable symbolTable) {
        return false;
    }
}
