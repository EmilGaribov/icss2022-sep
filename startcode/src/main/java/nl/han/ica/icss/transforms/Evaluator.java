package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.ast.operations.OrOperation;

import java.util.ArrayList;
import java.util.HashMap;


public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();
        variableValues.addFirst(new HashMap<>()); // Globale scope

        evaluate(ast.root);
    }

    private Literal calculateExpression(Expression expr) {
        if (expr instanceof Literal) {
            return (Literal) expr;
        }

        if (expr instanceof VariableReference) {
            String name = ((VariableReference) expr).name;
            for (int i = 0; i < variableValues.getSize(); i++) {
                HashMap<String, Literal> scope = variableValues.get(i);
                if (scope.containsKey(name)) {
                    return scope.get(name);
                }
            }
        }

        if (expr instanceof Operation) {
            Literal left = calculateExpression(((Operation) expr).lhs);
            Literal right = calculateExpression(((Operation) expr).rhs);

            if (expr instanceof AddOperation) {
                return add(left, right);
            } else if (expr instanceof SubtractOperation) {
                return subtract(left, right);
            } else if (expr instanceof MultiplyOperation) {
                return multiply(left, right);
            }

            else if (expr instanceof GreaterThanOperation) {
                return greaterThan(left, right);
            } else if (expr instanceof LessThanOperation) {
                return lessThan(left, right);
            } else if (expr instanceof EqualityOperation) {
                return equality(left, right);
            }

            else if (expr instanceof AndOperation) {
                return and(left, right);
            }
            else if (expr instanceof OrOperation) {
                return or(left, right);
            }
        }
        return null;
    }

    private Literal subtract(Literal left, Literal right) {
        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value - ((PixelLiteral) right).value);
        }
        if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value - ((PercentageLiteral) right).value);
        }
        return null;
    }

    private Literal multiply(Literal left, Literal right) {
        int valL = getLiteralValue(left);
        int valR = getLiteralValue(right);
        int res = valL * valR;

        if (left instanceof PixelLiteral || right instanceof PixelLiteral) {
            return new PixelLiteral(res);
        } else if (left instanceof PercentageLiteral || right instanceof PercentageLiteral) {
            return new PercentageLiteral(res);
        }
        return new ScalarLiteral(res);
    }

    private int getLiteralValue(Literal l) {
        if (l instanceof PixelLiteral) return ((PixelLiteral) l).value;
        if (l instanceof PercentageLiteral) return ((PercentageLiteral) l).value;
        if (l instanceof ScalarLiteral) return ((ScalarLiteral) l).value;
        return 0;
    }

    private Literal add(Literal left, Literal right) {
        if (left instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value + ((PixelLiteral) right).value);
        }
        if (left instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value + ((PercentageLiteral) right).value);
        }
        return null;
    }

    private Literal greaterThan(Literal left, Literal right) {
        int valL = getLiteralValue(left);
        int valR = getLiteralValue(right);
        return new BoolLiteral(valL > valR);
    }
    private Literal lessThan(Literal left, Literal right) {
        int valL = getLiteralValue(left);
        int valR = getLiteralValue(right);
        return new BoolLiteral(valL < valR);
    }

    private Literal equality(Literal left, Literal right) {
        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            return new BoolLiteral(((PixelLiteral) left).value == ((PixelLiteral) right).value);
        }
        if (left instanceof BoolLiteral && right instanceof BoolLiteral) {
            return new BoolLiteral(((BoolLiteral) left).value == ((BoolLiteral) right).value);
        }
        return new BoolLiteral(false);
    }

    private Literal and(Literal left, Literal right) {
        if (left instanceof BoolLiteral && right instanceof BoolLiteral) {
            return new BoolLiteral(((BoolLiteral) left).value && ((BoolLiteral) right).value);
        }
        return new BoolLiteral(false);
    }

    private Literal or(Literal left, Literal right) {
        if (left instanceof BoolLiteral && right instanceof BoolLiteral) {
            return new BoolLiteral(((BoolLiteral) left).value && ((BoolLiteral) right).value);
        }
        return new BoolLiteral(false);
    }

    private void updateNodeChildren(ASTNode node, ArrayList<ASTNode> newChildren) {
        if (node instanceof Stylesheet) {
            Stylesheet stylesheet = (Stylesheet) node;
            stylesheet.getChildren().clear();
            for (ASTNode child : newChildren) {
                stylesheet.addChild(child);
            }
        } else if (node instanceof Stylerule) {
            Stylerule stylerule = (Stylerule) node;
            stylerule.body.clear();
            for (ASTNode child : newChildren) {
                stylerule.body.add(child);
            }
        } else if (node instanceof ElseClause) {
            ElseClause elseClause = (ElseClause) node;
            elseClause.body.clear();
            for (ASTNode child : newChildren) {
                elseClause.body.add(child);
            }
        }
    }

    private void evaluate(ASTNode node) {
        ArrayList<ASTNode> children = node.getChildren();
        ArrayList<ASTNode> processedChildren = new ArrayList<>();

        for (ASTNode child : children) {
            if (child instanceof VariableAssignment) {
                Literal value = calculateExpression(((VariableAssignment) child).expression);
                variableValues.get(0).put(((VariableAssignment) child).name.name, value);

            } else if (child instanceof Declaration) {
                ((Declaration) child).expression = calculateExpression(((Declaration) child).expression);
                processedChildren.add(child);

            } else if (child instanceof IfClause) {
                BoolLiteral cond = (BoolLiteral) calculateExpression(((IfClause) child).conditionalExpression);

                if (cond.value) {
                    evaluate(child);
                    processedChildren.addAll(((IfClause) child).body);
                } else if (((IfClause) child).elseClause != null) {
                    evaluate(((IfClause) child).elseClause);
                    processedChildren.addAll(((IfClause) child).elseClause.body);
                }

            } else if (child instanceof Stylerule || child instanceof Stylesheet) {
                if (child instanceof Stylerule) {
                    variableValues.addFirst(new HashMap<>());
                }
                evaluate(child);
                if (child instanceof Stylerule) {
                    variableValues.removeFirst();
                }
                processedChildren.add(child);
            }
        }
        updateNodeChildren(node, processedChildren);
    }
}

    

