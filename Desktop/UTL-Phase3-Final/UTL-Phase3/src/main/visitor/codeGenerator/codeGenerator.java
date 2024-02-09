package main.visitor.codeGenerator;

import main.ast.node.Program;
import main.ast.node.declaration.*;
import main.ast.node.expression.*;
import main.ast.node.statement.*;
import main.ast.type.Type;
import main.ast.type.complexType.TradeType;
import main.compileError.CompileError;
import main.compileError.type.ConditionTypeNotBool;
import main.symbolTable.SymbolTable;
import main.symbolTable.itemException.ItemNotFoundException;
import main.symbolTable.symbolTableItems.*;
import main.visitor.Visitor;
import main.ast.node.expression.operators.BinaryOperator;
import main.ast.node.expression.operators.UnaryOperator;
import main.ast.node.expression.values.*;
import main.ast.type.*;
import main.ast.type.primitiveType.*;
import main.compileError.*;
import main.compileError.type.UnsupportedOperandType;
import main.symbolTable.SymbolTable;
import main.symbolTable.itemException.ItemNotFoundException;
import main.symbolTable.symbolTableItems.FunctionItem;
import main.symbolTable.symbolTableItems.SymbolTableItem;
import main.symbolTable.symbolTableItems.VariableItem;
import main.visitor.*;
import main.ast.node.declaration.*;
import main.ast.type.complexType.TradeType;
import main.compileError.CompileError;
import main.compileError.name.*;
import main.symbolTable.SymbolTable;
import main.symbolTable.itemException.ItemAlreadyExistsException;
import main.symbolTable.itemException.ItemNotFoundException;
import main.symbolTable.symbolTableItems.*;
import main.visitor.Visitor;
import main.visitor.typeAnalyzer.*;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.io.*;

public class codeGenerator extends Visitor<String> {
//    You may use following items or add your own for handling typechecker
    TypeChecker expressionTypeChecker;
//    Graph<String> classHierarchy;
    private String outputPath;
    private FileWriter currentFile;
    private FunctionDeclaration currentMethod;
    private Dictionary<String, Integer> name_to_id;
    private Dictionary<String, Type> name_to_type;
    Integer stack_count;

    public codeGenerator() {
//        this.classHierarchy = classHierarchy;

//        Uncomment below line to initialize your typechecker
        this.expressionTypeChecker = new TypeChecker(new ArrayList<CompileError>());

//        Call your type checker here!
//        ----------------------------
        this.prepareOutputFolder();

    }

    private void prepareOutputFolder() {
        this.outputPath = "output/";
        String jasminPath = "utilities/jarFiles/jasmin.jar";
        String listClassPath = "utilities/codeGenerationUtilityClasses/List.j";
        String fptrClassPath = "utilities/codeGenerationUtilityClasses/Fptr.j";
        try{
            File directory = new File(this.outputPath);
            File[] files = directory.listFiles();
            if(files != null)
                for (File file : files)
                    file.delete();
            directory.mkdir();
        }
        catch(SecurityException e) { }
        copyFile(jasminPath, this.outputPath + "jasmin.jar");
        copyFile(listClassPath, this.outputPath + "List.j");
        copyFile(fptrClassPath, this.outputPath + "Fptr.j");
    }

    private void copyFile(String toBeCopied, String toBePasted) {
        try {
            File readingFile = new File(toBeCopied);
            File writingFile = new File(toBePasted);
            InputStream readingFileStream = new FileInputStream(readingFile);
            OutputStream writingFileStream = new FileOutputStream(writingFile);
            byte[] buffer = new byte[1024];
            int readLength;
            while ((readLength = readingFileStream.read(buffer)) > 0)
                writingFileStream.write(buffer, 0, readLength);
            readingFileStream.close();
            writingFileStream.close();
        } catch (IOException e) { }
    }

    private void createFile(String name) {
        try {
            String path = this.outputPath + name + ".j";
            File file = new File(path);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(path);
            this.currentFile = fileWriter;
        } catch (IOException e) {}
    }

    private void addCommand(String command) {
        try {
            command = String.join("\n\t\t", command.split("\n"));
            if(command.startsWith("Label_"))
                this.currentFile.write("\t" + command + "\n");
            else if(command.startsWith("."))
                this.currentFile.write(command + "\n");
            else
                this.currentFile.write("\t\t" + command + "\n");
            System.out.println(command);
            this.currentFile.flush();
        } catch (IOException e) {}
    }

    private void addField(String name, String type)
    {
        addCommand(".field " + name + " " + type);
    }

    private String makeTypeSignature(Type t) {
        //todo
        if (t instanceof IntType) return "I";
        else if (t instanceof BoolType) return "Z";
        else if (t instanceof FloatType) return "F";
        else if (t instanceof StringType) return "[C";
        else return "V";
        // complex types unhandled
    }

    private String load_exp(Expression exp)
    {
//        if (exp.)
        return "";
    }

    @Override
    public String visit(Program program) {
        //todo
        // we may need a base stack count
        name_to_id = new Hashtable<>();
        name_to_type = new Hashtable<>();
        createFile(program.toString());

        addCommand(".class public Program");
        addCommand(".super main/ast/node/Node");

        addField("vars", "Ljava/util/ArrayList");
        addField("inits", "Ljava/util/ArrayList");
        addField("starts", "Ljava/util/ArrayList");
        addField("functions", "Ljava/util/ArrayList");
        addField("programMainDeclaration", "Ljava/util/MainDeclaration"); // not sure about type
        
        stack_count = 0;
        // SymbolTable.root = new SymbolTable();
        // SymbolTable.push(SymbolTable.root);
        for (VarDeclaration varDeclaration : program.getVars())
            varDeclaration.accept(this);
        for (FunctionDeclaration functionDeclaration : program.getFunctions())
            functionDeclaration.accept(this);
        for (OnInitDeclaration onInitDeclaration : program.getInits())
            onInitDeclaration.accept(this);
        for (OnStartDeclaration onStartDeclaration : program.getStarts())
            onStartDeclaration.accept(this);
        program.getMain().accept(this);
        return null;
    }

    @Override
    public String visit(FunctionDeclaration functionDeclaration) {
        // todo    
        // invoke
        int old_stack_count = stack_count;
        stack_count = 1;
        Dictionary<String, Integer> old_n2i = name_to_id;
        Dictionary<String, Type> old_n2t = name_to_type;
        name_to_id = new Hashtable<>();
        name_to_type = new Hashtable<>();

        String args = "";
        for (VarDeclaration arg : functionDeclaration.getArgs())
        {
            args = args.concat(makeTypeSignature(arg.getType()) + ";");
            name_to_id.put(arg.getIdentifier().getName(), stack_count);
            name_to_type.put(arg.getIdentifier().getName(), arg.getType());
            stack_count += 1;
        }

        addCommand("\n.method public static " + functionDeclaration.getName().getName() + "(" + 
                     args + ")" + makeTypeSignature(functionDeclaration.getReturnType())); 
        //limits and stuff

        for (VarDeclaration varDeclaration : functionDeclaration.getArgs())
            varDeclaration.accept(this);
        for (Statement stmt : functionDeclaration.getBody())
            stmt.accept(this);

        name_to_id = old_n2i;
        name_to_type = old_n2t;
        stack_count = old_stack_count;
        return null;
    }
    
    @Override
    public String visit(MainDeclaration mainDeclaration) {
        // todo    
        // invoke
        int old_stack_count = stack_count;
        stack_count = 1;
        Dictionary<String, Integer> old_n2i = name_to_id;
        Dictionary<String, Type> old_n2t = name_to_type;
        name_to_id = new Hashtable<>();
        name_to_type = new Hashtable<>();

        addCommand("\n.method public static main()V"); 
        //limits and stuff

        for (Statement stmt : mainDeclaration.getBody())
            stmt.accept(this);

        name_to_id = old_n2i;
        name_to_type = old_n2t;
        stack_count = old_stack_count;
        return null;
    }

    @Override
    public String visit(OnInitDeclaration onInitDeclaration) {
        // todo    
        // invoke    
        //smth with trades
        int old_stack_count = stack_count;
        stack_count = 1;
        Dictionary<String, Integer> old_n2i = name_to_id;
        Dictionary<String, Type> old_n2t = name_to_type;
        name_to_id = new Hashtable<>();
        name_to_type = new Hashtable<>();

        addCommand("\n.method public static OnInit(L/trade?)V"); // check the document 
        //limits and stuff

        // onInitDeclaration.getTradeName().accept(this); //?
        name_to_id.put(onInitDeclaration.getTradeName().getName(), stack_count);
        name_to_type.put(onInitDeclaration.getTradeName().getName(), new TradeType());
        stack_count++;

        name_to_id.put(onInitDeclaration.getTradeName().getName(), stack_count);
        stack_count++;
        name_to_type.put(onInitDeclaration.getTradeName().getName(), new TradeType());
        for (Statement stmt : onInitDeclaration.getBody())
            stmt.accept(this);

        name_to_id = old_n2i;
        name_to_type = old_n2t;
        stack_count = old_stack_count;
        return null;
    }

    @Override
    public String visit(OnStartDeclaration onStartDeclaration) {
        // todo    
        // invoke    
        //smth with trades
        int old_stack_count = stack_count;
        stack_count = 1;
        Dictionary<String, Integer> old_n2i = name_to_id;
        Dictionary<String, Type> old_n2t = name_to_type;
        name_to_id = new Hashtable<>();
        name_to_type = new Hashtable<>();

        addCommand("\n.method public static OnStart(L/trade?)V"); // check the document 
        //limits and stuff

        // onStartDeclaration.getTradeName().accept(this); //?
        name_to_id.put(onStartDeclaration.getTradeName().getName(), stack_count);
        name_to_type.put(onStartDeclaration.getTradeName().getName(), new TradeType());
        stack_count++;

        name_to_id.put(onStartDeclaration.getTradeName().getName(), stack_count);
        stack_count++;
        name_to_type.put(onStartDeclaration.getTradeName().getName(), new TradeType());
        for (Statement stmt : onStartDeclaration.getBody())
            stmt.accept(this);

        name_to_id = old_n2i;
        name_to_type = old_n2t;
        stack_count = old_stack_count;
        return null;
    }

    @Override
    public String visit(ExpressionStmt expressionStmt) {
        //todo
        System.out.println("exprStmt");
        Expression exp = expressionStmt.getExpression();
        exp.accept(this); // if ret != null ye gohi bokhor
        return null;
    }

    @Override
    public String visit(BinaryExpression binExp) {  //expression should return it's value instead of addcommanding it
        Expression lexp = binExp.getLeft();
        Expression rexp = binExp.getRight();

        if (binExp.getBinaryOperator().equals(BinaryOperator.AND)) { // short circuit implementation
            lexp.accept(this);
            addCommand("ifeq Label_False_" + binExp.getLine());
            rexp.accept(this);
            addCommand("ifeq Label_False_" + binExp.getLine());
            addCommand("iconst_1");
            addCommand("goto Label_" + binExp.getLine()+"_End");
            addCommand("Label_False_" + binExp.getLine()+":");
            addCommand("iconst_0");
            addCommand("Label_" + binExp.getLine()+"_End:");
            return null;
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.OR)) {
            lexp.accept(this);
            addCommand("ifeq Label_True_" + binExp.getLine());
            rexp.accept(this);
            addCommand("ifeq Label_True_" + binExp.getLine());
            addCommand("iconst_0");
            addCommand("goto Label_" + binExp.getLine()+"_End");
            addCommand("Label_True_" + binExp.getLine()+":");
            addCommand("iconst_1");
            addCommand("Label_" + binExp.getLine()+"_End:");
            return null;
        } 

        lexp.accept(this);
        rexp.accept(this);
        Type type = rexp.accept(expressionTypeChecker);

        if (binExp.getBinaryOperator().equals(BinaryOperator.PLUS)) {
            if (type instanceof IntType)
                addCommand("iadd");
            else if (type instanceof FloatType)
                addCommand("fadd");
            else addCommand(";wrong format" + lexp.toString());
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.MINUS)) {
            if (type instanceof IntType)
                addCommand("isub");
            else if (type instanceof FloatType)
                addCommand("fsub");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.MULT)) {
            if (type instanceof IntType)
                addCommand("imul");
            else if (type instanceof FloatType)
                addCommand("fmul");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.DIV)) {
            if (type instanceof IntType)
                addCommand("idiv");
            else if (type instanceof FloatType)
                addCommand("fdiv");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.MOD)) {
            if (type instanceof IntType)
                addCommand("irem");
            else if (type instanceof FloatType)
                addCommand("frem");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.BIT_AND)) {
            if (type instanceof IntType)
                addCommand("iand");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.BIT_OR)) {
            if (type instanceof IntType)
                addCommand("ior");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.BIT_XOR)) {
            if (type instanceof IntType)
                addCommand("ixor");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.L_SHIFT)) {
            if (type instanceof IntType)
                addCommand("ishl");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.R_SHIFT)) {
            if (type instanceof IntType)
                addCommand("ishr");
            else addCommand(";wrong format");
        }

        // binary ret
        else if (binExp.getBinaryOperator().equals(BinaryOperator.LT)) {
            addCommand("if_icmplt Label_" + binExp.getLine());
            addCommand("iconst_0");
            addCommand("goto Label_" + binExp.getLine() + "_End:");
            addCommand("Label_" + binExp.getLine() + ":");
            addCommand("iconst_1");
            addCommand("if_icmplt Label_" + binExp.getLine() + "_End:");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.GT)) {
            
            addCommand("if_icmpgt Label_" + binExp.getLine());
            addCommand("iconst_0");
            addCommand("goto Label_" + binExp.getLine() + "_End:");
            addCommand("Label_" + binExp.getLine() + ":");
            addCommand("iconst_1");
            addCommand("if_icmplt Label_" + binExp.getLine() + "_End:");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.EQ)) {
            
            addCommand("if_icmpeq Label_" + binExp.getLine());
            addCommand("iconst_0");
            addCommand("goto Label_" + binExp.getLine() + "_End:");
            addCommand("Label_" + binExp.getLine() + ":");
            addCommand("iconst_1");
            addCommand("if_icmplt Label_" + binExp.getLine() + "_End:");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.NEQ)) {
            
            addCommand("if_icmpneq Label_" + binExp.getLine());
            addCommand("iconst_0");
            addCommand("goto Label_" + binExp.getLine() + "_End:");
            addCommand("Label_" + binExp.getLine() + ":");
            addCommand("iconst_1");
            addCommand("if_icmplt Label_" + binExp.getLine() + "_End:");
        }

        // assign
        else if (binExp.getBinaryOperator().equals(BinaryOperator.ASSIGN)) // fuck we need to move varcall command to here
        {
            if (type instanceof IntType)
                addCommand("idiv");
            else if (type instanceof FloatType)
                addCommand("fdiv");
                
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.ADD_ASSIGN))
        {
            if (type instanceof IntType)
                addCommand("idiv");
            else if (type instanceof FloatType)
                addCommand("fdiv");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.SUB_ASSIGN))
        {
            if (type instanceof IntType)
                addCommand("idiv");
            else if (type instanceof FloatType)
                addCommand("fdiv");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.MUL_ASSIGN))
        {
            if (type instanceof IntType)
                addCommand("idiv");
            else if (type instanceof FloatType)
                addCommand("fdiv");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.DIV_ASSIGN))
        {
            if (type instanceof IntType)
                addCommand("idiv");
            else if (type instanceof FloatType)
                addCommand("fdiv");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.MOD_ASSIGN))
        {
            if (type instanceof IntType)
                addCommand("idiv");
            else if (type instanceof FloatType)
                addCommand("fdiv");
            else addCommand(";wrong format");
        }
        return null;
    }

    @Override
    public String visit(UnaryExpression unExp)
    {
        System.out.println("uni");
        unExp.getOperand().accept(this);
        Type type = unExp.getOperand().getType(); //can't get the type
        if (unExp.getUnaryOperator().equals(UnaryOperator.INC))
        {
            // if (type instanceof IntType)
            // {
                addCommand("iinc");//wrong format
            // }
            // else
            // {
            //     addCommand(";wrong format");
            // }
        }
        else if (unExp.getUnaryOperator().equals(UnaryOperator.DEC))
        {
            // if (type instanceof IntType)
            // {
                addCommand("iconst_1");
                addCommand("isub");
            // }
            // else
            // {
            //     addCommand(";wrong format");
            // }
        }
        else if (unExp.getUnaryOperator().equals(UnaryOperator.NOT))
        {
            // if (type instanceof IntType)
            // {
                addCommand("ifeq Label_" + unExp.getLine());
                addCommand("pop");
                addCommand("iconst_0");
                addCommand("goto Label_" + unExp.getLine() + "End");
                addCommand("Label_" + unExp.getLine()+ ":");
                addCommand("\tpop");
                addCommand("\ticonst_1");
                addCommand("Label_" + unExp.getLine() + "End:");
            // }
            // else
            // {
            //     addCommand(";wrong format" + unExp.getOperand().toString());
            // }
        }
        else if (unExp.getUnaryOperator().equals(UnaryOperator.MINUS))
        {
            if (unExp.getType() instanceof IntType)
            {
                addCommand("ineg");
            }
            else if (unExp.getType() instanceof FloatType)
            {
                addCommand("fneg");
            }
            else
            {
                addCommand(";wrong format");
            }
        }
        else if (unExp.getUnaryOperator().equals(UnaryOperator.BIT_NOT))
        {
            addCommand("ineg");
        }
        return null;
    }

    @Override
    public String visit(VarDeclaration varDeclaration) {
        //todo

        name_to_id.put(varDeclaration.getIdentifier().getName(), stack_count);
        stack_count++;
        name_to_type.put(varDeclaration.getIdentifier().getName(), varDeclaration.getType());

        if (varDeclaration.getRValue()!= null)
        {   //arrays unhandled
            varDeclaration.getRValue().accept(this);
            if (varDeclaration.getType() instanceof IntType || varDeclaration.getType() instanceof BoolType)
                addCommand("istore " + name_to_id.get(varDeclaration.getIdentifier().getName()));
            else if (varDeclaration.getType() instanceof FloatType)
                addCommand("fstore " + name_to_id.get(varDeclaration.getIdentifier().getName()));
            else
                addCommand("astore " + name_to_id.get(varDeclaration.getIdentifier().getName()));
        }
        return null;
    }

    @Override
    public String visit(AssignStmt assignmentStmt) {
        //todo
        assignmentStmt.getLValue().accept(this);
        assignmentStmt.getRValue().accept(this);

        if (assignmentStmt.getLValue().getType() instanceof IntType) // wtf
            addCommand("istore " + name_to_id.get(assignmentStmt.getLValue().toString()));
        addCommand("store ?" + name_to_id.get(assignmentStmt.getLValue().toString()));

        return null;
    }

    // @Override
    // public String visit(BlockStmt blockStmt) {
    //     //todo
    //     return null;
    // }

    @Override
    public String visit(IfElseStmt conditionalStmt) {
        String command = "";
        Expression cond = conditionalStmt.getCondition();
        String cond_code = cond.accept(this);
        String else_label_name = "Label_" + String.valueOf(conditionalStmt.getLine()); 
        command = "ifeq " + else_label_name +"\n";
        for (Statement stmt : conditionalStmt.getThenBody())
        {
            command.concat(stmt.accept(this)+"\n");
        }
        command=command.concat("goto "+ "Label_End_"+String.valueOf(conditionalStmt.getLine()));
        command = command.concat(else_label_name+":\n");
        for (Statement stmt : conditionalStmt.getElseBody())
        {
            command.concat(stmt.accept(this)+"\n");
        }
        command=command.concat("Label_End_"+String.valueOf(conditionalStmt.getLine())+":\n");
        addCommand(command);
        return command;
    }

    @Override 
    public String visit(WhileStmt while_stmt)
    {
        String command = "";
        Expression cond = while_stmt.getCondition();
        String cond_code= cond.accept(this);
        String cond_label = "Label_cond_"+String.valueOf(while_stmt.getLine());
        String end_label = "Label_end_"+String.valueOf(while_stmt.getLine());
        command = command.concat(cond_label+":\n");
        command = command.concat(cond_code+"\n");
        command = command.concat("ifeq "+ end_label);
        for (Statement stmt : while_stmt.getBody())
        {
            command = command.concat(stmt.accept(this)+"\n");
            
        }
        command = command.concat("goto " + cond_label);
        command = command.concat(end_label+":\n");

        addCommand(command);
        return command;
    }

    @Override
    public String visit(MethodCall methodCallStmt) {
        //todo
        // invoke
            
        return null;
    }

    @Override
    public String visit(FunctionCall funcCall) {
        //todo
        // invoke
        for (Expression arg : funcCall.getArgs()) arg.accept(this);
        addCommand("invokevirtual Program/" + funcCall.getFunctionName().getName() + "(args?)ret?");
        return null;
    }

    // @Override
    // public String visit(PrintStmt print) {
    //     //todo
    //     return null;
    // }

    @Override
    public String visit(ReturnStmt returnStmt) {
        Type type = returnStmt.getReturnedExpr().accept(expressionTypeChecker);
        if(type instanceof NullType) {
            addCommand("return");
        }
        else {
            // load the return exp ??
            returnStmt.getReturnedExpr().accept(this);
            if (type instanceof IntType || type instanceof BoolType)
                addCommand("ireturn");
            else if (type instanceof FloatType)
                addCommand("freturn");
            else
                addCommand("areturn");

            //todo add commands to return
        }
        return null;
    }
    
    @Override
    public String visit(Identifier id) {
        String commands = "";
        //todo

        addCommand("load " + name_to_id.get(id.getName()) + id.getName());
        return commands;
    }

    @Override
    public String visit(TradeValue trade) {
        String commands = "";
        //todo        
        return commands;
    }

    @Override
    public String visit(NullValue nullValue) {
        String commands = "";
        //todo
        System.out.println("null");
        return commands;
    }

    @Override
    public String visit(IntValue intValue) {
        String commands = "";
        commands = commands.concat("bipush " + String.valueOf(intValue.getConstant()));
        addCommand(commands);
        // System.out.println("int");
        return commands;
    }

    @Override
    public String visit(BoolValue boolValue) {
        //todo
        String command;
        if (boolValue.getConstant()) command = "iconst_1";
        // System.out.println("bool");
        else command = "iconst_0";
        addCommand(command);
        return command;
    }

    @Override
    public String visit(StringValue stringValue) {
        String commands = "";
        //todo
        System.out.println("string");
        return commands;
    }

}