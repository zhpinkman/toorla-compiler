package toorla.nameAnalyzer;

import toorla.ast.Program;
import toorla.ast.declaration.classDecs.ClassDeclaration;
import toorla.ast.declaration.classDecs.EntryClassDeclaration;
import toorla.ast.declaration.classDecs.classMembersDecs.ClassMemberDeclaration;
import toorla.ast.declaration.classDecs.classMembersDecs.FieldDeclaration;
import toorla.ast.declaration.classDecs.classMembersDecs.MethodDeclaration;
import toorla.ast.declaration.localVarDecs.ParameterDeclaration;
import toorla.ast.expression.*;
import toorla.ast.expression.binaryExpression.*;
import toorla.ast.expression.unaryExpression.Neg;
import toorla.ast.expression.unaryExpression.Not;
import toorla.ast.expression.value.BoolValue;
import toorla.ast.expression.value.IntValue;
import toorla.ast.expression.value.StringValue;
import toorla.ast.statement.*;
import toorla.ast.statement.localVarStats.LocalVarDef;
import toorla.ast.statement.localVarStats.LocalVarsDefinitions;
import toorla.ast.statement.returnStatement.Return;
import toorla.nameAnalyzer.compileErrorException.*;
import toorla.symbolTable.SymbolTable;
import toorla.symbolTable.exceptions.ItemAlreadyExistsException;
import toorla.symbolTable.symbolTableItem.ClassSymbolTableItem;
import toorla.symbolTable.symbolTableItem.MethodSymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.FieldSymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.LocalVariableSymbolTableItem;
import toorla.types.Type;
import toorla.visitor.Visitor;

import java.util.ArrayList;

public class NameCollectionPass implements Visitor<Void>, INameAnalyzingPass<Void> {
    private int newLocalVarIndex;// refreshed in start of every method
    private int classCounter = 0;

    @Override
    public Void visit(PrintLine printStat) {
        return null;
    }

    @Override
    public Void visit(Assign assignStat) {
        return null;
    }

    @Override
    public Void visit(Block block) {
        SymbolTable.push( new SymbolTable( SymbolTable.top() ) );
        for (Statement stmt : block.body)
            stmt.accept(this);
        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit(Conditional conditional) {
        SymbolTable.push( new SymbolTable( SymbolTable.top() ));
        conditional.getThenStatement().accept(this);
        SymbolTable.pop();
        SymbolTable.push(new SymbolTable(SymbolTable.top()));
        conditional.getElseStatement().accept(this);
        SymbolTable.pop();
        return null;

    }

    @Override
    public Void visit(While whileStat) {
        SymbolTable.push(new SymbolTable(SymbolTable.top()));
        whileStat.body.accept(this);
        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit(Return returnStat) {
        return null;
    }

    @Override
    public Void visit(Break breakStat) {

        return null;
    }

    @Override
    public Void visit(Continue continueStat) {
        return null;
    }

    @Override
    public Void visit(Skip skip) {
        return null;
    }

    @Override
    public Void visit(LocalVarDef localVarDef) {
        try {
            SymbolTable.top()
                    .put(new LocalVariableSymbolTableItem(localVarDef.getLocalVarName().getName(), newLocalVarIndex));
        } catch (ItemAlreadyExistsException e) {
            LocalVarRedefinitionException ee = new LocalVarRedefinitionException(
                    localVarDef.getLocalVarName().getName(), localVarDef.line, localVarDef.col);
            localVarDef.relatedErrors.add(ee);
        }
        newLocalVarIndex++;
        return null;
    }

    @Override
    public Void visit(IncStatement incStatement) {
        return null;
    }

    @Override
    public Void visit(DecStatement decStatement) {
        return null;
    }

    @Override
    public Void visit(Plus plusExpr) {
        return null;
    }

    @Override
    public Void visit(Minus minusExpr) {
        return null;
    }

    @Override
    public Void visit(Times timesExpr) {
        return null;
    }

    @Override
    public Void visit(Division divExpr) {
        return null;
    }

    @Override
    public Void visit(Modulo moduloExpr) {
        return null;
    }

    @Override
    public Void visit(Equals equalsExpr) {
        return null;
    }

    @Override
    public Void visit(GreaterThan gtExpr) {
        return null;
    }

    @Override
    public Void visit(LessThan lessThanExpr) {
        return null;
    }

    @Override
    public Void visit(And andExpr) {
        return null;
    }

    @Override
    public Void visit(Or orExpr) {
        return null;
    }

    @Override
    public Void visit(Neg negExpr) {
        return null;
    }

    @Override
    public Void visit(Not notExpr) {
        return null;
    }

    @Override
    public Void visit(MethodCall methodCall) {
        return null;
    }

    @Override
    public Void visit(Identifier identifier) {
        return null;
    }

    @Override
    public Void visit(Self self) {
        return null;
    }

    @Override
    public Void visit(IntValue intValue) {
        return null;
    }

    @Override
    public Void visit(NewArray newArray) {
        return null;
    }

    @Override
    public Void visit(BoolValue booleanValue) {
        return null;
    }

    @Override
    public Void visit(StringValue stringValue) {
        return null;
    }

    @Override
    public Void visit(NewClassInstance newClassInstance) {
        return null;
    }

    @Override
    public Void visit(FieldCall fieldCall) {
        return null;
    }

    @Override
    public Void visit(ArrayCall arrayCall) {
        return null;
    }

    @Override
    public Void visit(NotEquals notEquals) {
        return null;
    }

    @Override
    public Void visit(ClassDeclaration classDeclaration) {
        classCounter++;
        ClassSymbolTableItem thisClass = new ClassSymbolTableItem(classDeclaration.getName().getName());
        SymbolTable.push(new SymbolTable( SymbolTable.top() ) );
        try {
            thisClass.setSymbolTable(SymbolTable.top());
            thisClass.setParentSymbolTable(SymbolTable.top().getPreSymbolTable());
            SymbolTable.root.put(thisClass);
        } catch (ItemAlreadyExistsException e) {
            ClassRedefinitionException ee = new ClassRedefinitionException( classDeclaration , classCounter);
            ee.handle();
            classDeclaration.relatedErrors.add(ee);
        }
        for (ClassMemberDeclaration cmd : classDeclaration.getClassMembers())
            cmd.accept(this);
        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit(EntryClassDeclaration entryClassDeclaration) {
        this.visit((ClassDeclaration) entryClassDeclaration);
        return null;
    }

    @Override
    public Void visit(FieldDeclaration fieldDeclaration) {
        if (!fieldDeclaration.getIdentifier().getName().equals("length")) {
            try {
                SymbolTable.top().put(new FieldSymbolTableItem(fieldDeclaration.getIdentifier().getName(),
                        fieldDeclaration.getAccessModifier(), fieldDeclaration.getType()));
            } catch (ItemAlreadyExistsException e) {
                FieldRedefinitionException ee = new FieldRedefinitionException(
                        fieldDeclaration.getIdentifier().getName(), fieldDeclaration.line, fieldDeclaration.col);
                fieldDeclaration.relatedErrors.add(ee);
            }
        } else {
            FieldNamedLengthDeclarationException e = new FieldNamedLengthDeclarationException(
                    fieldDeclaration.getIdentifier().line , fieldDeclaration.getIdentifier().col
            );
            fieldDeclaration.relatedErrors.add(e);
        }
        return null;
    }

    @Override
    public Void visit(ParameterDeclaration parameterDeclaration) {
        try {
            SymbolTable.top().put(
                    new LocalVariableSymbolTableItem(parameterDeclaration.getIdentifier().getName(), newLocalVarIndex));
        } catch (ItemAlreadyExistsException e) {
            LocalVarRedefinitionException ee = new LocalVarRedefinitionException(
                    parameterDeclaration.getIdentifier().getName(), parameterDeclaration.line,
                    parameterDeclaration.col);
            parameterDeclaration.relatedErrors.add(ee);
        }
        newLocalVarIndex++;
        return null;
    }

    @Override
    public Void visit(MethodDeclaration methodDeclaration) {
        newLocalVarIndex = 1;
        try {
            ArrayList<Type> argumentsTypes = new ArrayList<>();
            for ( ParameterDeclaration arg : methodDeclaration.getArgs() )
                argumentsTypes.add( arg.getType() );
            SymbolTable.top().put(
                    new MethodSymbolTableItem(
                            methodDeclaration.getName().getName(),
                            methodDeclaration.getReturnType(),
                            argumentsTypes , methodDeclaration.getAccessModifier()) );
        } catch (ItemAlreadyExistsException e) {
            MethodRedefinitionException ee = new MethodRedefinitionException(methodDeclaration.getName().getName(),
                    methodDeclaration.getName().line, methodDeclaration.getName().col);
            methodDeclaration.relatedErrors.add(ee);
        }
        SymbolTable.push( new SymbolTable( SymbolTable.top() ) );
        for (ParameterDeclaration pd : methodDeclaration.getArgs())
            pd.accept(this);
        for (Statement stmt : methodDeclaration.getBody())
            stmt.accept(this);
        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit(LocalVarsDefinitions localVarsDefinitions) {
        for (LocalVarDef lvd : localVarsDefinitions.getVarDefinitions()) {
            lvd.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(Program program) {
        for (ClassDeclaration cd : program.getClasses()) {
            cd.accept(this);
        }
        SymbolTable.pop();
        return null;
    }

    @Override
    public void analyze(Program program) {
        this.visit(program);
    }

    @Override
    public Void getResult() {
        return null;
    }
}
