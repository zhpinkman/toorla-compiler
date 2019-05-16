package toorla.symbolTable.symbolTableItem.varItems;

import toorla.ast.expression.Expression;
import toorla.types.AnonymousType;

public class LocalVariableSymbolTableItem extends VarSymbolTableItem {
    private int index;
    private Expression inital_value;

    public LocalVariableSymbolTableItem(String name, int index) {
        this.name = name;
        this.varType = new AnonymousType();
        this.index = index;
    }

    public void setInital_value(Expression inital_value) {
        this.inital_value = inital_value;
    }

    public Expression getInital_value() {
        return inital_value;
    }

    public int getIndex() {
        return index;
    }


}
