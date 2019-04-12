package fling.examples.languages;

import static fling.examples.languages.SubFigure.V.*;
import static fling.examples.languages.SubFigure.Σ.*;
import static fling.grammars.api.BNFAPI.bnf;
import static fling.internal.grammar.sententials.Notation.oneOrMore;

import fling.adapters.JavaMediator;
import fling.grammars.BNF;
import fling.internal.grammar.sententials.*;

public class SubFigure {
  public enum Σ implements Terminal {
    load, row, column, seal
  }

  public enum V implements Variable {
    Figure, Orientation
  }

  public static final BNF bnf = bnf(). //
      start(Figure). //
      derive(Figure).to(load.with(String.class)). //
      derive(Figure).to(Orientation, oneOrMore(Figure), seal). //
      derive(Orientation).to(row). //
      derive(Orientation).to(column). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "fling.examples.generated", "SubFigure", Σ.class);
}
