package main.ast.node.expression.values;

import main.visitor.IVisitor;
import main.ast.node.expression.values.Value;
public class NullValue extends Value {
    private int constant;

    public NullValue() {
        this.constant = 0; //changed
    }

    public void getConstant() {
        return;
    }

//    public void setConstant(boolean constant) {
//        this.constant = constant;
//    }

    @Override
    public String toString(){
        return "NullValue";

    }
    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
