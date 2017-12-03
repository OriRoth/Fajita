package org.spartan.fajita.revision.junk;

import java.lang.SuppressWarnings;
import org.spartan.fajita.revision.export.ASTNode;
import org.spartan.fajita.revision.export.FluentAPIRecorder;

@SuppressWarnings("all")
public class RULES {
  public static RULE_1_n1<RULE_1<RULE_1_rec_2bb, RULE_1_n1_rec_207>, RULE_1_n1<RULE_1_rec_2bb, RULE_1_n1_rec_207>> head(ASTNode arg0) {
    $$$ $$$ = new $$$();$$$.recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.head,arg0);return $$$;}

  public static RULE_1<RULE_1<RULE_1_rec_2bb, RULE_1_n1_rec_207>, RULE_1_n1<RULE_1_rec_2bb, RULE_1_n1_rec_207>> fact(ASTNode arg0) {
    $$$ $$$ = new $$$();$$$.recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.fact,arg0);return $$$;}

  public interface RULE_1<fact, head> extends ASTNode {
    fact fact(ASTNode arg0);

    head head(ASTNode arg0);
  }

  public interface RULE_1_n1<fact, head> {
    BODY_1<fact, head> body();
  }

  public interface BODY_1<fact, head> {
    BODY_2<fact, head> literal(ASTNode arg0);
  }

  public interface BODY_2<fact, head> extends ASTNode {
    LITERALS_1<fact, head> literal(ASTNode arg0);

    fact fact(ASTNode arg0);

    head head(ASTNode arg0);
  }

  public interface LITERALS_1<fact, head> extends ASTNode {
    LITERALS_1<fact, head> literal(ASTNode arg0);

    fact fact(ASTNode arg0);

    head head(ASTNode arg0);
  }

  public interface RULE_1_n1_rec_207 {
    BODY_1<RULE_1_rec_2bb, RULE_1_n1_rec_207> body();
  }

  public interface RULE_1_rec_2bb {
    RULE_1_rec_2bb fact(ASTNode arg0);

    RULE_1_n1_rec_207 head(ASTNode arg0);
  }

  private interface ParseError {
  }

  private static class $$$ extends FluentAPIRecorder implements RULE_1, RULE_1_n1, BODY_1, BODY_2, LITERALS_1, RULE_1_n1_rec_207, RULE_1_rec_2bb {
    $$$() {
      super(new org.spartan.fajita.revision.examples.Datalog().bnf().bnf().getSubBNF(org.spartan.fajita.revision.examples.Datalog.NT.RULES));}

    public $$$ fact(ASTNode arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.fact,arg0);return this;}

    public $$$ head(ASTNode arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.head,arg0);return this;}

    public $$$ body() {
      recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.body);return this;}

    public $$$ literal(ASTNode arg0) {
      recordTerminal(org.spartan.fajita.revision.examples.Datalog.Term.literal,arg0);return this;}
  }
}
