package org.spartan.fajita.api.bnf.rules;

import java.util.ArrayList;
import java.util.List;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;

public class DerivationRule extends Rule {

    public final List<Symbol> expression;

    public DerivationRule(final NonTerminal lhs, final List<Symbol> expression, final int index) {
	super(lhs, index);
	this.expression = new ArrayList<>(expression);
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder(lhs.toString2() + " ::= ");
	for (Symbol symb : expression)
	    sb.append(symb.toString2() + " ");
	return sb.toString();
    }

    @Override
    public List<Symbol> getChildren() {
	return new ArrayList<>(expression);
    }
}