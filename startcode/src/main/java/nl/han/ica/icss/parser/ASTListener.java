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
		// root in doen
	}

	// =====================
	// ROOT

//	@Override
//	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
//		Stylesheet stylesheet = new Stylesheet();
//		stack.push(stylesheet);
//	}
//
//	@Override
//	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
//		ast.setRoot((Stylesheet) stack.pop());
//	}

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
		ASTNode parent = stack.peek(); // Stylesheet
		parent.addChild(rule);
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
		Declaration decl = new Declaration(ctx.prop().getText());

		Expression value = createValue(ctx.value());
		decl.expression = value;

		Stylerule rule = (Stylerule) stack.peek();
		rule.addChild(decl);
	}

	private Expression createValue(ICSSParser.ValueContext ctx) {

		if (ctx.COLOR() != null) return new ColorLiteral(ctx.COLOR().getText());
		if (ctx.PIXELSIZE() != null) return new PixelLiteral(ctx.PIXELSIZE().getText());
		if (ctx.PERCENTAGE() != null) return new PercentageLiteral(ctx.PERCENTAGE().getText());
		if (ctx.SCALAR() != null) return new ScalarLiteral(ctx.SCALAR().getText());
		if (ctx.TRUE() != null) return new BoolLiteral(true);
		if (ctx.FALSE() != null) return new BoolLiteral(false);
		if (ctx.CAPITAL_IDENT() != null) return new VariableReference(ctx.CAPITAL_IDENT().getText());


		throw new RuntimeException("Unknown value: " + ctx.getText());
	}

	// =====================
	// LITERALS

	@Override
	public void exitLite(ICSSParser.LiteContext ctx) {
		if (ctx.COLOR() != null) {
			stack.push(new ColorLiteral(ctx.getText()));
		} else if (ctx.PIXELSIZE() != null) {
			stack.push(new PixelLiteral(ctx.getText()));
		} else if (ctx.PERCENTAGE() != null) {
			stack.push(new PercentageLiteral(ctx.getText()));
		} else if (ctx.SCALAR() != null) {
			stack.push(new ScalarLiteral(ctx.getText()));
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
}