package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.SymbolTable;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;

public class Checker {

    private SymbolTable symbolTable;
    private CheckExpression checkExpression;
    private CheckVariable checkVariable;
    private CheckIfClause checkIfClause;

    public Checker() {
        this.symbolTable = new SymbolTable();
        this.checkExpression = new CheckExpression();
        this.checkVariable = new CheckVariable(symbolTable, checkExpression);
        this.checkIfClause = new CheckIfClause(checkExpression);
        this.checkExpression.setCheckVariable(checkVariable);
    }

    public void check(AST ast) {
        checkNode(ast.root);
    }

    private void checkNode(ASTNode node) {
        if (node instanceof Stylesheet || node instanceof Stylerule || node instanceof IfClause) {
            symbolTable.pushScope();
        }
        if (node instanceof VariableAssignment) {
            checkVariable.checkVariableAssignment((VariableAssignment) node);
        } else if (node instanceof IfClause) {
            checkIfClause.check((IfClause) node);
        }
        if (node instanceof Declaration) {
            checkDeclaration((Declaration) node);
        }
        for (ASTNode child : node.getChildren()) {
            checkNode(child);
        }
        if (node instanceof Stylesheet || node instanceof Stylerule || node instanceof IfClause) {
            symbolTable.popScope();
        }
    }

    private void checkDeclaration(Declaration decl) {
        ExpressionType valueType = checkExpression.checkExpression(decl.expression);

        if (valueType == ExpressionType.UNDEFINED) return;

        if (valueType == ExpressionType.SCALAR) {
            decl.setError("Een getal zonder eenheid (px of %) is niet toegestaan voor een property.");
            return;
        }

        String propertyName = decl.property.name;
        if (propertyName.equals("color") && valueType != ExpressionType.COLOR) {
            decl.setError("Property '" + propertyName + "' verwacht een kleur.");
        }
    }
}