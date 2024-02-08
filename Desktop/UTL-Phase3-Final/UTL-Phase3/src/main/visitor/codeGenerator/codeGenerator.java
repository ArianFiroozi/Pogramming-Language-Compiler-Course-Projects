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
    private Dictionary<String, Integer> hash_root;
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
        hash_root = new Hashtable<>();
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
        Dictionary<String, Integer> old_root = hash_root;
        hash_root = new Hashtable<>();

        String args = "";
        for (VarDeclaration arg : functionDeclaration.getArgs())
        {
            args = args.concat(makeTypeSignature(arg.getType()) + ";");
            hash_root.put(arg.getIdentifier().getName(), stack_count);
            stack_count += 1;
        }

        addCommand("\n  .method public static " + functionDeclaration.getName().getName() + "(" + 
                     args + ")" + makeTypeSignature(functionDeclaration.getReturnType())); 
        //limits and stuff

        for (VarDeclaration varDeclaration : functionDeclaration.getArgs())
            varDeclaration.accept(this);
        for (Statement stmt : functionDeclaration.getBody())
            stmt.accept(this);

        hash_root = old_root;
        stack_count = old_stack_count;
        return null;
    }
    
    @Override
    public String visit(MainDeclaration mainDeclaration) {
        // todo    
        // invoke
        for (Statement stmt : mainDeclaration.getBody())
            stmt.accept(this);
        return null;
    }

    @Override
    public String visit(OnInitDeclaration onInitDeclaration) {
        // todo    
        // invoke    
        //smth with trades
        for (Statement stmt : onInitDeclaration.getBody())
            stmt.accept(this);
        return null;
    }

    @Override
    public String visit(OnStartDeclaration onStartDeclaration) {
        // todo    
        // invoke    
        //smth with trades
        for (Statement stmt : onStartDeclaration.getBody())
            stmt.accept(this);
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
        lexp.accept(this);
        // if (binExp.getBinaryOperator().equals(BinaryOperator.AND))
        // {
        //     addCommand("ifeq Label");
        // }
        Expression rexp = binExp.getRight();
        rexp.accept(this);

        if (binExp.getBinaryOperator().equals(BinaryOperator.PLUS))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("iadd");
            else if (lexp.getType() instanceof FloatType)
                addCommand("fadd");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.MINUS))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("isub");
            else if (lexp.getType() instanceof FloatType)
                addCommand("fsub");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.MULT))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("imul");
            else if (lexp.getType() instanceof FloatType)
                addCommand("fmul");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.DIV))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("idiv");
            else if (lexp.getType() instanceof FloatType)
                addCommand("fdiv");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.MOD))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("irem");
            else if (lexp.getType() instanceof FloatType)
                addCommand("frem");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.BIT_AND))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("iand");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.BIT_OR))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("ior");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.BIT_XOR))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("ixor");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.L_SHIFT))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("ishl");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.R_SHIFT))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("ishr");
            else addCommand(";wrong format");
        }

        // binary ret
        else if (binExp.getBinaryOperator().equals(BinaryOperator.LT))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("idiv");
            else if (lexp.getType() instanceof FloatType)
                addCommand("fdiv");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.GT))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("idiv");
            else if (lexp.getType() instanceof FloatType)
                addCommand("fdiv");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.EQ))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("idiv");
            else if (lexp.getType() instanceof FloatType)
                addCommand("fdiv");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.NEQ))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("idiv");
            else if (lexp.getType() instanceof FloatType)
                addCommand("fdiv");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.ASSIGN)) //?
        {
            if (lexp.getType() instanceof IntType)
                addCommand("idiv");
            else if (lexp.getType() instanceof FloatType)
                addCommand("fdiv");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.DIV))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("idiv");
            else if (lexp.getType() instanceof FloatType)
                addCommand("fdiv");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.DIV))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("idiv");
            else if (lexp.getType() instanceof FloatType)
                addCommand("fdiv");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.DIV))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("idiv");
            else if (lexp.getType() instanceof FloatType)
                addCommand("fdiv");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.DIV))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("idiv");
            else if (lexp.getType() instanceof FloatType)
                addCommand("fdiv");
            else addCommand(";wrong format");
        }
        else if (binExp.getBinaryOperator().equals(BinaryOperator.DIV))
        {
            if (lexp.getType() instanceof IntType)
                addCommand("idiv");
            else if (lexp.getType() instanceof FloatType)
                addCommand("fdiv");
            else addCommand(";wrong format");
        }
        return null;
    }

    @Override
    public String visit(UnaryExpression unExp)
    {
        System.out.println("uni");
        return null;
    }

    @Override
    public String visit(VarDeclaration varDeclaration) {
        //todo
        hash_root.put(varDeclaration.getIdentifier().getName(), stack_count++);

        if (varDeclaration.getRValue()!= null)
        {   //arrays unhandled
            varDeclaration.getRValue().accept(this);
            if (varDeclaration.getType() instanceof IntType || varDeclaration.getType() instanceof BoolType)
                addCommand("istore " + hash_root.get(varDeclaration.getIdentifier().getName()));
            else if (varDeclaration.getType() instanceof FloatType)
                addCommand("fstore " + hash_root.get(varDeclaration.getIdentifier().getName()));
            else
                addCommand("astore " + hash_root.get(varDeclaration.getIdentifier().getName()));
        }
        return null;
    }

    @Override
    public String visit(AssignStmt assignmentStmt) {
        //todo
        assignmentStmt.getRValue();

        addCommand("store ?");

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
        command.concat(cond_label+":\n");
        command.concat(cond_code+"\n");
        command.concat("ifeq "+ end_label);
        for (Statement stmt : while_stmt.getBody())
        {
            command.concat(stmt.accept(this)+"\n");
            
        }
        command.concat("goto " + cond_label);
        command.concat(end_label+":\n");

        addCommand(command);
        return command;
    }

    @Override
    public String visit(MethodCall methodCallStmt) {
        //todo
        // invoke
            
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

            addCommand("areturn");
            //todo add commands to return
        }
        return null;
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
        commands += "bipush " + String.valueOf(intValue.getConstant());
        
        System.out.println("int");
        return commands;
    }

    @Override
    public String visit(BoolValue boolValue) {
        String commands = "";
        //todo
        System.out.println("bool");
        return commands;
    }

    @Override
    public String visit(StringValue stringValue) {
        String commands = "";
        //todo
        System.out.println("string");
        return commands;
    }

}