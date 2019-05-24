package toorla.ast.statement;

import toorla.ast.expression.Expression;
import toorla.symbolTable.SymbolTable;
import toorla.typeChecking.typeCheckExceptions.LvalueAssignability;
import toorla.typeChecking.typeCheckExceptions.TypeCheckException;
import toorla.types.Type;
import toorla.types.singleType.VoidType;
import toorla.visitor.Visitor;

public class Assign extends Statement {
	private Expression lvalue;
	private Expression rvalue;

	public Assign(Expression lvalue, Expression rvalue) {
		this.lvalue = lvalue;
		this.rvalue = rvalue;
	}

	public Expression getRvalue() {
		return rvalue;
	}


	public Expression getLvalue() {
		return lvalue;
	}

	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public Type type_check(SymbolTable symbolTable) {
		try	{
			if (!this.getLvalue().lvalue_check(symbolTable))
				throw new LvalueAssignability(line, col);

		}
		catch(TypeCheckException exception){
			exception.emit_error_message();
		}
		return new VoidType();
	}

	@Override
	public String toString() {
		return "Assign";
	}
}