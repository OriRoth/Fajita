package org.spartan.fajita.revision.export;

import org.spartan.fajita.revision.bnf.EBNF;
import org.spartan.fajita.revision.parser.ell.ELLRecognizer;
import org.spartan.fajita.revision.parser.ell.Interpretation;
import org.spartan.fajita.revision.symbols.Terminal;

public class FluentAPIRecorder {
  public final ELLRecognizer ell;

  public FluentAPIRecorder(EBNF ebnf) {
    this.ell = new ELLRecognizer(ebnf);
  }
  public void recordTerminal(Terminal t, Object... args) {
    ell.consume(new RuntimeVerb(t, args));
  }
  @Override public String toString() {
    return conclude().toString();
  }
  public String toString(int ident) {
    return conclude().toString(ident);
  }
  public void fold() {
    conclude();
  }
  public Interpretation conclude() {
    return ell.ast();
  }
  public <S> S ast(String astPath) {
    return new ASTBuilder(conclude(), astPath).build();
  }
}
