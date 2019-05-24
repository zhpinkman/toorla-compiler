package toorla.nameAnalyzer;

import toorla.ast.Program;
import toorla.ast.declaration.classDecs.ClassDeclaration;
import toorla.ast.declaration.classDecs.EntryClassDeclaration;
import toorla.ast.declaration.classDecs.classMembersDecs.MethodDeclaration;
import toorla.ast.declaration.localVarDecs.ParameterDeclaration;
import toorla.ast.statement.Block;
import toorla.ast.statement.Conditional;
import toorla.ast.statement.While;
import toorla.ast.statement.localVarStats.LocalVarDef;
import toorla.symbolTable.SymbolTable;
import toorla.symbolTable.exceptions.ItemNotFoundException;
import toorla.symbolTable.symbolTableItem.varItems.LocalVariableSymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.VarSymbolTableItem;
import toorla.visitor.TreePrinter;


public class NameTreePrinter extends TreePrinter {
    @Override
    public Void visit(Program program) {
        SymbolTable.pushFromQueue();
        super.visit( program );
        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit( ClassDeclaration classDeclaration )
    {
        SymbolTable.pushFromQueue();
        super.visit( classDeclaration );
        SymbolTable.pop();
        return null;
    }
    @Override
    public Void visit( EntryClassDeclaration entryClassDeclaration )
    {
        SymbolTable.pushFromQueue();
        super.visit( entryClassDeclaration );
        SymbolTable.pop();
        return null;
    }
    @Override
    public Void visit( MethodDeclaration methodDeclaration )
    {
        SymbolTable.pushFromQueue();
        super.visit( methodDeclaration );
        SymbolTable.pop();
        return null;
    }
    @Override
    public Void visit(Block block )
    {
        SymbolTable.pushFromQueue();
        super.visit( block );
        SymbolTable.pop();
        return null;
    }
    @Override
    public Void visit(Conditional conditionalStatement )
    {
        System.out.print("(if ");
        conditionalStatement.getCondition().accept(this);
        System.out.print(" ");
        SymbolTable.pushFromQueue();
        conditionalStatement.getThenStatement().accept(this);
        SymbolTable.pop();
        System.out.print(" ");
        SymbolTable.pushFromQueue();
        conditionalStatement.getElseStatement().accept(this);
        SymbolTable.pop();
        System.out.println(")");
        return null;
    }
    @Override
    public Void visit( While whileStatement )
    {
        System.out.print("(while ");
        whileStatement.expr.accept(this);
        System.out.print(" ");
        SymbolTable.pushFromQueue();
        whileStatement.body.accept(this);
        SymbolTable.pop();
        System.out.println(")");
        return null;
    }
    @Override
    public Void visit(ParameterDeclaration parameterDeclaration )
    {
        String parameterName = parameterDeclaration.getIdentifier().getName();
        int parameterIndex = getLocalVarIndex( parameterName );
        System.out.print("( ");
        System.out.print( "(Identifier," + parameterName + "_" + parameterIndex + ")" );
        System.out.print(" : ");
        System.out.print(parameterDeclaration.getType());
        System.out.print(")");
        return null;
    }
    @Override
    public Void visit( LocalVarDef localVarDef )
    {
        String localVarName = localVarDef.getLocalVarName().getName();
        int localVarIndex = getLocalVarIndex( localVarName );
        System.out.print("(var ");
        System.out.print( "(Identifier," + localVarName + "_" + localVarIndex + ")" );
        System.out.print(" ");
        localVarDef.getInitialValue().accept(this);
        System.out.println(")");
        return null;
    }
    private int getLocalVarIndex( String name )
    {
        int localVarIndex = 0;
        try {
            LocalVariableSymbolTableItem parameterSymbolTableItem =
                    (LocalVariableSymbolTableItem) ( SymbolTable.top().get(VarSymbolTableItem.var_modifier
                            + name ) );
            localVarIndex = parameterSymbolTableItem.getIndex();
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
        }
        return localVarIndex;
    }

}
