package toorla.symbolTable.symbolTableItem.varItems;

import toorla.ast.expression.Expression;
import toorla.types.AnonymousType;
import toorla.types.Type;

public class LocalVariableSymbolTableItem extends VarSymbolTableItem {
    private int index;
    private Type inital_type;

    public LocalVariableSymbolTableItem(String name, int index) {
        this.name = name;
        this.varType = new AnonymousType();
        this.index = index;
    }

    public void setInital_value(Type inital_type) {
        this.inital_type = inital_type;
    }

    public Type getInital_value() {
        return inital_type;
    }

    public int getIndex() {
        return index;
    }


}
