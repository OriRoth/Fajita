package il.ac.technion.cs.fling.grammars;

import static il.ac.technion.cs.fling.util.RunDPDA.run;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.BNF;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
@SuppressWarnings("static-method") public class LL1Test {
  @Test public void testArithemeticalExpression() {
    final var bnf = BNF.of(v("E")).//
        derive(v("E")).to(v("T"), v("E'")). // E --> TE'
        derive(v("E'")).to(t("+"), v("T"), v("E'")). // E' --> +TE'
        derive(v("E'")).to(). // E'--> e
        derive(v("T")).to(v("F"), v("T'")). // T --> FT' T' --> *FT' | e F -->
                                            // id | (E)
        derive(v("T'")).to(t("*"), v("F"), v("T'")). // T --> FT' T' --> *FT' |
                                                     // e F --> id | (E)
        derive(v("T'")).to(). // T' --> | e F --> id | (E)
        derive(v("F")).to(t("id")). //
        derive(v("F")).to(t("("), v("E"), t(")")). //
        build();
    final var dpda = LL1.buildAutomaton(bnf);
    assertThat(run(dpda)).isFalse();
    assertThat(run(dpda, w("id"))).isTrue();
    assertThat(run(dpda, w("+"))).isFalse();
    assertThat(run(dpda, w("id +"))).isFalse();
    assertThat(run(dpda, w("id + id"))).isTrue();
    assertThat(run(dpda, w("id + id * ( id + ( id ) )"))).isTrue();
    assertThat(run(dpda, w("id + id * ( id + ( id )"))).isFalse();
  }
  @Test public void testJump() {
    var bnf = BNF.of(v("S")). //
        derive(v("S")).to(v("A"), t("x")).derive(v("A")).to(t("a"), v("A"), v("B")). //
        derive(v("A")).to(). //
        derive(v("B")).to(t("b")). //
        derive(v("B")).to(). //
        build();
    var dpda = LL1.buildAutomaton(bnf);
    assertThat(run(dpda)).isFalse();
    assertThat(run(dpda, w("x"))).isTrue();
    assertThat(run(dpda, w("a x"))).isTrue();
    assertThat(run(dpda, w("a a b b x"))).isTrue();
    assertThat(run(dpda, w("a a a a x"))).isTrue();
    assertThat(run(dpda, w("a a a a b x"))).isTrue();
    assertThat(run(dpda, w("a a b b b x"))).isFalse();
  }
  private static Variable v(final String s) {
    return Variable.byName(s);
  }
  private static Token t(final String s) {
    return Token.of(s);
  }
  private static Token[] w(final String w) {
    return Arrays.stream(w.split(" ")).map(LL1Test::t).toArray(Token[]::new);
  }
}
