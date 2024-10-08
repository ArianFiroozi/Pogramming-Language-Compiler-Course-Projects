package main.visitor.codeGenerator;

import main.ast.node.Program;
import main.ast.node.declaration.*;
import main.ast.node.expression.*;
import main.ast.node.statement.*;
import main.ast.type.Type;
import main.ast.type.complexType.OrderType;
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
    // You may use following items or add your own for handling typechecker
    TypeChecker expressionTypeChecker;
    // Graph<String> classHierarchy;
    private String outputPath;
    private FileWriter currentFile;
    private FunctionDeclaration currentMethod;
    private Dictionary<String, Integer> name_to_id;
    private Dictionary<String, Type> name_to_type;
    private Dictionary<String, Type> root_n2t;
    private Dictionary<String, Type> fields;

    private Dictionary<String, ArrayList<Type>> funcList;
    Integer stack_count;
    Integer label_count;

    public codeGenerator() {
        // this.classHierarchy = classHierarchy;

        // Uncomment below line to initialize your typechecker
        this.expressionTypeChecker = new TypeChecker(new ArrayList<CompileError>());

        // Call your type checker here!
        // ----------------------------
        this.prepareOutputFolder();

    }

    private void prepareOutputFolder() {
        this.outputPath = "output/";
        String jasminPath = "utilities/jarFiles/jasmin.jar";
        String listClassPath = "utilities/codeGenerationUtilityClasses/List.j";
        String fptrClassPath = "utilities/codeGenerationUtilityClasses/Fptr.j";
        try {
            File directory = new File(this.outputPath);
            File[] files = directory.listFiles();
            if (files != null)
                for (File file : files)
                    file.delete();
            directory.mkdir();
        } catch (SecurityException e) {
        }
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
        } catch (IOException e) {
        }
    }

    private void createFile(String name) {
        try {
            String path = this.outputPath + name + ".j";
            File file = new File(path);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(path);
            this.currentFile = fileWriter;
        } catch (IOException e) {
        }
    }

    private void addCommand(String command) {
        try {
            command = String.join("\n\t\t", command.split("\n"));
            if (command.startsWith("Label_"))
                this.currentFile.write("\t" + command + "\n");
            else if (command.startsWith(".m"))
                this.currentFile.write("\n" + command + "\n");
            else if (command.startsWith("."))
                this.currentFile.write(command + "\n");
            else
                this.currentFile.write("\t\t" + command + "\n");
            System.out.println(command);
            this.currentFile.flush();
        } catch (IOException e) {
        }
    }

    private void addField(String name, String type) {
        addCommand(".field " + name + " " + type);
    }

    private String makeTypeSignature(Type t) {
        // todo
        if (t instanceof IntType)
            return "I";
        else if (t instanceof BoolType)
            return "Z";
        else if (t instanceof FloatType)
            return "F";
        else if (t instanceof StringType)
            return "Ljava/lang/String";
        else if (t instanceof TradeType)
            return "LTrade";
        else
            return "V";
        // complex types unhandled
    }

    private String load_exp(Expression exp) {
        // if (exp.)
        return "";
    }

    @Override
    public String visit(Program program) {
        // todo
        // we may need a base stack count
        name_to_id = new Hashtable<>();
        name_to_type = new Hashtable<>();
        root_n2t = name_to_type;
        fields = new Hashtable<>();

        funcList = new Hashtable<>();
        createFile(program.toString());

        addCommand(".class public UTL");
        addCommand(".super java/lang/Object");

        addField("vars", "Ljava/util/ArrayList");
        addField("inits", "Ljava/util/ArrayList");
        addField("starts", "Ljava/util/ArrayList");
        addField("functions", "Ljava/util/ArrayList");

        addField("programMainDeclaration", "Ljava/util/MainDeclaration"); 

        stack_count = 1;
        label_count = 1;

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

        ArrayList<Type> argTypes = new ArrayList<>();

        argTypes.add(functionDeclaration.getReturnType());
        String args = "";
        for (VarDeclaration arg : functionDeclaration.getArgs()) {
            argTypes.add(arg.getType());
            args = args.concat(makeTypeSignature(arg.getType()) + ";");
            name_to_id.put(arg.getIdentifier().getName(), stack_count);
            name_to_type.put(arg.getIdentifier().getName(), arg.getType());
            stack_count += 1;
        }

        addCommand(".method public static " + functionDeclaration.getName().getName() + "(" +
                args + ")" + makeTypeSignature(functionDeclaration.getReturnType()));
        addCommand(".limit stack 128");
        addCommand(".limit locals 128");

        funcList.put(functionDeclaration.getName().getName(), argTypes);

        for (VarDeclaration varDeclaration : functionDeclaration.getArgs())
            varDeclaration.accept(this);
        for (Statement stmt : functionDeclaration.getBody())
            stmt.accept(this);

        name_to_id = old_n2i;
        name_to_type = old_n2t;
        stack_count = old_stack_count;
        addCommand("return");
        addCommand(".end method");
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

        addCommand(".method public static Main()V");
        addCommand(".limit stack 128");
        addCommand(".limit locals 128");

        for (Statement stmt : mainDeclaration.getBody())
            stmt.accept(this);

        name_to_id = old_n2i;
        name_to_type = old_n2t;
        stack_count = old_stack_count;
        addCommand("return");
        addCommand(".end method");
        return null;
    }

    @Override
    public String visit(OnInitDeclaration onInitDeclaration) {
        // todo

        int old_stack_count = stack_count;
        stack_count = 1;
        Dictionary<String, Integer> old_n2i = name_to_id;
        Dictionary<String, Type> old_n2t = name_to_type;
        name_to_id = new Hashtable<>();
        name_to_type = new Hashtable<>();

        addCommand(".method public static OnInit(LTrade;)V"); // check the document
        addCommand(".limit stack 128");
        addCommand(".limit locals 128");

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
        addCommand("return");
        addCommand(".end method");
        return null;
    }

    @Override
    public String visit(OnStartDeclaration onStartDeclaration) {
        // todo

        int old_stack_count = stack_count;
        stack_count = 0;
        Dictionary<String, Integer> old_n2i = name_to_id;
        Dictionary<String, Type> old_n2t = name_to_type;
        name_to_id = new Hashtable<>();
        name_to_type = new Hashtable<>();

        addCommand(".method public static OnStart(LTrade;)V"); // check the document
        addCommand(".limit stack 128");
        addCommand(".limit locals 128");

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
        addCommand("return");
        addCommand(".end method");
        return null;
    }

    @Override
    public String visit(ExpressionStmt expressionStmt) {
        // todo
        Expression exp = expressionStmt.getExpression();
        // // if (exp instanceof MethodCall)
        // System.out.println(exp.getClass());
        exp.accept(this);

        return null;
    }

    @Override
    public String visit(BinaryExpression binExp) { // expression should return it's value instead of addcommanding it
        Expression lexp = binExp.getLeft();
        Expression rexp = binExp.getRight();

        if (binExp.getBinaryOperator().equals(BinaryOperator.AND)) { // short circuit implementation
            lexp.accept(this);
            addCommand("ifeq Label_False_" + label_count);
            rexp.accept(this);
            addCommand("ifeq Label_False_" + label_count);
            addCommand("iconst_1");
            addCommand("goto Label_" + label_count + "_End");
            addCommand("Label_False_" + label_count + ":");
            addCommand("iconst_0");
            addCommand("Label_" + label_count + "_End:");
            label_count++;
            return null;
        } else if (binExp.getBinaryOperator().equals(BinaryOperator.OR)) {
            lexp.accept(this);
            addCommand("ifeq Label_True_" + label_count);
            rexp.accept(this);
            addCommand("ifeq Label_True_" + label_count);
            addCommand("iconst_0");
            addCommand("goto Label_" + label_count + "_End");
            addCommand("Label_True_" + label_count + ":");
            addCommand("iconst_1");
            addCommand("Label_" + label_count + "_End:");
            label_count++;
            return null;
        }

        lexp.accept(this);
        rexp.accept(this);

        Type type;
        try {
            type = rexp.accept(expressionTypeChecker);
        } catch (Exception e) {
            type = name_to_type.get(((Identifier) rexp).getName());
        }

        if (binExp.getBinaryOperator().equals(BinaryOperator.PLUS)) {
            if (type instanceof IntType)
                addCommand("iadd");
            else if (type instanceof FloatType)
                addCommand("fadd");
            else
                addCommand(";wrong format" + lexp.toString());
        } else if (binExp.getBinaryOperator().equals(BinaryOperator.MINUS)) {
            if (type instanceof IntType)
                addCommand("isub");
            else if (type instanceof FloatType)
                addCommand("fsub");
            else
                addCommand(";wrong format");
        } else if (binExp.getBinaryOperator().equals(BinaryOperator.MULT)) {
            if (type instanceof IntType)
                addCommand("imul");
            else if (type instanceof FloatType)
                addCommand("fmul");
            else
                addCommand(";wrong format");
        } else if (binExp.getBinaryOperator().equals(BinaryOperator.DIV)) {
            if (type instanceof IntType)
                addCommand("idiv");
            else if (type instanceof FloatType)
                addCommand("fdiv");
            else
                addCommand(";wrong format");
        } else if (binExp.getBinaryOperator().equals(BinaryOperator.MOD)) {
            if (type instanceof IntType)
                addCommand("irem");
            else if (type instanceof FloatType)
                addCommand("frem");
            else
                addCommand(";wrong format");
        } else if (binExp.getBinaryOperator().equals(BinaryOperator.BIT_AND)) {
            if (type instanceof IntType)
                addCommand("iand");
            else
                addCommand(";wrong format");
        } else if (binExp.getBinaryOperator().equals(BinaryOperator.BIT_OR)) {
            if (type instanceof IntType)
                addCommand("ior");
            else
                addCommand(";wrong format");
        } else if (binExp.getBinaryOperator().equals(BinaryOperator.BIT_XOR)) {
            if (type instanceof IntType)
                addCommand("ixor");
            else
                addCommand(";wrong format");
        } else if (binExp.getBinaryOperator().equals(BinaryOperator.L_SHIFT)) {
            if (type instanceof IntType)
                addCommand("ishl");
            else
                addCommand(";wrong format");
        } else if (binExp.getBinaryOperator().equals(BinaryOperator.R_SHIFT)) {
            if (type instanceof IntType)
                addCommand("ishr");
            else
                addCommand(";wrong format");
        }

        // binary ret
        else if (binExp.getBinaryOperator().equals(BinaryOperator.LT)) {
            addCommand("if_icmplt Label_" + label_count);
            addCommand("iconst_0");
            addCommand("goto Label_" + label_count + "_End");
            addCommand("Label_" + label_count + ":");
            addCommand("iconst_1");
            addCommand("Label_" + label_count + "_End:");
            label_count++;
        } else if (binExp.getBinaryOperator().equals(BinaryOperator.GT)) {

            addCommand("if_icmpgt Label_" + label_count);
            addCommand("iconst_0");
            addCommand("goto Label_" + label_count + "_End:");
            addCommand("Label_" + label_count + ":");
            addCommand("iconst_1");
            addCommand("Label_" + label_count + "_End:");
            label_count++;
        } else if (binExp.getBinaryOperator().equals(BinaryOperator.EQ)) {

            addCommand("if_icmpeq Label_" + label_count);
            addCommand("iconst_0");
            addCommand("goto Label_" + label_count + "_End:");
            addCommand("Label_" + label_count + ":");
            addCommand("iconst_1");
            addCommand("Label_" + label_count + "_End:");
            label_count++;
        } else if (binExp.getBinaryOperator().equals(BinaryOperator.NEQ)) {

            addCommand("if_icmpneq Label_" + label_count);
            addCommand("iconst_0");
            addCommand("goto Label_" + label_count + "_End:");
            addCommand("Label_" + label_count + ":");
            addCommand("iconst_1");
            addCommand("Label_" + label_count + "_End:");
            label_count++;
        }

        return null;
    }

    @Override
    public String visit(UnaryExpression unExp) {
        String name = ((Identifier) unExp.getOperand()).getName();
        unExp.getOperand().accept(this);
        Type type = name_to_type.get(name);
        if (unExp.getUnaryOperator().equals(UnaryOperator.INC)) {
            if (type instanceof IntType) {
                addCommand("iinc");
            } else {
                addCommand(";wrong format");
            }
        } else if (unExp.getUnaryOperator().equals(UnaryOperator.DEC)) {
            if (type instanceof IntType) {
                addCommand("iconst_1");
                addCommand("isub");
            } else {
                addCommand(";wrong format");
            }
        } else if (unExp.getUnaryOperator().equals(UnaryOperator.NOT)) {
            addCommand("ifeq Label_" + label_count);
            addCommand("pop");
            addCommand("iconst_0");
            addCommand("goto Label_" + label_count + "_End");
            addCommand("Label_" + label_count + ":");
            addCommand("pop");
            addCommand("iconst_1");
            addCommand("Label_" + label_count + "_End:");
            label_count++;
        } else if (unExp.getUnaryOperator().equals(UnaryOperator.MINUS)) {
            if (type instanceof IntType) {
                addCommand("ineg");
            } else if (type instanceof FloatType) {
                addCommand("fneg");
            } else {
                addCommand(";wrong format");
            }
        } else if (unExp.getUnaryOperator().equals(UnaryOperator.BIT_NOT)) {
            addCommand("ineg");
        }
        return null;
    }

    @Override
    public String visit(VarDeclaration varDeclaration) {
        // todo

        name_to_id.put(varDeclaration.getIdentifier().getName(), stack_count);
        stack_count++;
        name_to_type.put(varDeclaration.getIdentifier().getName(), varDeclaration.getType());

        if (root_n2t == name_to_type) {
            addField(varDeclaration.getIdentifier().getName(), makeTypeSignature(varDeclaration.getType()));
            fields.put(varDeclaration.getIdentifier().getName(), varDeclaration.getType());
            return null;
        }

        if (varDeclaration.getType() instanceof OrderType) {
            addCommand("new Order");
            addCommand("dup");
        }

        // if (varDeclaration.getType() instanceof TradeType)
        // {
        // addCommand("new Trade");
        // addCommand("dup");
        // }

        if (varDeclaration.getRValue() != null) { // arrays unhandled
            varDeclaration.getRValue().accept(this);
            if (varDeclaration.getType() instanceof IntType || varDeclaration.getType() instanceof BoolType) {
                int id = name_to_id.get(varDeclaration.getIdentifier().getName());
                if (id <= 3)
                    addCommand("istore_" + id);
                else
                    addCommand("istore " + id);

            } else if (varDeclaration.getType() instanceof FloatType)

            {
                int id = name_to_id.get(varDeclaration.getIdentifier().getName());
                if (id <= 3)
                    addCommand("fstore_" + name_to_id.get(varDeclaration.getIdentifier().getName()));
                else
                    addCommand("fstore " + name_to_id.get(varDeclaration.getIdentifier().getName()));

            } else {
                int id = name_to_id.get(varDeclaration.getIdentifier().getName());
                if (id <= 3)
                    addCommand("astore_" + name_to_id.get(varDeclaration.getIdentifier().getName()));
                else
                    addCommand("astore " + name_to_id.get(varDeclaration.getIdentifier().getName()));

            }
        }
        return null;
    }

    @Override
    public String visit(AssignStmt assignmentStmt) {
        // todo
        // assignmentStmt.getLValue().accept(this);
        Identifier id = (Identifier) assignmentStmt.getLValue();

        // cannot assign with operators due to incomplete grammar
        int ID = -1;
        
        try{
            ID = name_to_id.get(id.getName());
        }
        catch (Exception e) {
            Type type = fields.get(id.getName());
                
            addCommand("aload_0");
            assignmentStmt.getRValue().accept(this);
            addCommand("putfield " + id.getName() + " " + makeTypeSignature(type));
            return null;
        }

        assignmentStmt.getRValue().accept(this);

        if (name_to_type.get(id.getName()) instanceof IntType || name_to_type.get(id.getName()) instanceof BoolType)
            if (ID > 3)
                addCommand("istore " + name_to_id.get(id.getName()));
            else
                addCommand("istore_" + name_to_id.get(id.getName()));

        else if (name_to_type.get(id.getName()) instanceof FloatType)
            if (ID > 3)
                addCommand("fstore " + name_to_id.get(id.getName()));
            else
                addCommand("fstore_" + name_to_id.get(id.getName()));

        else if (ID > 3)
            addCommand("astore " + name_to_id.get(id.getName()));
        else
            addCommand("astore_" + name_to_id.get(id.getName()));

        return null;
    }

    // @Override
    // public String visit(BlockStmt blockStmt) {
    // //todo
    // return null;
    // }

    @Override
    public String visit(IfElseStmt conditionalStmt) {
        String command = "";
        Expression cond = conditionalStmt.getCondition();
        cond.accept(this);
        String else_label_name = "Label_" + label_count;
        addCommand("ifeq " + else_label_name);
        for (Statement stmt : conditionalStmt.getThenBody()) {
            stmt.accept(this);
        }
        addCommand("goto Label_" + label_count + "_End");
        addCommand(else_label_name);
        for (Statement stmt : conditionalStmt.getElseBody()) {
            stmt.accept(this);
        }
        addCommand("Label_" + label_count + "_End:");
        label_count++;
        return command;
    }

    @Override
    public String visit(WhileStmt while_stmt) {
        String command = "";
        Expression cond = while_stmt.getCondition();
        String cond_label = "Label_Cond_" + label_count;
        String end_label = "Label_" + label_count + "_End";
        // command = command.concat(cond_label+":\n");
        addCommand(cond_label + ":");
        String cond_code = cond.accept(this);
        // command = command.concat(cond_code+"\n");
        // addCommand(cond_code);
        // command = command.concat("ifeq "+ end_label);
        addCommand("ifeq " + end_label);

        for (Statement stmt : while_stmt.getBody()) {
            command = command.concat(stmt.accept(this) + "\n");

        }
        // command = command.concat("goto " + cond_label);
        addCommand("goto " + cond_label);
        // command = command.concat(end_label+":\n");
        addCommand(end_label + ":");
        label_count++;
        // addCommand(command);
        return command;
    }

    @Override
    public String visit(VarAccess methodCallStmt) {
        // todo
        // invoke

        methodCallStmt.getInstance().accept(this);

        String ret_type = "";
        String args = "";
        ret_type = "V";

        addCommand("invokevirtual LTrade/" + methodCallStmt.getVariable().getName() + "(" + args + ")" + ret_type);
        return null;
    }

    @Override
    public String visit(FunctionCall funcCall) {
        // todo
        // invoke
        for (Expression arg : funcCall.getArgs())
            arg.accept(this);

        String ret_type = "";
        String args = "";
        if (funcList.get(funcCall.getFunctionName().getName()) != null) {
            ret_type = makeTypeSignature(funcList.get(funcCall.getFunctionName().getName()).get(0));
            for (Type argType : funcList.get(funcCall.getFunctionName().getName()).subList(1,
                    funcList.get(funcCall.getFunctionName().getName()).size()))
                args = args.concat(makeTypeSignature(argType) + ";");
        } else { // maybe predefined ??
            for (Expression arg : funcCall.getArgs()) {
                try {
                    args = args.concat(makeTypeSignature(arg.accept(expressionTypeChecker)) + ";");
                } catch (Exception e) {
                    args = args.concat(makeTypeSignature(name_to_type.get(((Identifier) arg).getName())) + ";");
                }
            }

            if (funcCall.getFunctionName().getName().equals("Order"))
                ret_type = "Order";
            else
                ret_type = "V";
            // args = "?";
        }

        if (funcCall.getFunctionName().getName().equals("Order"))
            addCommand("invokespecial Program/" + funcCall.getFunctionName().getName() + "(" + args + ")" + ret_type);
        else if (funcCall.getFunctionName().getName().equals("Observe") || funcCall.getFunctionName().getName().equals("Connect"))
            addCommand("invokestatic Program/" + funcCall.getFunctionName().getName() + "(" + args + ")" + ret_type);
        else
            addCommand("invokevirtual Program/" + funcCall.getFunctionName().getName() + "(" + args + ")" + ret_type);
        return null;
    }

    // @Override
    // public String visit(PrintStmt print) {
    // //todo
    // return null;
    // }

    @Override
    public String visit(ReturnStmt returnStmt) {
        Type type = returnStmt.getReturnedExpr().accept(expressionTypeChecker);
        if (type instanceof NullType) {
            addCommand("return");
        } else {
            // load the return exp ??
            returnStmt.getReturnedExpr().accept(this);
            if (type instanceof IntType || type instanceof BoolType)
                addCommand("ireturn");
            else if (type instanceof FloatType)
                addCommand("freturn");
            else
                addCommand("areturn");
        }
        return null;
    }

    @Override
    public String visit(Identifier id) {
        String commands = "";
        // todo
        try {
            Type type = name_to_type.get(id.getName());
            if (type == null)
            {
                type = fields.get(id.getName());
                if (type == null) return null;
                addCommand("aload_0");
                addCommand("getfield " + id.getName() + " " + makeTypeSignature(type));
                return null;
            }
            if (type instanceof NullType) {
                addCommand("load" + name_to_id.get(id.getName()));
            } 
            else {
                int ID = name_to_id.get(id.getName());
                if (type instanceof IntType || type instanceof BoolType)
                    if (ID > 3)
                        addCommand("iload " + name_to_id.get(id.getName()));
                    else
                        addCommand("iload_" + name_to_id.get(id.getName()));

                else if (type instanceof FloatType)
                    if (ID > 3)
                        addCommand("fload " + name_to_id.get(id.getName()));
                    else
                        addCommand("fload_" + name_to_id.get(id.getName()));

                else
                    if (ID >3)
                        addCommand("aload " + name_to_id.get(id.getName()));
                    else
                        addCommand("aload_" + name_to_id.get(id.getName()));
            }

        } catch (Exception e) { }
        // addCommand("load " + name_to_id.get(id.getName()) + id.getName());
        return commands;
    }

    @Override
    public String visit(TradeValue trade) {
        String commands = "";
        // todo
        if (trade.getConstant().equals("SELL"))
            addCommand("ldc \"SELL\"");
        else if (trade.getConstant().equals("BUY"))
            addCommand("ldc \"BUY\"");
        else
            addCommand("aload " + name_to_id.get(trade.getConstant()));
        return commands;
    }

    @Override
    public String visit(NullValue nullValue) {
        String commands = "";
        // todo
        System.out.println("null");
        return commands;
    }

    @Override
    public String visit(IntValue intValue) {
        String commands = "";
        if (intValue.getConstant() <= 3)
            commands = commands.concat("iconst_" + intValue.getConstant());
        else
            commands = commands.concat("bipush " + intValue.getConstant());

        addCommand(commands);
        // System.out.println("int");
        return commands;
    }

    @Override
    public String visit(BoolValue boolValue) {
        // todo
        String command;
        if (boolValue.getConstant())
            command = "iconst_1";
        else
            command = "iconst_0";
        addCommand(command);

        return command;
    }

    @Override
    public String visit(StringValue stringValue) {
        String commands = "";
        // todo
        commands = "ldc " + stringValue.getConstant();
        addCommand(commands);
        // addCommand("astore " + stack_count);
        // stack_count++;

        System.out.println("string");
        return commands;
    }

}