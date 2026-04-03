package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.IfClause;
import nl.han.ica.icss.ast.types.ExpressionType;

public class CheckIfClause {
    private final CheckExpression checkExpression;

    public CheckIfClause(CheckExpression checkExpression) {
        this.checkExpression = checkExpression;
    }

    public void check(IfClause ifClause) {
        ExpressionType conditionType = checkExpression.checkExpression(ifClause.conditionalExpression);

        if (conditionType != ExpressionType.BOOL) {
            ifClause.setError("De conditie in een if-statement moet een boolean zijn (TRUE of FALSE).");
        }
    }
}