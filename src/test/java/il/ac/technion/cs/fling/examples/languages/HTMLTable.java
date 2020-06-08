package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.HTMLTable.V.*;
import static il.ac.technion.cs.fling.examples.languages.HTMLTable.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;
import static il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers.noneOrMore;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.languages.HTMLTable.*;
import il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;

public class HTMLTable implements FluentLanguageAPI<Σ, V> {
  public enum Σ implements Terminal {
    html, table, tr, th, td, end, ¢
  }

  public enum V implements Variable {
    HTML, Table, Header, Row, Tr, Th, Td, Cell
  }

  @Override public Class<Σ> Σ() {
    return Σ.class;
  }

  @Override public Class<V> V() {
    return V.class;
  }

  @Override public il.ac.technion.cs.fling.FancyEBNF BNF() {
    return bnf(). //
        start(HTML). //
        derive(HTML).to(html.with(String.class), Table). //
        derive(Table).to(table.many(String.class), Header, Quantifiers.noneOrMore(Row), end). //
        derive(Header).to(Tr, Quantifiers.noneOrMore(Th), end). //
        derive(Row).to(Tr, Quantifiers.noneOrMore(Td), end). //
        derive(Tr).to(tr.many(String.class)). //
        derive(Th).to(th.many(String.class), ¢.with(String.class), end). //
        derive(Td).to(td.many(String.class), Cell, end). //
        derive(Cell).to(¢.with(String.class)).or(Table). //
        build();
  }
}
