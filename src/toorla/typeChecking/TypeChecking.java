package toorla.typeChecking;

import toorla.ast.Program;
import toorla.ast.declaration.classDecs.ClassDeclaration;
import toorla.ast.declaration.classDecs.EntryClassDeclaration;
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
import toorla.typeChecking.typeCheckExceptions.IllegalLoopStatementActions;
import toorla.types.Type;
import toorla.visitor.Visitor;

public class TypeChecking implements Visitor<Type> {

    public static int loop_depth;

    public TypeChecking(){
        loop_depth = 0;
    }

    @Override
    public Type visit(PrintLine printStat) {


        return null;
    }

    @Override
    public Type visit(Assign assignStat) {
        return null;
    }

    @Override
    public Type visit(Block block) {
        return null;
    }

    @Override
    public Type visit(Conditional conditional) {
        return null;
    }

    @Override
    public Type visit(While whileStat) {
        loop_depth ++;
        try{
            whileStat.expr.accept(this);
        }
        catch (Exception exception){
            //
        }
        try{
            whileStat.body.accept(this);
        }
        catch (Exception exception){
            //
        }
        loop_depth --;
        return null;
    }

    @Override
    public Type visit(Return returnStat) {
        return null;
    }

    @Override
    public Type visit(Break breakStat) {
        return null;
    }

    @Override
    public Type visit(Continue continueStat) {
        try{
            if (loop_depth == 0){
                throw new IllegalLoopStatementActions(continueStat.line, continueStat.col, "continue");
            }
        }
        catch(Exception exception){
            //
        }
        return null;
    }

    @Override
    public Type visit(Skip skip) {
        return null;
    }

    @Override
    public Type visit(LocalVarDef localVarDef) {
        return null;
    }

    @Override
    public Type visit(IncStatement incStatement) {
        return null;
    }

    @Override
    public Type visit(DecStatement decStatement) {
        return null;
    }

    @Override
    public Type visit(Plus plusExpr) {
        return null;
    }

    @Override
    public Type visit(Minus minusExpr) {
        return null;
    }

    @Override
    public Type visit(Times timesExpr) {
        return null;
    }

    @Override
    public Type visit(Division divExpr) {
        return null;
    }

    @Override
    public Type visit(Modulo moduloExpr) {
        return null;
    }

    @Override
    public Type visit(Equals equalsExpr) {
        return null;
    }

    @Override
    public Type visit(GreaterThan gtExpr) {
        return null;
    }

    @Override
    public Type visit(LessThan lessThanExpr) {
        return null;
    }

    @Override
    public Type visit(And andExpr) {
        return null;
    }

    @Override
    public Type visit(Or orExpr) {
        return null;
    }

    @Override
    public Type visit(Neg negExpr) {
        return null;
    }

    @Override
    public Type visit(Not notExpr) {
        return null;
    }

    @Override
    public Type visit(MethodCall methodCall) {
        return null;
    }

    @Override
    public Type visit(Identifier identifier) {
        return null;
    }

    @Override
    public Type visit(Self self) {
        return null;
    }

    @Override
    public Type visit(IntValue intValue) {
        return null;
    }

    @Override
    public Type visit(NewArray newArray) {
        return null;
    }

    @Override
    public Type visit(BoolValue booleanValue) {
        return null;
    }

    @Override
    public Type visit(StringValue stringValue) {
        return null;
    }

    @Override
    public Type visit(NewClassInstance newClassInstance) {
        return null;
    }

    @Override
    public Type visit(FieldCall fieldCall) {
        return null;
    }

    @Override
    public Type visit(ArrayCall arrayCall) {
        return null;
    }

    @Override
    public Type visit(NotEquals notEquals) {
        return null;
    }

    @Override
    public Type visit(ClassDeclaration classDeclaration) {
        return null;
    }

    @Override
    public Type visit(EntryClassDeclaration entryClassDeclaration) {
        return null;
    }

    @Override
    public Type visit(FieldDeclaration fieldDeclaration) {
        return null;
    }

    @Override
    public Type visit(ParameterDeclaration parameterDeclaration) {
        return null;
    }

    @Override
    public Type visit(MethodDeclaration methodDeclaration) {
        return null;
    }

    @Override
    public Type visit(LocalVarsDefinitions localVarsDefinitions) {
        return null;
    }

    @Override
    public Type visit(Program program) {
        return null;
    }
}
