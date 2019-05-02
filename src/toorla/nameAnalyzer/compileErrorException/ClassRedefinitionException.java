package toorla.nameAnalyzer.compileErrorException;

import toorla.ast.declaration.classDecs.ClassDeclaration;
import toorla.ast.expression.Identifier;
import toorla.symbolTable.SymbolTable;
import toorla.symbolTable.exceptions.ItemAlreadyExistsException;
import toorla.symbolTable.symbolTableItem.ClassSymbolTableItem;

public class ClassRedefinitionException extends CompileErrorException {
    private ClassDeclaration classDeclaration;
    private int seenClassesNum;
    private String oldName;

    public ClassRedefinitionException(ClassDeclaration classDeclaration , int seenClassesNum ) {
        super(classDeclaration.getName().line , classDeclaration.getName().col);
        this.classDeclaration = classDeclaration;
        this.seenClassesNum = seenClassesNum;
        this.oldName = classDeclaration.getName().getName();
    }


    public void handle()
    {
        String newClassName = "$"
                + seenClassesNum + oldName;
        ClassSymbolTableItem thisClass = new ClassSymbolTableItem( newClassName );
        thisClass.setSymbolTable( SymbolTable.top() );
        thisClass.setParentSymbolTable(SymbolTable.top().getPreSymbolTable());
        Identifier newClassIdentifier = new Identifier( newClassName );
        newClassIdentifier.line = atLine;
        newClassIdentifier.col = atColumn;
        try
        {
            SymbolTable.root.put( thisClass );
            classDeclaration.setName( newClassIdentifier );
        }
        catch( ItemAlreadyExistsException itemAlreadyExists ) {
            itemAlreadyExists.printStackTrace();
        }
    }
    @Override
    public String toString() {
        return String.format("Error:Line:%d:Redefinition of Class %s", atLine, oldName);
    }
}