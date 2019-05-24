package toorla.ast.statement;

import toorla.ast.expression.Expression;
import toorla.symbolTable.SymbolTable;
import toorla.typeChecking.typeCheckExceptions.InvalidLoopCondition;
import toorla.typeChecking.typeCheckExceptions.InvalidOperationOperands;
import toorla.typeChecking.typeCheckExceptions.TypeCheckException;
import toorla.types.Type;
import toorla.types.singleType.VoidType;
import toorla.visitor.Visitor;

public class While extends Statement {
	public Expression expr;
	public Statement body;

	public While(Expression expr, Statement body) {
		this.expr = expr;
		this.body = body;
	}

	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public Type type_check(SymbolTable symbolTable) {
		loop_depth ++;
		Type expr_type = expr.type_check(symbolTable);
		Type body_type = body.type_check(symbolTable);

		try	{
			if (expr_type.toString() != "(BoolType)")
				throw new InvalidLoopCondition(line, col, this.toString());
		}
		catch (TypeCheckException exception){
			exception.emit_error_message();
		}
		loop_depth --;
		return body_type;
	}

	@Override
	public String toString() {
		return "While";
	}
}