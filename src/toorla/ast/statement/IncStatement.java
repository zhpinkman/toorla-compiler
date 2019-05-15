package toorla.ast.statement;

import toorla.ast.expression.Expression;
import toorla.symbolTable.SymbolTable;
import toorla.typeChecking.TypeCheck;
import toorla.typeChecking.typeCheckExceptions.InvalidIncDecOperand;
import toorla.typeChecking.typeCheckExceptions.TypeCheckException;
import toorla.types.Type;
import toorla.types.singleType.VoidType;
import toorla.visitor.Visitor;

public class IncStatement extends Statement {
    private Expression operand;

    public IncStatement(Expression operand) {
        this.operand = operand;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit( this );
    }

    @Override
    public Type type_check(SymbolTable symbolTable) {
        try {
            if (!operand.lvalue_check(symbolTable))
                throw new InvalidIncDecOperand(line, col, this.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public String toString() {
        return "IncStat";
    }

    public Expression getOperand() {
        return operand;
    }

}
