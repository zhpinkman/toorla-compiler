package toorla.typeChecking;

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
import toorla.nameAnalyzer.ClassParentshipExtractorPass;
import toorla.symbolTable.SymbolTable;
import toorla.symbolTable.symbolTableItem.SymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.FieldSymbolTableItem;
import toorla.typeChecking.typeCheckExceptions.*;
import toorla.types.Type;
import toorla.types.singleType.BoolType;
import toorla.types.singleType.IntType;
import toorla.types.singleType.StringType;
import toorla.types.singleType.VoidType;
import toorla.visitor.Visitor;
import toorla.utilities.graph.Graph;

public class TypeChecking implements Visitor<Type> {
    private Program program;
    private Graph<String> classHierarchy;
    private static int loop_depth;
    private String INT_TYPE = "(IntType)";
    private String STR_TYPE = "(StringType)";
    private int var_index = 0;

    public TypeChecking(Program p){
        program = p;
        loop_depth = 0;
    }

    public void check(){
        ClassParentshipExtractorPass classParentshipExtractorPass = new ClassParentshipExtractorPass();
        classParentshipExtractorPass.analyze( program );
        classHierarchy = classParentshipExtractorPass.getResult();
        this.visit(program);

    }

    @Override
    public Type visit(PrintLine printStat) {
        Type expr_type = printStat.getArg().accept(this);
        try{
            String str = expr_type.toString();
            if (!str.equals(INT_TYPE)&& !str.equals(STR_TYPE) && !str.equals("(ArrayType,IntType)"))
                throw new PrintArgException(printStat.line, printStat.col);
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public Type visit(Assign assignStat) {
        try	{
            if (!assignStat.getLvalue().lvalue_check(new SymbolTable()))
                throw new LvalueAssignability(assignStat.line, assignStat.col);
        }
        catch(TypeCheckException exception){
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public Type visit(Block block) {
        SymbolTable.pushFromQueue();
        for (Statement statement: block.body){
            statement.accept(this);
        }
        SymbolTable.pop();

        return new VoidType();
    }

    @Override
    public Type visit(Conditional conditional) {
        Type cond = conditional.getCondition().accept(this);
        try {
            if (!cond.toString().equals("(BoolType)")) {
                throw new InvalidLoopCondition(conditional.line, conditional.col, conditional.toString());
            }
        }catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        SymbolTable.pushFromQueue();
        conditional.getThenStatement().accept(this);
        SymbolTable.pop();
        SymbolTable.pushFromQueue();
        conditional.getElseStatement().accept(this);
        SymbolTable.pop();
        return null;
    }

    @Override
    public Type visit(While whileStat) {
        SymbolTable.pushFromQueue();
        loop_depth ++;
        Type expr_type = whileStat.expr.accept(this);
        Type body_type = whileStat.body.accept(this);

        try	{
            if (!expr_type.toString().equals("(BoolType)"))
                throw new InvalidLoopCondition(whileStat.line, whileStat.col, whileStat.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        loop_depth --;
        SymbolTable.pop();
        return body_type;
    }

    @Override
    public Type visit(Return returnStat) {
        return null;
    }

    @Override
    public Type visit(Break breakStat) {
        try{
            if (loop_depth == 0)
                throw new IllegalLoopStatementActions(breakStat.line, breakStat.col, breakStat.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public Type visit(Continue continueStat) {
        try{
            if (loop_depth == 0)
                throw new IllegalLoopStatementActions(continueStat.line, continueStat.col, continueStat.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public Type visit(Skip skip) {
        return new VoidType();
    }

    @Override
    public Type visit(LocalVarDef localVarDef) {
        var_index++;
        return new VoidType();
    }

    @Override
    public Type visit(IncStatement incStatement) {
        try {
            if (!incStatement.getOperand().lvalue_check(new SymbolTable()))
                throw new InvalidIncDecOperand(incStatement.line, incStatement.col, incStatement.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public Type visit(DecStatement decStatement) {
        try {
            if (!decStatement.getOperand().lvalue_check(new SymbolTable()))
                throw new InvalidIncDecOperand(decStatement.line, decStatement.col, decStatement.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public Type visit(Plus plusExpr) {

        Type first_operand_type = plusExpr.getLhs().accept(this);
        Type second_operand_type = plusExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(INT_TYPE)  || !second_operand_type.toString().equals(INT_TYPE))
                throw new InvalidOperationOperands(plusExpr.line, plusExpr.col, plusExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new IntType();
    }

    @Override
    public Type visit(Minus minusExpr) {

        Type first_operand_type = minusExpr.getLhs().accept(this);
        Type second_operand_type = minusExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(INT_TYPE) || !second_operand_type.toString().equals(INT_TYPE))
                throw new InvalidOperationOperands(minusExpr.line, minusExpr.col,minusExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new IntType();
    }

    @Override
    public Type visit(Times timesExpr) {
        Type first_operand_type = timesExpr.getLhs().accept(this);
        Type second_operand_type = timesExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(INT_TYPE) || !second_operand_type.toString().equals(INT_TYPE))
                throw new InvalidOperationOperands(timesExpr.line, timesExpr.col, timesExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new IntType();
    }

    @Override
    public Type visit(Division divExpr) {
        Type first_operand_type = divExpr.getLhs().accept(this);
        Type second_operand_type = divExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(INT_TYPE) || !second_operand_type.toString().equals(INT_TYPE))
                throw new InvalidOperationOperands(divExpr.line, divExpr.col, divExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new IntType();
    }

    @Override
    public Type visit(Modulo moduloExpr) {

        Type first_operand_type = moduloExpr.getLhs().accept(this);
        Type second_operand_type = moduloExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(INT_TYPE) || !second_operand_type.toString().equals(INT_TYPE))
                throw new InvalidOperationOperands(moduloExpr.line, moduloExpr.col, moduloExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new IntType();
    }

    @Override
    public Type visit(Equals equalsExpr) {
        Type first_operand_type =  equalsExpr.getLhs().accept(this);
        Type second_operand_type =  equalsExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(second_operand_type.toString()) )
                throw new InvalidOperationOperands( equalsExpr.line,  equalsExpr.col, equalsExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new BoolType();
    }

    @Override
    public Type visit(GreaterThan gtExpr) {
        Type first_operand_type = gtExpr.getLhs().accept(this);
        Type second_operand_type = gtExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(INT_TYPE) || !second_operand_type.toString().equals(INT_TYPE))
                throw new InvalidOperationOperands(gtExpr.line, gtExpr.col, gtExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new BoolType();
    }

    @Override
    public Type visit(LessThan lessThanExpr) {
        Type first_operand_type = lessThanExpr.getLhs().accept(this);
        Type second_operand_type = lessThanExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(INT_TYPE) || !second_operand_type.toString().equals(INT_TYPE))
                throw new InvalidOperationOperands(lessThanExpr.line, lessThanExpr.col, lessThanExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new BoolType();

    }

    @Override
    public Type visit(And andExpr) {
        Type first_operand_type = andExpr.getLhs().accept(this);
        Type second_operand_type = andExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals("(BoolType)") || !second_operand_type.toString().equals("(BoolType)"))
                throw new InvalidOperationOperands(andExpr.line, andExpr.col, andExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new BoolType();
    }

    @Override
    public Type visit(Or orExpr) {

        Type first_operand_type = orExpr.getLhs().accept(this);
        Type second_operand_type = orExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals("(BoolType)") || !second_operand_type.toString().equals("(BoolType)"))
                throw new InvalidOperationOperands(orExpr.line, orExpr.col, orExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new BoolType();
    }

    @Override
    public Type visit(Neg negExpr) {
        Type type = negExpr.getExpr().accept(this);
        try{
            if (!type.toString().equals(INT_TYPE))
                throw new InvalidOperationOperands(negExpr.line, negExpr.col, negExpr.toString());
        }
        catch(TypeCheckException exception){
            exception.emit_error_message();
        }
        return type;
    }

    @Override
    public Type visit(Not notExpr) {
        Type type = notExpr.getExpr().accept(this);
        try{
            if (!type.toString().equals("(BoolType)"))
                throw new InvalidOperationOperands(notExpr.line, notExpr.col, notExpr.toString());
        }
        catch(TypeCheckException exception){
            exception.emit_error_message();
        }
        return type;
    }

    @Override
    public Type visit(MethodCall methodCall) {
        Type instance_type = methodCall.getInstance().accept(this);

        return null;
    }

    @Override
    public Type visit(Identifier identifier) {
        SymbolTable s = new SymbolTable().top();

        try {
            SymbolTableItem var_item = SymbolTable.top().get("var_"+identifier.getName());
            System.out.println(var_index);

        }catch (Exception e){
            System.out.println("bbb");
        }


        return null;
    }

    @Override
    public Type visit(Self self) {
        return null;
    }

    @Override
    public Type visit(IntValue intValue) {
        return new IntType();
    }

    @Override
    public Type visit(NewArray newArray) {
        return null;
    }

    @Override
    public Type visit(BoolValue booleanValue) {
        return new BoolType();
    }

    @Override
    public Type visit(StringValue stringValue) {
        return new StringType();
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

        Type first_operand_type = notEquals.getLhs().accept(this);
        Type second_operand_type = notEquals.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(second_operand_type.toString()))
                throw new InvalidOperationOperands(notEquals.line, notEquals.col, notEquals.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new BoolType();
    }

    @Override
    public Type visit(ClassDeclaration classDeclaration) {
        SymbolTable.pushFromQueue();
        for (ClassMemberDeclaration classMemberDeclaration: classDeclaration.getClassMembers()){
            classMemberDeclaration.accept(this);
        }
        SymbolTable.pop();
        return new VoidType();
    }

    @Override
    public Type visit(EntryClassDeclaration entryClassDeclaration) {
        SymbolTable.pushFromQueue();
        for (ClassMemberDeclaration classMemberDeclaration: entryClassDeclaration.getClassMembers()){
            classMemberDeclaration.accept(this);
        }
        SymbolTable.pop();
        return new VoidType();
    }

    @Override
    public Type visit(FieldDeclaration fieldDeclaration) {
        String type_name = "";
        String hard_type = fieldDeclaration.getType().toString();
        try {
            if (hard_type.equals(INT_TYPE) || hard_type.equals("(BoolType)") || hard_type.equals(STR_TYPE))
                return new VoidType();
            int index_of_name = hard_type.indexOf(',');
            type_name = hard_type.substring(index_of_name + 1, hard_type.length() - 1);
            SymbolTable.top().get("class_" + type_name);
        }
        catch (Exception exception){
            System.out.println("Error:Line:" + fieldDeclaration.line + ":" + "There is no type with name " + type_name);
        }
        return null;
    }

    @Override
    public Type visit(ParameterDeclaration parameterDeclaration) {
        return null;
    }

    @Override
    public Type visit(MethodDeclaration methodDeclaration) {
        var_index = 0;
        String type_name = methodDeclaration.getReturnType().toString();
        try {
            if (!type_name.equals(INT_TYPE) && !type_name.equals("(BoolType)") && !type_name.equals(STR_TYPE)) {
                int index_of_name = type_name.indexOf(',');
                type_name = type_name.substring(index_of_name + 1, type_name.length() - 1);
                SymbolTable.top().get("class_" + type_name);
            }
        }
        catch (Exception exception){
            System.out.println("Error:Line:" + methodDeclaration.line + ":" + "There is no type with name " + type_name);
        }
        SymbolTable.pushFromQueue();
        for (Statement statement: methodDeclaration.getBody()){
            statement.accept(this);
        }
        SymbolTable.pop();

        return new VoidType();
    }

    @Override
    public Type visit(LocalVarsDefinitions localVarsDefinitions) {
        for (LocalVarDef lvd:
             localVarsDefinitions.getVarDefinitions()) {
            lvd.accept(this);
        }
        return null;
    }

    @Override
    public Type visit(Program program) {
        SymbolTable.pushFromQueue();
        for (ClassDeclaration classDeclaration : program.getClasses()){
            classDeclaration.accept(this);
        }
        SymbolTable.pop();
        return new VoidType();
    }

}
