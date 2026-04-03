package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.ast.types.ExpressionType;

public class CheckOperation {
    private final CheckExpression checkExpression;

    public CheckOperation(CheckExpression checkExpression){
        this.checkExpression = checkExpression;
    }

    public ExpressionType check(Operation operation) {
        ExpressionType left = checkExpression.checkExpression(operation.lhs);
        ExpressionType right = checkExpression.checkExpression(operation.rhs);

        if (operation instanceof AddOperation || operation instanceof SubtractOperation || operation instanceof MultiplyOperation) {
            if (left == ExpressionType.COLOR || right == ExpressionType.COLOR ||
                    left == ExpressionType.BOOL || right == ExpressionType.BOOL) {
                operation.setError("Je mag niet rekenen met kleuren of booleans.");
                return ExpressionType.UNDEFINED;
            }

            if (operation instanceof MultiplyOperation) {
                if (left != ExpressionType.SCALAR && right != ExpressionType.SCALAR) {
                    operation.setError("Bij vermenigvuldigen moet minimaal één waarde een getal (scalar) zijn.");
                    return ExpressionType.UNDEFINED;
                }
                return (left == ExpressionType.SCALAR) ? right : left;
            } else {
                if (left != right) {
                    operation.setError("Types moeten gelijk zijn voor optellen/aftrekken.");
                    return ExpressionType.UNDEFINED;
                }
                return left;
            }
        }

        if (operation instanceof GreaterThanOperation || operation instanceof LessThanOperation || operation instanceof EqualityOperation) {
            if (left != right) {
                operation.setError("Je kunt alleen gelijke types vergelijken.");
                return ExpressionType.UNDEFINED;
            }
            return ExpressionType.BOOL;
        }

        if (operation instanceof AndOperation || operation instanceof OrOperation) {
            if (left != ExpressionType.BOOL || right != ExpressionType.BOOL) {
                operation.setError("&& en || werken alleen met booleans.");
                return ExpressionType.UNDEFINED;
            }
            return ExpressionType.BOOL;
        }

        return ExpressionType.UNDEFINED;
    }
}

