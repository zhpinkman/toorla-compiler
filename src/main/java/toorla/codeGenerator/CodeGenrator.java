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
import toorla.symbolTable.symbolTableItem.varItems.FieldSymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.LocalVariableSymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.VarSymbolTableItem;
import toorla.typeChecker.ExpressionTypeExtractor;
import toorla.types.Type;
import toorla.types.arrayType.ArrayType;
import toorla.types.singleType.BoolType;
import toorla.types.singleType.IntType;
import toorla.types.singleType.StringType;
import toorla.types.singleType.UserDefinedType;
import toorla.utilities.graph.Graph;
import toorla.visitor.Visitor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    private int var_index = 0;

    ExpressionTypeExtractor expressionTypeExtractor;
//    public PrintWriter printWriter;
    int tabs_before;


    public void copy_jasmin_file(){


        InputStream inStream = null;
        OutputStream outStream = null;

        try{

            File afile =new File("jasmin.jar");
            File bfile =new File("artifact/jasmin.jar");

            inStream = new FileInputStream(afile);
            outStream = new FileOutputStream(bfile);

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes
            while ((length = inStream.read(buffer)) > 0){

                outStream.write(buffer, 0, length);

            }

            inStream.close();
            outStream.close();

            System.out.println("File is copied successful!");

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public String get_access_modifier(String access_modifier){
        if (access_modifier.equals(PUBLIC_ACCESS))
            return "public";
        else
            return "private";
    }

    public void append_runner_class(String entry_class_name){
        try(FileWriter fw = new FileWriter("artifact/" + "Runner" + ".j", false);
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
                    "new " + entry_class_name + "\n" +
                    "dup\n" +
                    "invokespecial " + entry_class_name + "/<init>()V\n" +
                    "astore_1\n" +
                    "aload 1\n" +
                    "invokestatic " + entry_class_name + "/main()I\n" +
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
        append_command(".limit locals 10"); // TODO local variables should be counted for this part // Just make it big enough
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
        try(FileWriter fw = new FileWriter("artifact/" + class_name + ".j", false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {} catch (IOException e) {
        }
    }


    public void create_Any_class(){
        try(FileWriter fw = new FileWriter("artifact/" + "Any" + ".j", false);
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
            copy_jasmin_file();
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


    public VarSymbolTableItem find_var_or_field(String name){
        try {
            SymbolTable xx = SymbolTable.top();
            LocalVariableSymbolTableItem lvsti = null;
            while(lvsti == null) {
                try {
                    lvsti = (LocalVariableSymbolTableItem) xx.get("var_" + name);
                    if (lvsti.getIndex() <= var_index) {

                    } else {
                        lvsti = null;
                        throw new Exception();
                    }
                } catch (Exception e) {
                    xx = xx.getPreSymbolTable();
                }
            }
            return lvsti;
        } catch (Exception exception) { // it's a field
            try {
                SymbolTable xx = SymbolTable.top();
                String z = "var_" + name;
                boolean f = false;
                FieldSymbolTableItem field_item = null;
                while(field_item == null){
                    try{
                        field_item = (FieldSymbolTableItem) xx.get("var_" + name);
                    }catch (Exception e){
                        xx = xx.getPreSymbolTable();
                    }
                }
                return field_item;
            } catch (Exception e) {
                //Field call is being handled in it's node, if there is no a.b we assume it was variable we were searching for
                //System.out.println("error about couldn't finding variable or field"); // not sure which error to emit
            }
        }
        return null;
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
        equalsExpr.getLhs().accept(this);
        equalsExpr.getRhs().accept(this);
        append_command("if_icmpne " + String.valueOf(unique_label) + "_else");
        return null;
    }

    public Void visit(GreaterThan gtExpr) {
        gtExpr.getLhs().accept(this);
        gtExpr.getRhs().accept(this);
        append_command("if_icmple " + String.valueOf(unique_label) + "_else");

        return null;
    }

    public Void visit(LessThan lessThanExpr) {
        lessThanExpr.getLhs().accept(this);
        lessThanExpr.getRhs().accept(this);
        append_command("if_icmpge " + String.valueOf(unique_label) + "_else");
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
        try {
            VarSymbolTableItem variableSymbol = find_var_or_field(identifier.getName());
            SymbolTable xx = SymbolTable.top();
            try {
                int var_index = ((LocalVariableSymbolTableItem) variableSymbol).getIndex(); // if it is variable
                Type var_type = variableSymbol.getType();
                if( (var_type instanceof IntType) || (var_type instanceof BoolType)){
                    append_command("iload_" + var_index);
                }else{
                    append_command("aload_" + var_index);
                }

            }catch (Exception e){ // it is field
                Type var_type = variableSymbol.getType();
                if( (var_type instanceof IntType) || (var_type instanceof BoolType)){
                    append_command("aload_0"); // push self as obj ref
                    append_command("getfield" + current_class + "/" + identifier.getName() + " I");
                }else{
                    //TODO: ObjectReference package.ClassName -> Lpackage.ClassName;
                    // Array of type a -> [a
                }
            }
        }catch (Exception e){

        }
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
        Type class_type = fieldCall.getInstance().accept(expressionTypeExtractor);
        System.out.println("s");
        return null;
    }

    public Void visit(ArrayCall arrayCall) {
        return null;
    }

    public Void visit(NotEquals notEquals) {
        notEquals.getLhs().accept(this);
        notEquals.getRhs().accept(this);
        append_command("if_icmpeq " + String.valueOf(unique_label) + "_else");
        return null;
    }

    // Statement
    public Void visit(PrintLine printStat) {
        Type type = printStat.getArg().accept(expressionTypeExtractor); // TODO: their code is returning wrong type!!!!
        append_command("getstatic java/lang/System/out Ljava/io/PrintStream;");
        if ( type instanceof IntType) {
            printStat.getArg().accept(this);
            append_command("invokevirtual java/io/PrintStream/println(I)V");
        }
        else if (type instanceof StringType){
            printStat.getArg().accept(this);
            append_command("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V");
        }
        //TODO ARRAY TYPE

        return null;
    }

    public Void visit(Assign assignStat) {
        //VarSymbolTableItem lhs_symbol_table = find_var_or_field(assignStat.getLvalue().ac);
        if(assignStat.getLvalue() instanceof  Identifier){ // Variable Or Self field

        }else if(assignStat.getLvalue() instanceof  ArrayCall){ // array[10] or field

        }else if(assignStat.getLvalue() instanceof  FieldCall){

        }
        return null;
    }

    public Void visit(Block block) {
        SymbolTable.pushFromQueue();
        for (Statement stmt : block.body)
            stmt.accept(this);
        SymbolTable.pop();
        return null;
    }

    public Void visit(Conditional conditional) {
        conditional.getCondition().accept(this);

        conditional.getThenStatement().accept(this);
        append_command("goto " + String.valueOf(unique_label) + "_exit");

        append_command(String.valueOf(unique_label) + "_else : ");
        conditional.getElseStatement().accept(this);

        append_command(String.valueOf(unique_label) + "_exit : ");

        unique_label ++;
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
        var_index++;
        try{
            Type init_val_type = localVarDef.getInitialValue().accept(expressionTypeExtractor);
            //SymbolTable xx = SymbolTable.top();
            LocalVariableSymbolTableItem lvsti= (LocalVariableSymbolTableItem) SymbolTable.top().get("var_"+localVarDef.getLocalVarName().getName()); // You had forgotten "var_" prefix. check other places too
            curr_var  = lvsti.getIndex();
            localVarDef.getInitialValue().accept(this); // PUSH init value to store

            if(init_val_type instanceof IntType || init_val_type instanceof BoolType){
                append_command("istore_"+curr_var);
            }else{
                append_command("astore_"+curr_var);
            }
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
        append_runner_class(entryClassDeclaration.getName().getName());

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
        var_index++;
        return null;
    }

    public Void visit(MethodDeclaration methodDeclaration) {
        var_index = 0;
        SymbolTable.pushFromQueue();
        String static_keyword = " ";
//        if (methodDeclaration.getName().getName().equals("main"))
//            static_keyword = " static";
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
