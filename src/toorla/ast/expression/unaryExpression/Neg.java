package toorla.ast.expression.unaryExpression;

import toorla.ast.expression.Expression;
import toorla.symbolTable.SymbolTable;
import toorla.typeChecking.typeCheckExceptions.InvalidOperationOperands;
import toorla.typeChecking.typeCheckExceptions.TypeCheckException;
import toorla.types.Type;
import toorla.visitor.Visitor;

public class Neg extends UnaryExpression {

	public Neg(Expression expr) {
		super(expr);
	}

	public Neg() {
		super( null );
	}

	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public Type type_check(SymbolTable symbolTable) {
		Type type = this.expr.type_check(symbolTable);
		try{
			if (type.toString() != "(IntType)")
				throw new InvalidOperationOperands(line, col, this.toString());
		}
		catch(TypeCheckException exception){
			exception.emit_error_message();
		}
		return type;
	}

	@Override
	public String toString() {
		return "Neg";
	}
}
