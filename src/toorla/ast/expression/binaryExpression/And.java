package toorla.ast.expression.binaryExpression;


import toorla.ast.expression.Expression;
import toorla.symbolTable.SymbolTable;
import toorla.typeChecking.typeCheckExceptions.InvalidOperationOperands;
import toorla.typeChecking.typeCheckExceptions.TypeCheckException;
import toorla.types.Type;
import toorla.types.singleType.BoolType;
import toorla.visitor.Visitor;

public class And extends BinaryExpression {
	public And(Expression lhs, Expression rhs) {
		super(lhs, rhs);
	}

	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visit(this);
	}

	@Override
	public Type type_check(SymbolTable symbolTable) {
		Type first_operand_type = this.lhs.type_check(symbolTable);
		Type second_operand_type = this.rhs.type_check(symbolTable);
		try {
			if (first_operand_type.toString() != "(BoolType)" || second_operand_type.toString() != "(BoolType)")
				throw new InvalidOperationOperands(line, col, this.toString());
		}
		catch (TypeCheckException exception){
			exception.emit_error_message();
		}
		return new BoolType();
	}

	@Override
	public String toString() {
		return "And";
	}
}
