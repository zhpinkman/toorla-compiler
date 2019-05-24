package toorla.ast.statement;

import toorla.ast.expression.Expression;
import toorla.symbolTable.SymbolTable;
import toorla.typeChecking.typeCheckExceptions.PrintArgException;
import toorla.typeChecking.typeCheckExceptions.TypeCheckException;
import toorla.types.Type;
import toorla.types.singleType.VoidType;
import toorla.visitor.Visitor;

public class PrintLine extends Statement {
	private Expression arg;

	public PrintLine(Expression arg) {
		this.arg = arg;
	}

    public Expression getArg() {
        return arg;
    }


	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public Type type_check(SymbolTable symbolTable) {
		Type expr_type = arg.type_check(symbolTable);
		try{
			String str = expr_type.toString();
			if (str != "(IntType)" && str != "(StringType)" && str != "(ArrayType,IntType)")
				throw new PrintArgException(line, col);
		}
		catch (TypeCheckException exception){
			exception.emit_error_message();
		}
		return new VoidType();
	}

	@Override
	public String toString() {
		return "PrintLine";
	}
}