package il.ac.technion.cs.fling.internal.grammar;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;
import static il.ac.technion.cs.fling.internal.grammar.BNFUtilsTest.Γ.X;
import static il.ac.technion.cs.fling.internal.grammar.BNFUtilsTest.Γ.Y;
import static il.ac.technion.cs.fling.internal.grammar.BNFUtilsTest.Σ.a;
import static il.ac.technion.cs.fling.internal.grammar.BNFUtilsTest.Σ.b;
import static il.ac.technion.cs.fling.internal.grammar.BNFUtilsTest.Σ.c;
import static il.ac.technion.cs.fling.internal.grammar.BNFUtilsTest.Σ.d;
import static il.ac.technion.cs.fling.internal.grammar.BNFUtilsTest.Σ.e;
import static il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers.noneOrMore;
import static il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers.oneOrMore;
import static il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers.optional;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import il.ac.technion.cs.fling.EBNF;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
class BNFUtilsTest {
  public enum Σ implements Terminal {
    a, b, c, d, e
  }
  public enum Γ implements Variable {
    X, Y
  }
  static class Q implements FluentLanguageAPI<Σ, Γ> {
    @Override public Class<Σ> Σ() {
      return Σ.class;
    }
    @Override public Class<Γ> Γ() {
      return Γ.class;
    }
    @Override public EBNF BNF() {
      return bnf(). //
          start(X). //
          derive(X).to(oneOrMore(a.with(int.class)), //
              noneOrMore(b.with(int.class)), //
              optional(Y), //
              optional(e.with(int.class)))
          . //
          derive(Y).to(c.with(int.class)). //
          or(d.with(int.class)). //
          build();
    }
  }
  static class Q1 implements FluentLanguageAPI<Σ, Γ> {
    @Override public Class<Σ> Σ() {
      return Σ.class;
    }
    @Override public Class<Γ> Γ() {
      return Γ.class;
    }
    @Override public EBNF BNF() {
      return bnf(). //
          start(X). //
          derive(X).to(a, oneOrMore(b.with(byte.class)), c).derive(X).to(c, b, a).build();
    }
  }
  @Test void test() {
    EBNF x = new Q().BNF();
    final SoftAssertions softly = new SoftAssertions();
    softly.assertThat(x).isNotNull();
    softly.assertThat(x.Γ).contains(Γ.X);
    softly.assertThat(x.Γ).contains(Γ.Y);
    softly.assertThat(x.Σ).contains(Token.of(Σ.a).with(int.class));
    softly.assertThat(x.Σ).contains(Token.of(Σ.b).with(int.class));
    softly.assertThat(x.Σ).doesNotContain(Token.of(Σ.d).with(byte.class));
    softly.assertThat(x.Σ).doesNotContain(Token.of(Σ.d));
    softly.assertAll();
    System.out.println(x);
    System.out.println(FancyEBNF.from(x));
    System.out.println(BNFUtils.normalize(FancyEBNF.from(x)));
    System.out.println(BNFUtils.normalize(FancyEBNF.from(x)));
    System.out.println(BNFUtils.normalize(BNFUtils.getBNF(FancyEBNF.from(x))));
  }
  @Test public void test1() {
    EBNF x = new Q1().BNF();
    System.out.println(x);
    System.out.println(FancyEBNF.from(x));
    System.out.println(BNFUtils.normalize(FancyEBNF.from(x)));
    System.out.println(BNFUtils.normalize(FancyEBNF.from(x)));
    System.out.println(BNFUtils.normalize(BNFUtils.getBNF(FancyEBNF.from(x))));
  }
}
