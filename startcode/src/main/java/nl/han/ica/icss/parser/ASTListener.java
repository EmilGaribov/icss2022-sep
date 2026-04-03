package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.ast.selectors.*;

public class ASTListener extends ICSSBaseListener {

	private final AST ast;
	private final IHANStack<ASTNode> stack;

	public ASTListener() {
		ast = new AST();
		stack = new HANStack<>();
	}

	public AST getAST() {
		return ast;
	}

	private void addNode(ASTNode node) {
			stack.peek().addChild(node);
	}

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = new Stylesheet();
		ast.setRoot(stylesheet);
		stack.push(stylesheet);
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		stack.pop();
	}

	// =====================
	// RULESETS & ASSIGNMENTS
	// =====================

	@Override
	public void enterCssrules(ICSSParser.CssrulesContext ctx) {
		Stylerule rule = new Stylerule();
		addNode(rule);
		stack.push(rule);
	}

	@Override
	public void exitCssrules(ICSSParser.CssrulesContext ctx) {
		stack.pop();
	}

	@Override
	public void enterSelecty(ICSSParser.SelectyContext ctx) {
		Selector selector;
		if (ctx.LOWER_IDENT() != null) selector = new TagSelector(ctx.getText());
		else if (ctx.ID_IDENT() != null) selector = new IdSelector(ctx.getText());
		else selector = new ClassSelector(ctx.getText());

		addNode(selector);
	}

	@Override
	public void enterLlcsrules(ICSSParser.LlcsrulesContext ctx) {
		VariableAssignment assignment = new VariableAssignment();
		// De naam is een VariableReference (kind van de assignment)
		assignment.addChild(new VariableReference(ctx.CAPITAL_IDENT().getText()));

		addNode(assignment);
		stack.push(assignment);
	}

	@Override
	public void exitLlcsrules(ICSSParser.LlcsrulesContext ctx) {
		stack.pop();
	}

	@Override
	public void enterDeclare(ICSSParser.DeclareContext ctx) {
		Declaration decl = new Declaration(ctx.prop().getText());
		addNode(decl);
		stack.push(decl);
	}

	@Override
	public void exitDeclare(ICSSParser.DeclareContext ctx) {
		stack.pop();
	}

	// =====================
	// EXPRESSIES & OPERATIES
	// =====================

	@Override
	public void enterExpression(ICSSParser.ExpressionContext ctx) {
		if (ctx.getChildCount() == 3 && ctx.expression().size() == 2) {
			Operation op;
			String operator = ctx.getChild(1).getText();
			switch (operator) {
				case "*": op = new MultiplyOperation(); break;
				case "+": op = new AddOperation(); break;
				case "-": op = new SubtractOperation(); break;
				case ">":  op = new GreaterThanOperation(); break;
				case "<":  op = new LessThanOperation(); break;
				case "==": op = new EqualityOperation(); break;
				case "&&": op = new AndOperation(); break;
				case "||": op = new OrOperation(); break;
				default: return;
			}
			addNode(op);
			stack.push(op);
		}
	}

	@Override
	public void exitExpression(ICSSParser.ExpressionContext ctx) {
		// Alleen poppen als een operatie is gepusht
		if (ctx.getChildCount() == 3 && ctx.expression().size() == 2) {
			stack.pop();
		}
	}

	// =====================
	// LITERALS & VARIABLES
	// =====================

	@Override
	public void enterLiteral(ICSSParser.LiteralContext ctx) {
		ASTNode literal = null;
		if (ctx.COLOR() != null) literal = new ColorLiteral(ctx.getText());
		else if (ctx.PIXELSIZE() != null) literal = new PixelLiteral(ctx.getText());
		else if (ctx.PERCENTAGE() != null) literal = new PercentageLiteral(ctx.getText());
		else if (ctx.SCALAR() != null) literal = new ScalarLiteral(ctx.getText());
		else if (ctx.TRUE() != null) literal = new BoolLiteral(true);
		else if (ctx.FALSE() != null) literal = new BoolLiteral(false);

		if (literal != null) addNode(literal);
	}

	@Override
	public void enterVariable(ICSSParser.VariableContext ctx) {
		addNode(new VariableReference(ctx.getText()));
	}

	// =====================
	// IF / ELSE
	// =====================

	@Override
	public void enterIfblock(ICSSParser.IfblockContext ctx) {
		IfClause ifNode = new IfClause();
		addNode(ifNode);
		stack.push(ifNode);
	}

	@Override
	public void exitIfblock(ICSSParser.IfblockContext ctx) {
		IfClause ifNode = (IfClause) stack.pop();
		if (!ifNode.getChildren().isEmpty() && ifNode.getChildren().get(0) instanceof Expression) {
			ifNode.conditionalExpression = (Expression) ifNode.getChildren().remove(0);
		}
	}

	@Override
	public void enterElseblock(ICSSParser.ElseblockContext ctx) {
		ElseClause elseNode = new ElseClause();
		ASTNode parent = stack.peek();
		int lastIndex = parent.getChildren().size() - 1;

		if (lastIndex >= 0 && parent.getChildren().get(lastIndex) instanceof IfClause) {
			IfClause lastIf = (IfClause) parent.getChildren().get(lastIndex);
			lastIf.elseClause = elseNode;
		}
		stack.push(elseNode);
	}

	@Override
	public void exitElseblock(ICSSParser.ElseblockContext ctx) {
		stack.pop();
	}
}