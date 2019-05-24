package toorla.ast.statement;

import toorla.symbolTable.SymbolTable;
import toorla.types.Type;
import toorla.types.singleType.VoidType;
import toorla.visitor.Visitor;

public class Skip extends Statement {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit( this );
    }

    @Override
    public Type type_check(SymbolTable symbolTable) {
        return new VoidType();
    }

    @Override
    public String toString() {
        return "(Skip)";
    }
}
