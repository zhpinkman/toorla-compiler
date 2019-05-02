package toorla.symbolTable.symbolTableItem.varItems;

import toorla.symbolTable.symbolTableItem.SymbolTableItem;
import toorla.types.Type;

public class VarSymbolTableItem extends SymbolTableItem {
    protected Type varType;

    public static String var_modifier = "var_";
    public String getKey()
    {
        return VarSymbolTableItem.var_modifier + name;
    }
    public Type getVarType() {
        return varType;
    }

    public void setVarType(Type varType) {
        this.varType = varType;
    }
}
