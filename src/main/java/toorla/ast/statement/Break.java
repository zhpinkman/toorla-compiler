package toorla.ast.statement;

import toorla.symbolTable.SymbolTable;
import toorla.typeChecking.typeCheckExceptions.IllegalLoopStatementActions;
import toorla.typeChecking.typeCheckExceptions.TypeCheckException;
import toorla.types.Type;
import toorla.types.singleType.VoidType;
import toorla.visitor.Visitor;

public class Break extends Statement {

    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Type type_check(SymbolTable symbolTable) {
        try{
            if (loop_depth == 0)
                throw new IllegalLoopStatementActions(line, col, this.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public String toString() {
        return "(Break)";
    }
}