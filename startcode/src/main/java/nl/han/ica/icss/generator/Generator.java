package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;

public class Generator {

	public String generate(AST ast) {
		StringBuilder sb = new StringBuilder();

		for (ASTNode node : ast.root.getChildren()) {
			if (node instanceof Stylerule) {
				sb.append(generateStylerule((Stylerule) node));
			}
		}

		return sb.toString();
	}

	private String generateStylerule(Stylerule rule) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < rule.selectors.size(); i++) {
			sb.append(rule.selectors.get(i).toString());
			if (i < rule.selectors.size() - 1) {
				sb.append(", ");
			}
		}

		sb.append(" {\n");

		for (ASTNode node : rule.body) {
			if (node instanceof Declaration) {
				sb.append(generateDeclaration((Declaration) node));
			}
		}

		sb.append("}\n\n");
		return sb.toString();
	}

	private String generateDeclaration(Declaration dec) {
		return "  " + dec.property.name + ": " + valueToString(dec.expression) + ";\n";
	}

	private String valueToString(Expression expr) {
		if (expr instanceof PixelLiteral) {
			return ((PixelLiteral) expr).value + "px";
		}
		if (expr instanceof PercentageLiteral) {
			return ((PercentageLiteral) expr).value + "%";
		}
		if (expr instanceof ColorLiteral) {
			return ((ColorLiteral) expr).value;
		}
		return "";
	}
}