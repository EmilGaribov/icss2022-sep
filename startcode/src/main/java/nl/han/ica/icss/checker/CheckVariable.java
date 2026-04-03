package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.SymbolTable;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.types.ExpressionType;

public class CheckVariable {
    private final SymbolTable symbolTable;
    private final CheckExpression checkExpression;

    public CheckVariable(SymbolTable symbolTable, CheckExpression checkExpression) {
        this.symbolTable = symbolTable;
        this.checkExpression = checkExpression;
    }

    public void checkVariableAssignment(VariableAssignment node) {
        ExpressionType type = checkExpression.checkExpression(node.expression);
        // Sla het op in de tabel zodat CheckExpression het kan vinden
        symbolTable.add(node.name.name, type);
    }

    public ExpressionType checkVariableReference(VariableReference ref) {
        ExpressionType type = symbolTable.get(ref.name);
        if (type == null) {
            ref.setError("Variabele '" + ref.name + "' is niet gedefinieerd.");
            return ExpressionType.UNDEFINED;
        }
        return type;
    }
}
