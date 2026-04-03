package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.SymbolTable;
import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

public class CheckExpression {
    private final CheckOperation checkOperation;
    private CheckVariable checkVariable; // Om variabelen op te zoeken

    public CheckExpression() {
        this.checkOperation = new CheckOperation(this);
    }

    public void setCheckVariable(CheckVariable checkVariable) {
        this.checkVariable = checkVariable;
    }

    public ExpressionType checkExpression(Expression expr) {
        if (expr instanceof ColorLiteral) return ExpressionType.COLOR;
        if (expr instanceof PixelLiteral) return ExpressionType.PIXEL;
        if (expr instanceof PercentageLiteral) return ExpressionType.PERCENTAGE;
        if (expr instanceof ScalarLiteral) return ExpressionType.SCALAR;
        if (expr instanceof BoolLiteral) return ExpressionType.BOOL;

        if (expr instanceof VariableReference) {
            return this.checkVariable.checkVariableReference((VariableReference) expr);
            }
        if (expr instanceof Operation) {
            return this.checkOperation.check((Operation) expr);
        }

        return ExpressionType.UNDEFINED;
    }
}
