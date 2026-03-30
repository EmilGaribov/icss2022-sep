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
		stack.push(ast.root);
	}

	// =====================
	// ROOT

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = new Stylesheet();
		stack.push(stylesheet);
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = (Stylesheet) stack.pop();
		ast.setRoot(stylesheet);
	}

	@Override
	public void enterStatements(ICSSParser.StatementsContext ctx) {
	}

	@Override
	public void exitStatements(ICSSParser.StatementsContext ctx) {
	}

	public AST getAST() {
		return ast;
	}

	// =====================
	// RULESET

	@Override
	public void enterCssrules(ICSSParser.CssrulesContext ctx) {
		Stylerule rule = new Stylerule();
		stack.push(rule);
	}

	@Override
	public void exitCssrules(ICSSParser.CssrulesContext ctx) {
		Stylerule rule = (Stylerule) stack.pop();
		ASTNode parent = stack.peek();
		parent.addChild(rule);
	}

	@Override
	public void exitLlcsrules(ICSSParser.LlcsrulesContext ctx) {
		Expression val = (Expression) stack.pop();
		VariableAssignment assignment = new VariableAssignment();

		assignment.name = new VariableReference(ctx.CAPITAL_IDENT().getText());
		assignment.expression = val;

		stack.peek().addChild(assignment);
	}

	// =====================
	// SELECTOR

	@Override
	public void exitSelecty(ICSSParser.SelectyContext ctx) {
		Selector selector;

		if (ctx.LOWER_IDENT() != null) {
			selector = new TagSelector(ctx.getText());
		} else if (ctx.ID_IDENT() != null) {
			selector = new IdSelector(ctx.getText());
		} else {
			selector = new ClassSelector(ctx.getText());
		}

		Stylerule rule = (Stylerule) stack.peek();
		rule.addChild(selector);
	}

	// =====================
	// DECLARATION


	@Override
	public void enterDeclare(ICSSParser.DeclareContext ctx) {
	}

	@Override
	public void exitDeclare(ICSSParser.DeclareContext ctx) {
		Expression val = (Expression) stack.pop();
		Declaration decl = new Declaration(ctx.prop().getText());
		decl.expression = val;

		stack.peek().addChild(decl);
	}

	@Override
	public void exitValue(ICSSParser.ValueContext ctx) {
		if (ctx.COLOR() != null) {
			stack.push(new ColorLiteral(ctx.COLOR().getText()));
		} else if (ctx.PIXELSIZE() != null) {
			stack.push(new PixelLiteral(ctx.PIXELSIZE().getText()));
		} else if (ctx.PERCENTAGE() != null) {
			stack.push(new PercentageLiteral(ctx.PERCENTAGE().getText()));
		} else if (ctx.SCALAR() != null) {
			stack.push(new ScalarLiteral(ctx.SCALAR().getText()));
		} else if (ctx.TRUE() != null) {
			stack.push(new BoolLiteral(true));
		} else if (ctx.FALSE() != null) {
			stack.push(new BoolLiteral(false));
		} else if (ctx.CAPITAL_IDENT() != null) {
			stack.push(new VariableReference(ctx.CAPITAL_IDENT().getText()));
		}
	}

	// =====================
	// LITERALS

	@Override
	public void exitLite(ICSSParser.LiteContext ctx) {
		Expression lit = null;
		if (ctx.COLOR() != null) lit = new ColorLiteral(ctx.getText());
		else if (ctx.PIXELSIZE() != null) lit = new PixelLiteral(ctx.getText());
		else if (ctx.PERCENTAGE() != null) lit = new PercentageLiteral(ctx.getText());
		else if (ctx.SCALAR() != null) lit = new ScalarLiteral(ctx.getText());

		if (lit != null) {
			stack.push(lit);
		}
	}

	// =====================
	// VARIABLE REFERENCE

	@Override
	public void exitFacto(ICSSParser.FactoContext ctx) {
		if (ctx.CAPITAL_IDENT() != null) {
			stack.push(new VariableReference(ctx.getText()));
		}
	}

	// =====================
	// EXPRESSION (*)


	@Override
	public void exitTermius(ICSSParser.TermiusContext ctx) {
		if (ctx.getChildCount() == 1) return;

		ASTNode right = stack.pop();
		ASTNode left = stack.pop();

		MultiplyOperation op = new MultiplyOperation();
		op.addChild(left);
		op.addChild(right);

		stack.push(op);
	}

	// =====================
	// EXPRESSIONS (+ -)

	@Override
	public void exitCalcus(ICSSParser.CalcusContext ctx) {
		if (ctx.getChildCount() == 1) return;

		ASTNode right = stack.pop();
		ASTNode left = stack.pop();

		String operator = ctx.getChild(1).getText();

		if (operator.equals("+")) {
			AddOperation op = new AddOperation();
			op.addChild(left);
			op.addChild(right);
			stack.push(op);
		} else {
			SubtractOperation op = new SubtractOperation();
			op.addChild(left);
			op.addChild(right);
			stack.push(op);
		}
	}

	// =====================
	// EXPRESSIONS (IF - ELSE)

	@Override
	public void enterIfexpr(ICSSParser.IfexprContext ctx) {
		IfClause ifClause = new IfClause();
		stack.push(ifClause); // Zet de IF op de stack zodat kinderen (declarations) hierin landen
	}

	@Override
	public void exitIfexpr(ICSSParser.IfexprContext ctx) {
		// Bij het verlaten van de IF staat de laatst gepushte expressie (de conditie)
		// bovenop de IfClause op de stack.

		ASTNode condition = stack.pop(); // De Expression
		ASTNode ifNode = stack.pop();    // De IfClause

		if (ifNode instanceof IfClause) {
			((IfClause) ifNode).conditionalExpression = (Expression) condition;
			stack.peek().addChild(ifNode);
		}
	}

}