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
import toorla.symbolTable.symbolTableItem.ClassSymbolTableItem;
import toorla.symbolTable.symbolTableItem.MethodSymbolTableItem;
import toorla.symbolTable.symbolTableItem.SymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.FieldSymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.LocalVariableSymbolTableItem;
import toorla.typeChecking.typeCheckExceptions.*;
import toorla.types.Type;
import toorla.types.singleType.*;
import toorla.visitor.Visitor;
import toorla.utilities.graph.Graph;

import javax.sound.midi.Soundbank;
import javax.sound.midi.SysexMessage;
import java.sql.SQLOutput;
import java.util.List;

public class TypeChecking implements Visitor<Type> {
    private Program program;
    private Graph<String> classHierarchy;
    private static int loop_depth;
    private String INT_TYPE = "(IntType)";
    private String INT_ARRAY = "(ArrayType,IntType)";
    private String STR_TYPE = "(StringType)";
    private String BOOL_TYPE = "(BoolType)";
    private String UNDEFINED_TYPE = "(UndefinedType)";
    private String method_return_type = "";
    private String VAR_PREFIX = "var_";
    private String CLASS_PREFIX = "class_";
    private String METHOD_PREFIX = "method_";
    private String FIELD_PREFIX = "field_";
    private int var_index = 0;
    private static ClassDeclaration current_class;
    private String public_access = "(ACCESS_MODIFIER_PUBLIC)";
    private String private_access = "(ACCESS_MODIFIER_PRIVATE)";

    private static final String VARIABLE_CALLING = "VAR_CALLING_ID";
    private static final String FIELD_CALLING = "FIELD_CALLING_ID";
    private static final String CLASS_CALLING = "CLASS_CALLING_ID";
    private static final String METHOD_CALLING = "METHOD_CALLING_ID";
    private static final String ARRAY_CALLING = "ARRAY_CALLING_ID";
    private String who_is_calling_identifier = VARIABLE_CALLING;

    private Boolean is_subtype(String lhs, String rhs){ //rhs is subtype of lhs // lhs is father
        try {
//            System.out.println(lhs);
//            System.out.println(rhs);
            if (classHierarchy.getParentsOfNode(rhs).contains(lhs) || lhs.equals(rhs)){
//                System.out.println(true);
                return true;
            }
            else{
//                System.out.println(false);
                return false;
            }
        }
        catch (Exception exception){
            System.out.println("wow");
        }
        return null;
    }

    private Boolean is_not_primitive(Type type){
        if (type.toString() != INT_TYPE && type.toString() != STR_TYPE && type.toString() != BOOL_TYPE
        && !type.toString().startsWith("(ArrayType,"))
            return true;
        else
            return false;
    }

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
            if (!str.equals(INT_TYPE)&& !str.equals(STR_TYPE) && !str.equals(INT_ARRAY))
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
            if (!assignStat.getLvalue().lvalue_check(SymbolTable.top()))
                throw new LvalueAssignability(assignStat.line, assignStat.col);
        }
        catch(TypeCheckException exception){
            exception.emit_error_message();
        }
        Type lhs = assignStat.getLvalue().accept(this);
//        System.out.println(lhs);
        Type rhs = assignStat.getRvalue().accept(this);
//        System.out.println(rhs);
        if (is_not_primitive(lhs) && is_not_primitive(rhs)){
            String rhs_class_name ;
            String lhs_class_name ;
            int index;
            index = lhs.toString().indexOf(',');
            lhs_class_name = lhs.toString().substring(index + 1, lhs.toString().length() - 1);
            index = rhs.toString().indexOf(',');
            rhs_class_name = rhs.toString().substring(index + 1, rhs.toString().length() - 1);
//            System.out.println(lhs_class_name);
//            System.out.println(rhs_class_name);
            try {
                if (is_subtype(lhs_class_name, rhs_class_name))
                    return new VoidType();
                else
                    throw new LvalueAssignability(assignStat.line, assignStat.col);
            }
            catch (TypeCheckException exception){
                exception.emit_error_message();
            }

        }
        else{
            try{
                if (lhs.toString() != rhs.toString()) // not sure about arrays and assigning each of them to one another
                    throw new LvalueAssignability(assignStat.line, assignStat.col);
            }
            catch (TypeCheckException exception){
                exception.emit_error_message();
            }
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
                throw new InvalidLoopCondition(conditional.getCondition().line, conditional.getCondition().col, conditional.toString());
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
        try {
//            System.out.println(returnStat.getReturnedExpr().accept(this).toString());
//            System.out.println(method_return_type);
            if (!returnStat.getReturnedExpr().accept(this).toString().equals(method_return_type))
                throw new InvalidReturnType(returnStat.line, returnStat.col);
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new VoidType();
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
        try {
//            System.out.println("1");
            LocalVariableSymbolTableItem lvst = (LocalVariableSymbolTableItem) SymbolTable.top().get(VAR_PREFIX + localVarDef.getLocalVarName().getName());
            lvst.setInital_value(localVarDef.getInitialValue().accept(this));
//            System.out.println(lvst.getInital_value());

        }
        catch (Exception exception){
//            System.out.println("testing");
        }


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
        try {
            String class_name = ((UserDefinedType) instance_type).getClassDeclaration().getName().getName();
            ClassSymbolTableItem class_symbol_table = (ClassSymbolTableItem) SymbolTable.top().get(CLASS_PREFIX + class_name);
            try {
                MethodSymbolTableItem method = (MethodSymbolTableItem) class_symbol_table.getSymbolTable().get(VAR_PREFIX + methodCall.getMethodName().getName());
                if ( method.getAccessModifier().toString().equals(public_access) || is_subtype(class_name, current_class.getName().getName()) ){
                    int i=0;
                    for (Expression arg_expr: methodCall.getArgs() ) {
                        Type arg_type = arg_expr.accept(this);
                        if(!arg_type.toString().equals(UNDEFINED_TYPE) && !arg_type.toString().equals(method.getArgumentsTypes().get(i).toString()) ){
                            throw new Exception(); // GOES TO InvalidMethodCall if args doesnt match
                        }
                        i++;
                    }
                    return method.getReturnType();  // PUBLIC or (Private and subClass)
                }else{
                    try{
                        throw new IllegalAccessToMember(methodCall.line, methodCall.col, class_name, "METHOD", method.getName());
                    }catch (IllegalAccessToMember ie){
                        ie.emit_error_message();
                    }
                }
//                System.out.println("tested");
            }catch (Exception  exception){
                try{
                    throw new InvalidMethodCall(methodCall.line, methodCall.col, class_name, methodCall.getMethodName().getName());
                }catch (InvalidMethodCall ie){
                    ie.emit_error_message();
                }
            }
        }
        catch (Exception e){
            //Error for when method call instance was not a class.
            try {
                if(!instance_type.toString().equals(UNDEFINED_TYPE) ) { // if not undefined
                    throw new InvalidOperationOperands(methodCall.line, methodCall.col, methodCall.toString());
                }
            }catch (InvalidOperationOperands io){
                io.emit_error_message();
            }
        }

        return new UndefinedType();
    }


    @Override
    public Type visit(Identifier identifier) {
//        System.out.println("here");
        SymbolTable s = new SymbolTable().top();


        try {
            LocalVariableSymbolTableItem var_item = (LocalVariableSymbolTableItem) SymbolTable.top().get("var_" + identifier.getName());
            if (var_item.getIndex() <= var_index) {
                return var_item.getInital_value();
//                System.out.println(type);
            }
//            System.out.println(var_index);
        } catch (Exception exception) {
            //
        }

        try {
            FieldSymbolTableItem field_item = (FieldSymbolTableItem) SymbolTable.top().get("var_" + identifier.getName());
            return field_item.getVarType();
        } catch (Exception exception) {
            //Field call is being handled int it's node, if there is no a.b we assume it was variable we were searching for
            //System.out.println("error about couldn't finding variable or field"); // not sure which error to emit
        }


        try {
            throw new InvalidVariableCall(identifier.line, identifier.col, identifier.getName());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }

        return new UndefinedType();
    }

    @Override
    public Type visit(Self self) {
//        System.out.println("here");
        return new UserDefinedType(current_class);
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
        try {
            ClassSymbolTableItem class_item  = (ClassSymbolTableItem) SymbolTable.top().get(CLASS_PREFIX + newClassInstance.getClassName().getName());
            return new UserDefinedType(new ClassDeclaration(newClassInstance.getClassName()));
        }
        catch (Exception exception){
            try {
                throw new InvalidClassName(newClassInstance.line, newClassInstance.col, newClassInstance.getClassName().getName());
            }
            catch (TypeCheckException exc){
                exc.emit_error_message();
            }
        }
        return new UndefinedType();
    }

    @Override
    public Type visit(FieldCall fieldCall) {
        Type instance_type = fieldCall.getInstance().accept(this);
        try {
            String class_name = ((UserDefinedType) instance_type).getClassDeclaration().getName().getName();
            ClassSymbolTableItem class_symbol_table = (ClassSymbolTableItem) SymbolTable.top().get(CLASS_PREFIX + class_name);
            try {
                FieldSymbolTableItem field = (FieldSymbolTableItem) class_symbol_table.getSymbolTable().get(VAR_PREFIX + fieldCall.getField().getName());
                if ( field.getAccessModifier().toString().equals(public_access) || is_subtype(class_name, current_class.getName().getName()) ){
                    return field.getVarType();  // PUBLIC or (Private and subClass)
                }else{
                    try{
                        throw new IllegalAccessToMember(fieldCall.line, fieldCall.col, class_name, "FIELD", field.getName());
                    }catch (IllegalAccessToMember ie){
                        ie.emit_error_message();
                    }
                }
//                System.out.println("tested");
            }catch (Exception  exception){
                try{
                    throw new InvalidFieldCall(fieldCall.line, fieldCall.col, class_name, fieldCall.getField().getName());
                }catch (InvalidFieldCall ie){
                    ie.emit_error_message();
                }
            }
        }
        catch (Exception e){
            //Error for when field call instance was not found or was not a class.
            try {
                if(!instance_type.toString().equals(UNDEFINED_TYPE) ) { // if not undefined
                    throw new InvalidOperationOperands(fieldCall.line, fieldCall.col, fieldCall.toString());
                }
            }catch (InvalidOperationOperands io){
                io.emit_error_message();
            }
        }

//        try {
//            if (fieldCall.getInstance().toString().equals("(Self)")) {
////                System.out.println("zzz");
////                System.out.println(current_class.getName().getName());
////                System.out.println(CLASS_PREFIX + current_class.getName().getName());
//                ClassSymbolTableItem class_symbol_table = (ClassSymbolTableItem) SymbolTable.top().get(CLASS_PREFIX + current_class.getName().getName());
////                System.out.println("test");
//                try {
//                    FieldSymbolTableItem field = (FieldSymbolTableItem) class_symbol_table.getSymbolTable().get(VAR_PREFIX + fieldCall.getField().getName());
////                    System.out.println(field.getVarType());
//                    return field.getVarType();
//                }
//                catch (Exception exception){}
//            }
//        }
//        catch (Exception exception){
//
//        }
        return new UndefinedType();
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
        current_class = classDeclaration;
        SymbolTable.pushFromQueue();
        for (ClassMemberDeclaration classMemberDeclaration: classDeclaration.getClassMembers()){
            classMemberDeclaration.accept(this);
        }
        SymbolTable.pop();
        return new VoidType();
    }

    @Override
    public Type visit(EntryClassDeclaration entryClassDeclaration) {
        current_class = entryClassDeclaration;
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
//        System.out.println(methodDeclaration);
        method_return_type = methodDeclaration.getReturnType().toString(); // setting return type of the method declaration

        var_index = 0;

        String type_name = methodDeclaration.getReturnType().toString();

        try {
            if (!type_name.equals(INT_TYPE) && !type_name.equals(BOOL_TYPE) && !type_name.equals(STR_TYPE)) {
                int index_of_name = type_name.indexOf(',');
                type_name = type_name.substring(index_of_name + 1, type_name.length() - 1);

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
        for (LocalVarDef lvd: localVarsDefinitions.getVarDefinitions()) {
            lvd.accept(this);
        }
        return null;
    }

    @Override
    public Type visit(Program program) {
        SymbolTable.pushFromQueue();
        for (ClassDeclaration classDeclaration : program.getClasses()){
//            System.out.println("hello");
            classDeclaration.accept(this);
        }
        SymbolTable.pop();
        return new VoidType();
    }

}
