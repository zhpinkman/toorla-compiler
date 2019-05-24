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
import toorla.nameAnalyzer.compileErrorException.FieldRedefinitionException;
import toorla.nameAnalyzer.compileErrorException.MethodRedefinitionException;
import toorla.symbolTable.SymbolTable;
import toorla.symbolTable.exceptions.ItemNotFoundException;
import toorla.symbolTable.symbolTableItem.MethodSymbolTableItem;
import toorla.symbolTable.symbolTableItem.SymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.VarSymbolTableItem;
import toorla.visitor.Visitor;

public class NameCheckingPass implements Visitor<Void>, INameAnalyzingPass<Void> {

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
        SymbolTable.pushFromQueue();
        for (Statement stmt : block.body)
            stmt.accept(this);
        SymbolTable.pop();
        return null;
    }

    @Override
    public Void visit(Conditional conditional) {
        SymbolTable.pushFromQueue();
        conditional.getThenStatement().accept(this);
        SymbolTable.pop();
        SymbolTable.pushFromQueue();
        conditional.getElseStatement().accept(this);
        SymbolTable.pop();
        return null;

    }

    @Override
    public Void visit(While whileStat) {
        SymbolTable.pushFromQueue();
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
        SymbolTable.pushFromQueue();
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
        if (fieldDeclaration.relatedErrors.size() == 0)
            try {
                SymbolTableItem sameFieldInParents = SymbolTable.top()
                        .getInParentScopes(VarSymbolTableItem.var_modifier + fieldDeclaration.getIdentifier().getName());
                FieldRedefinitionException e = new FieldRedefinitionException(
                        fieldDeclaration.getIdentifier().getName(), fieldDeclaration.line, fieldDeclaration.col);
                fieldDeclaration.relatedErrors.add(e);
            } catch (ItemNotFoundException ignored) {
            }
        return null;
    }

    @Override
    public Void visit(ParameterDeclaration parameterDeclaration) {
        return null;
    }

    @Override
    public Void visit(MethodDeclaration methodDeclaration) {
        if( methodDeclaration.relatedErrors.size() == 0 )
            try {
                SymbolTableItem sameMethodInParents = SymbolTable.top()
                        .getInParentScopes(MethodSymbolTableItem.methodModifier + methodDeclaration.getName().getName());
                MethodRedefinitionException e = new MethodRedefinitionException(methodDeclaration.getName().getName(),
                        methodDeclaration.getName().line, methodDeclaration.getName().col);
                methodDeclaration.relatedErrors.add(e);
            } catch (ItemNotFoundException ignored) {
            }
        SymbolTable.pushFromQueue();
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
        SymbolTable.pushFromQueue();
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
