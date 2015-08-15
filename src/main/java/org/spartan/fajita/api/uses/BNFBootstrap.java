package org.spartan.fajita.api.uses;

import static org.spartan.fajita.api.uses.BNFBootstrap.NT.*;
import static org.spartan.fajita.api.uses.BNFBootstrap.Term.*;

import org.spartan.fajita.api.bnf.BNF;
import org.spartan.fajita.api.bnf.BNFBuilder;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class BNFBootstrap {
    public static void expressionBuilder() {

	// showASTs();
    }

    static enum Term implements Terminal {
	setApiName(String.class), derive(NonTerminal.class), to(NonTerminal.class), //
	and(Symbol.class), toOneOf(NonTerminal.class), or(NonTerminal.class), //
	finish;

	private final Class<?>[] type;

	private Term(final Class<?>... type) {
	    this.type = type;
	}

	private Term() {
	    type = new Class<?>[] { Void.class };
	}

	@Override
	public Class<?>[] type() {
	    return type;
	}

    }

    static enum NT implements NonTerminal {
	S, NAME_OPT, NAME, RULE, RULE_TYPE, ABSTRACT_RULE, //
	NEXT_ABSTRACT, OR, NORMAL_RULE, NEXT_NORMAL, //
	AND, NEXT, NEXT_RULE, FINISH;
    }

    // TODO: fix bootstrap.
    public static void buildBNF() {
	BNF b = new BNFBuilder(Term.class, NT.class) //
		.startConfig() //
		.setApiNameTo("BNF Bootstrap") //
		.setStartSymbols(S) //
		.overload(to).with(Terminal.class) //
		.endConfig() //
		//
		.derive(S).to(NAME_OPT).and(RULE).and(NEXT) //
		.derive(NAME_OPT).to(NAME).or(NonTerminal.EPSILON) //
		.derive(NAME).to(setApiName) //
		.derive(RULE).to(derive).and(RULE_TYPE) //
		.derive(RULE_TYPE).to(ABSTRACT_RULE).or(NORMAL_RULE) //
		.derive(ABSTRACT_RULE).to(to).and(NEXT_ABSTRACT) //
		.derive(NEXT_ABSTRACT).to(OR).or(NonTerminal.EPSILON) //
		.derive(OR).to(or).and(NEXT_ABSTRACT) //
		.derive(NORMAL_RULE).to(to).and(NEXT_NORMAL) //
		.derive(NEXT_NORMAL).to(AND).or(NonTerminal.EPSILON) //
		.derive(AND).to(and).and(NEXT_NORMAL) //
		.derive(NEXT).to(NEXT_RULE).or(FINISH) //
		.derive(NEXT_RULE).to(RULE).and(NEXT) //
		.derive(FINISH).to(finish) //
		.finish();

	System.out.println(b);
    }

    public static void main(final String[] args) {
	buildBNF();
	expressionBuilder();
    }
}
