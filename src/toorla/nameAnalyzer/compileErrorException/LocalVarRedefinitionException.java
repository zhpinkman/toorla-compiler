package toorla.nameAnalyzer.compileErrorException;

public class LocalVarRedefinitionException extends CompileErrorException {
    String varName;

    public LocalVarRedefinitionException(String name, int atLine, int atColumn) {
        super(atLine, atColumn);
        varName = name;
    }

    public LocalVarRedefinitionException(String name) {
        varName = name;
    }

    @Override
    public String toString() {
        return String.format("Error:Line:%d:Redefinition of Local Variable %s in current scope", atLine, varName);
    }
}