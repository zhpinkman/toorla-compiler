package toorla.ast.expression;

import toorla.ast.expression.value.IntValue;
import toorla.types.singleType.SingleType;
import toorla.visitor.Visitor;

public class NewArray extends Expression {
    private IntValue length;
    private SingleType type;

    public NewArray(SingleType type, IntValue length) {
        this.length = length;
        this.type = type;
    }

    public IntValue getLength() {
        return length;
    }

    public void setLength(IntValue length) {
        this.length = length;
    }

    public SingleType getType() {
        return type;
    }

    public void setType(SingleType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "NewArray";
    }

    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}
