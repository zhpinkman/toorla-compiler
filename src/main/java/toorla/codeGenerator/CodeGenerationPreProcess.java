package toorla.codeGenerator;

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
import toorla.symbolTable.SymbolTable;
import toorla.visitor.Visitor;

public class CodeGenerationPreProcess extends Visitor<Void> {

    static MethodDeclaration current_method;


    public Void visit(Plus plusExpr) {
        return null;
    }

    public Void visit(Minus minusExpr) {
        return null;
    }

    public Void visit(Times timesExpr) {
        return null;
    }

    public Void visit(Division divExpr) {
        return null;
    }

    public Void visit(Modulo moduloExpr) {
        return null;
    }

    public Void visit(Equals equalsExpr) {
        return null;
    }

    public Void visit(GreaterThan gtExpr) {
        return null;
    }

    public Void visit(LessThan lessThanExpr) {
        return null;
    }

    public Void visit(And andExpr) {
        return null;
    }

    public Void visit(Or orExpr) {
        return null;
    }

    public Void visit(Neg negExpr) {
        return null;
    }

    public Void visit(Not notExpr) {
        return null;
    }

    public Void visit(MethodCall methodCall) {
        return null;
    }

    public Void visit(Identifier identifier) {
        return null;
    }

    public Void visit(Self self) {
        return null;
    }

    public Void visit(IntValue intValue) {
        return null;
    }

    public Void visit(NewArray newArray) {
        return null;
    }

    public Void visit(BoolValue booleanValue) {
        return null;
    }

    public Void visit(StringValue stringValue) {
        return null;
    }

    public Void visit(NewClassInstance newClassInstance) {
        return null;
    }

    public Void visit(FieldCall fieldCall) {
        return null;
    }

    public Void visit(ArrayCall arrayCall) {
        return null;
    }

    public Void visit(NotEquals notEquals) {
        return null;
    }

    // Statement
    public Void visit(PrintLine printStat) {
        return null;
    }

    public Void visit(Assign assignStat) {
        return null;
    }

    public Void visit(Block block) {
        for (Statement statement : block.body){
            statement.accept(this);
        }
        return null;
    }

    public Void visit(Conditional conditional) {
        conditional.getThenStatement().accept(this);
        conditional.getElseStatement().accept(this);
        return null;
    }

    public Void visit(While whileStat) {
        whileStat.body.accept(this);
        return null;
    }

    public Void visit(Return returnStat) {
        return null;
    }

    public Void visit(Break breakStat) {
        return null;
    }

    public Void visit(Continue continueStat) {
        return null;
    }

    public Void visit(Skip skip) {
        return null;
    }

    public Void visit(LocalVarDef localVarDef) {
        current_method.incLocals();
        return null;
    }

    public Void visit(IncStatement incStatement) {
        return null;
    }

    public Void visit(DecStatement decStatement) {
        return null;
    }

    // declarations
    public Void visit(ClassDeclaration classDeclaration) {
        for (ClassMemberDeclaration classMemberDeclaration : classDeclaration.getClassMembers()){
            classMemberDeclaration.accept(this);
        }
        return null;
    }

    public Void visit(EntryClassDeclaration entryClassDeclaration) {
        for (ClassMemberDeclaration classMemberDeclaration : entryClassDeclaration.getClassMembers()){
            classMemberDeclaration.accept(this);
        }
        return null;
    }

    public Void visit(FieldDeclaration fieldDeclaration) {
        return null;
    }

    public Void visit(ParameterDeclaration parameterDeclaration) {
        current_method.incLocals();
        return null;
    }

    public Void visit(MethodDeclaration methodDeclaration) {
        current_method = methodDeclaration;
        for (ParameterDeclaration parameterDeclaration : methodDeclaration.getArgs()){
            parameterDeclaration.accept(this);
        }
        for (Statement statement : methodDeclaration.getBody()){
            statement.accept(this);
        }
        return null;
    }

    public Void visit(LocalVarsDefinitions localVarsDefinitions) {
        for (LocalVarDef localVarDef : localVarsDefinitions.getVarDefinitions()){
            localVarDef.accept(this);
        }
        return null;
    }

    public Void visit(Program program) {
        SymbolTable.pushFromQueue();
        for (ClassDeclaration classDeclaration : program.getClasses()){
            classDeclaration.accept(this);
        }
        SymbolTable.pop();
        return null;
    }

}
