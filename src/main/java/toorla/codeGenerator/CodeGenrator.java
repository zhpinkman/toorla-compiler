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
import toorla.symbolTable.exceptions.ItemNotFoundException;
import toorla.symbolTable.symbolTableItem.varItems.LocalVariableSymbolTableItem;
import toorla.typeChecker.ExpressionTypeExtractor;
import toorla.types.Type;
import toorla.types.arrayType.ArrayType;
import toorla.types.singleType.BoolType;
import toorla.types.singleType.IntType;
import toorla.types.singleType.StringType;
import toorla.types.singleType.UserDefinedType;
import toorla.utilities.graph.Graph;
import toorla.visitor.Visitor;

import java.io.*;
import java.util.ArrayList;

public class CodeGenrator extends Visitor<Void> {

    static String PUBLIC_ACCESS = "(ACCESS_MODIFIER_PUBLIC)";
    static String PRIVATE_ACCESS = "(ACCESS_MODIFIER_PRIVATE)";
    static String current_class = "";
    static String INTEGER_TYPE = "I";
    static String STRING_TYPE = "Ljava/lang/String;";
    static String BOOL_TYPE = "Z";
    static String ARRAY_TYPE = "[";
    static int unique_label = 0;
    static int curr_var = 0;
    static boolean is_using_self = false;
    static String class_with_main = "";
    ExpressionTypeExtractor expressionTypeExtractor;
//    public PrintWriter printWriter;
    int tabs_before;

    public String get_access_modifier(String access_modifier){
        if (access_modifier.equals(PUBLIC_ACCESS))
            return "public";
        else
            return "private";
    }

    public void append_runner_class(){
        try(FileWriter fw = new FileWriter("artifact/" + "Runner" + ".j", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.print(".class public Runner\n" +
                    ".super java/lang/Object\n" +
                    ".method public <init>()V\n" +
                    "aload_0\n" +
                    "invokespecial java/lang/Object/<init>()V\n" +
                    "return\n" +
                    ".end method\n" +
                    "\n" +
                    ".method public static main([Ljava/lang/String;)V\n" +
                    ".limit stack 1000\n" +
                    ".limit locals 100\n" +
                    "new " + class_with_main + "\n" +
                    "dup\n" +
                    "invokespecial " + class_with_main + "/<init>()V\n" +
                    "astore_1\n" +
                    "aload 1\n" +
                    "invokestatic " + class_with_main + "/main()I\n" +
                    "istore_0\n" +
                    "return\n" +
                    ".end method");
        } catch (IOException e) {
        }

    }

    public String get_type_code(Type param){
        if (param instanceof IntType)
            return  INTEGER_TYPE;
        else if (param instanceof StringType)
            return  STRING_TYPE;
        else if (param instanceof BoolType)
            return BOOL_TYPE;
        else if (param  instanceof UserDefinedType)
            return "L" + ((UserDefinedType) param).getClassDeclaration().getName().getName() + ";";
        else{
            ArrayType type = (toorla.types.arrayType.ArrayType) param;
            return  ARRAY_TYPE + get_type_code(type.getSingleType());
        }
    }

    public String get_args_code(ArrayList<ParameterDeclaration> args){
        String result = "";
        for (ParameterDeclaration parameter : args){
            result += get_type_code(parameter.getType());
        }
        return result;
    }

    public void append_limits(){
        append_command(".limit locals 10"); // TODO local variables should be counted for this part
        append_command(".limit stack 100");
    }

    public void append_default_constructor(){
        append_command(".method public <init>()V\n" +
                "       aload_0 ; push this\n" +
                "       invokespecial java/lang/Object/<init>()V ; call super\n" +
                "       return\n" +
                "   .end method");
    }

    public void create_class_file(String class_name){
        try(FileWriter fw = new FileWriter("artifact/" + class_name + ".j", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {} catch (IOException e) {
        }
    }


    public void create_Any_class(){
        try(FileWriter fw = new FileWriter("artifact/" + "Any" + ".j", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            out.print(".class public Any\n" +
                    ".super java/lang/Object\n" +
                    ".method public <init>()V\n" +
                    "aload_0\n" +
                    "invokespecial java/lang/Object/<init>()V\n" +
                    "return\n" +
                    ".end method\n");
            //
        }catch (Exception exception){}
    }

    public void create_directory(){
        File theDir = new File("artifact");
        try{
            theDir.mkdir();
            create_Any_class();
        }
        catch(SecurityException se){
        }
    }

    public CodeGenrator(Graph<String> classHierarchy){
        expressionTypeExtractor = new ExpressionTypeExtractor(classHierarchy);
        tabs_before = 0;
        create_directory();
    }

    public void append_command(String command)
    {
        try(FileWriter fw = new FileWriter("artifact/" + current_class + ".j", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            for (int i = 0;i < tabs_before; i++)
                out.print("   ");
            out.println(command);
        } catch (IOException e) {
        }
    }

    public Void visit(Plus plusExpr) {
        plusExpr.getRhs().accept(this);
        plusExpr.getLhs().accept(this);

        append_command("iadd");

        return null;
    }

    public Void visit(Minus minusExpr) {
        minusExpr.getLhs().accept(this);
        minusExpr.getRhs().accept(this);

        append_command("isub");
        return null;
    }

    public Void visit(Times timesExpr) {
        timesExpr.getLhs().accept(this);
        timesExpr.getRhs().accept(this);

        append_command("imul");
        return null;
    }

    public Void visit(Division divExpr) {
        divExpr.getLhs().accept(this);
        divExpr.getRhs().accept(this);

        append_command("idiv");
        return null;
    }

    public Void visit(Modulo moduloExpr) {

        moduloExpr.getLhs().accept(this);
        moduloExpr.getRhs().accept(this);

        append_command("irem");
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
        andExpr.getLhs().accept(this);

        append_command("ifeq " + String.valueOf(unique_label) + "_0");

        andExpr.getRhs().accept(this);

        append_command("ifeq " + String.valueOf(unique_label) + "_0");

        append_command("iconst_1");
        append_command("goto " + String.valueOf(unique_label) + "_exit");

        append_command(String.valueOf(unique_label) + "_0: " + "iconst_0");

        // TODO label for exiting the whole statement

        unique_label ++;
        return null;
    }

    public Void visit(Or orExpr) {

        orExpr.getLhs().accept(this);

        append_command("ifne " + String.valueOf(unique_label) + "_1");

        orExpr.getRhs().accept(this);

        append_command("ifeq " + String.valueOf(unique_label) + "_0");

        append_command(String.valueOf(unique_label) + "_1: " + "iconst_1");
        append_command("goto " + String.valueOf(unique_label) + "_exit");

        append_command(String.valueOf(unique_label) + "_0: " + "iconst_0");

        // TODO label for exiting the whole statement

        unique_label ++;
        return null;
    }

    public Void visit(Neg negExpr) {

        negExpr.getExpr().accept(this);
        append_command("ineg");

        return null;
    }

    public Void visit(Not notExpr) {

        notExpr.getExpr().accept(this);
        append_command("ifne " + String.valueOf(unique_label) + "_0");
        append_command("iconst_1");
        append_command("goto " + String.valueOf(unique_label) + "_exit");

        append_command(String.valueOf(unique_label) + "_0: " + "iconst_0");

        // TODO label for exiting the whole expression
        return null;
    }

    public Void visit(MethodCall methodCall) {
        // getting the refrence
        for (Expression expression : methodCall.getArgs())
            expression.accept(this);
        append_command("invokevirtual " + methodCall);
        return null;
    }

    public Void visit(Identifier identifier) {
        return null;
    }

    public Void visit(Self self) {
        return null;
    }

    public Void visit(IntValue intValue) {
        append_command("ldc " + intValue.getConstant());
        return null;
    }

    public Void visit(NewArray newArray) {
        return null;
    }

    public Void visit(BoolValue booleanValue) {
        int int_val = booleanValue.isConstant() ? 1 : 0;
        append_command("iconst_" + int_val);
        return null;
    }

    public Void visit(StringValue stringValue) {
        append_command("ldc " + stringValue.getConstant());
        return null;
    }

    public Void visit(NewClassInstance newClassInstance) {
        append_command("new " + newClassInstance.getClassName().getName());
        append_command("dup");
        append_command("invokespecial " + newClassInstance.getClassName().getName() + "/" + "<init>()V");
        append_command("astore_" + curr_var);
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
        Type type = printStat.getArg().accept(expressionTypeExtractor);
        append_command("getstatic java/lang/System/out Ljava/io/PrintStream;");
        if ( type instanceof IntType) {
            printStat.getArg().accept(this);
            append_command("invokevirtual java/io/PrintStream/println(I)V");
        }
        else if (type instanceof StringType){
            printStat.getArg().accept(this);
            append_command("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V");
        }

        return null;
    }

    public Void visit(Assign assignStat) {

        return null;
    }

    public Void visit(Block block) {
        SymbolTable.pushFromQueue();
        //
        SymbolTable.pop();
        return null;
    }

    public Void visit(Conditional conditional) {
        return null;
    }

    public Void visit(While whileStat) {
        return null;
    }

    public Void visit(Return returnStat) {
        Type type = returnStat.getReturnedExpr().accept(expressionTypeExtractor);
        returnStat.getReturnedExpr().accept(this);
        if (type instanceof IntType || type instanceof BoolType)
            append_command("ireturn");
        else
            append_command("areturn");
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
        try{
            LocalVariableSymbolTableItem lvsti= (LocalVariableSymbolTableItem) SymbolTable.top().get(localVarDef.getLocalVarName().getName());
            curr_var  = lvsti.getIndex() + 1;
        }
        catch(ItemNotFoundException itfe){}
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
        tabs_before = 0;
        current_class = classDeclaration.getName().getName();
        create_class_file(classDeclaration.getName().getName());
        SymbolTable.pushFromQueue();
        append_command(".class public " + classDeclaration.getName().getName());
        if (classDeclaration.getParentName().getName() == null)
            append_command(".super " +  "Any"); // TODO package for any class should be added before Any keyword
        else
            append_command(".super " + classDeclaration.getParentName().getName());
        tabs_before ++;
        append_default_constructor();
        for (ClassMemberDeclaration classMemberDeclaration : classDeclaration.getClassMembers()){
            classMemberDeclaration.accept(this);
        }
        SymbolTable.pop();
        return null;
    }

    public Void visit(EntryClassDeclaration entryClassDeclaration) {
        tabs_before = 0;
        current_class = entryClassDeclaration.getName().getName();
        create_class_file(entryClassDeclaration.getName().getName());
        SymbolTable.pushFromQueue();
        append_command(".class public " + entryClassDeclaration.getName().getName());
        if (entryClassDeclaration.getParentName().getName() == null)
            append_command(".super " +  "Any"); // TODO package for any class should be added before Any keyword
        else
            append_command(".super " + entryClassDeclaration.getParentName().getName());
        tabs_before ++;
        append_default_constructor();
        for (ClassMemberDeclaration classMemberDeclaration : entryClassDeclaration.getClassMembers()){
            classMemberDeclaration.accept(this);
        }
        SymbolTable.pop();
        return null;
    }

    public Void visit(FieldDeclaration fieldDeclaration) {
        append_command(".field " + fieldDeclaration.getAccessModifier().toString() + " " + fieldDeclaration.getIdentifier().getName() + " "
                + get_type_code(fieldDeclaration.getType()));
        return null;
    }

    public Void visit(ParameterDeclaration parameterDeclaration) {
        return null;
    }

    public Void visit(MethodDeclaration methodDeclaration) {
        SymbolTable.pushFromQueue();
        String static_keyword = " ";
        if (methodDeclaration.getName().getName().equals("main"))
            static_keyword = " static";
        String arg_defs = get_args_code(methodDeclaration.getArgs());
        String access = get_access_modifier(methodDeclaration.getAccessModifier().toString());
        append_command(".method " + access + static_keyword + " " +  methodDeclaration.getName().getName() + "(" +
                arg_defs + ")" + get_type_code(methodDeclaration.getReturnType()));
        tabs_before ++;
        append_limits();
        for (Statement statement : methodDeclaration.getBody()){
            statement.accept(this);
        }
        tabs_before --;
        append_command(".end method");
        SymbolTable.pop();
        return null;
    }

    public Void visit(LocalVarsDefinitions localVarsDefinitions) {
        for (LocalVarDef localVarDef : localVarsDefinitions.getVarDefinitions()){
            localVarDef.accept(this);
        }
        return null;
    }

    public Void visit(Program program) {
//        System.out.println("ngar");
        SymbolTable.pushFromQueue();
        for (ClassDeclaration classDeclaration : program.getClasses()){
            classDeclaration.accept(this);
        }
        SymbolTable.pop();
        return null;
    }

}
