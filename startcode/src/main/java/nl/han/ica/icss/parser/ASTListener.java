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
//		stack.push(ast.root);
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
		ASTNode node = stack.pop();
		if (!(node instanceof Stylerule)) {
			throw new RuntimeException("Expected Stylerule but got: " + node.getClass());
		}
		Stylerule rule = (Stylerule) node;
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
		// DIT IS DE ENIGE PLEK VOOR VARIABELEN EN BOOLEANS
		if (ctx.CAPITAL_IDENT() != null) {
			stack.push(new VariableReference(ctx.getText()));
		} else if (ctx.TRUE() != null) {
			stack.push(new BoolLiteral(true));
		} else if (ctx.FALSE() != null) {
			stack.push(new BoolLiteral(false));
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
	public void enterIfblock(ICSSParser.IfblockContext ctx) {
		IfClause ifClause = new IfClause();
		stack.push(ifClause);
	}

	@Override
	public void exitIfblock(ICSSParser.IfblockContext ctx) {

		ASTNode top = stack.pop();
		if (top instanceof Expression) {
			Expression condition = (Expression) top;
			IfClause ifNode = (IfClause) stack.pop();
			ifNode.conditionalExpression = condition;
			stack.peek().addChild(ifNode);
		} else if (top instanceof IfClause) {
		}
	}

	@Override
	public void enterElseblock(ICSSParser.ElseblockContext ctx) {

	}

	@Override
	public void exitElseblock(ICSSParser.ElseblockContext ctx) {

	}
}