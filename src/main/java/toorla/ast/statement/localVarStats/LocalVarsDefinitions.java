package toorla.ast.statement.localVarStats;

import toorla.ast.statement.Statement;
import toorla.symbolTable.SymbolTable;
import toorla.types.Type;
import toorla.visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class LocalVarsDefinitions extends Statement {
    private List<LocalVarDef> definitions;

    public LocalVarsDefinitions() {
        definitions = new ArrayList<>();
    }

    public void addVarDefinition(LocalVarDef localVarDefinition) {
        definitions.add(localVarDefinition);
    }

    public List<LocalVarDef> getVarDefinitions() {
        return definitions;
    }

    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Type type_check(SymbolTable symbolTable) {
        return null;
    }

    @Override
    public String toString() {
        return "LocalVarDefContainer";
    }
}