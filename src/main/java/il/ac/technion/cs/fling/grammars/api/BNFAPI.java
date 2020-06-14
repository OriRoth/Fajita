package il.ac.technion.cs.fling.grammars.api;

import static il.ac.technion.cs.fling.grammars.api.BNFAPICompiler.parse_PlainBNF;

import java.util.ArrayList;
import java.util.List;

import il.ac.technion.cs.fling.EBNF;
import il.ac.technion.cs.fling.FancyEBNF;
import il.ac.technion.cs.fling.PlainBNFImporter;
import il.ac.technion.cs.fling.internal.compiler.Invocation;
import il.ac.technion.cs.fling.internal.grammar.rules.TempComponent;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;

/** {@link FancyEBNF} builder API. Initially generated by Fling.
 *
 * @author Ori Roth */
@SuppressWarnings("all") public interface BNFAPI {
  public enum Σ implements Terminal {
    bnf, start, derive, specialize, to, into, toEpsilon, or, orNone
  }

  public enum Γ implements Variable {
    PlainBNF, Rule, RuleBody, RuleTail
  }

  /** Start specifying grammar.
   *
   * @return BNF builder API */
  static q0ø__Rule1$start_q0$q0ø<q0$_$_q0$<$>, $> bnf() {
    final α α = new α();
    α.w.add(new Invocation(Σ.bnf));
    return α;
  }

  interface $ {
    EBNF build();
  }

  interface q0$_$_q0$<q0$> extends $ {
  }

  interface q0ø__Rule1$start_q0$q0ø<q0$, q0ø> {
    q0$__Rule1$_q0$q0ø<q0$, q0ø> start(Variable variable);
  }

  interface q0$__Rule1$_q0$q0ø<q0$, q0ø> extends $ {
    q0ø__Rule1$RuleBody$_q0$q0ø<q0$, q0ø> derive(Variable variable);

    q0ø__Rule1$into_q0$q0ø<q0$, q0ø> specialize(Variable variable);
  }

  interface q0ø__Rule1$RuleBody$_q0$q0ø<q0$, q0ø> {
    q0$__RuleTail1$_derivespecializeq0$q0ø<q0ø__Rule1$RuleBody$_q0$q0ø<q0$, q0ø>, q0ø__Rule1$into_q0$q0ø<q0$, q0ø>, q0$__Rule1$_q0$q0ø<q0$, q0ø>, q0$__Rule1$_q0$q0ø<q0$, q0ø>> to(
        TempComponent... symbols);

    q0$__Rule1$_q0$q0ø<q0$, q0ø> toEpsilon();
  }

  interface q0ø__Rule1$into_q0$q0ø<q0$, q0ø> {
    q0$__Rule1$_q0$q0ø<q0$, q0ø> into(Variable... variables);
  }

  interface q0$__RuleTail1$_derivespecializeq0$q0ø<derive, specialize, q0$, q0ø> extends $ {
    derive derive(Variable variable);

    specialize specialize(Variable variable);

    q0$__RuleTail1$_derivespecializeq0$q0ø<derive, specialize, q0$, q0ø> or(TempComponent... symbols);

    q0$__RuleTail1$_derivespecializeq0$q0ø<derive, specialize, q0$, q0ø> orNone();
  }

  static class α implements $, q0$_$_q0$, q0ø__Rule1$start_q0$q0ø, q0$__Rule1$_q0$q0ø, q0ø__Rule1$RuleBody$_q0$q0ø,
      q0ø__Rule1$into_q0$q0ø, q0$__RuleTail1$_derivespecializeq0$q0ø {
    public List<il.ac.technion.cs.fling.internal.compiler.Invocation> w = new ArrayList<>();

    public α bnf() {
      this.w.add(new Invocation(Σ.bnf, new Object[] {}));
      return self();
    }

    @Override public α start(final Variable variable) {
      this.w.add(new Invocation(Σ.start, new Object[] { variable }));
      return self();
    }

    @Override public α derive(final Variable variable) {
      this.w.add(new Invocation(Σ.derive, new Object[] { variable }));
      return self();
    }

    @Override public α specialize(final Variable variable) {
      this.w.add(new Invocation(Σ.specialize, new Object[] { variable }));
      return self();
    }

    @Override public α into(final Variable... variables) {
      this.w.add(new Invocation(Σ.into, new Object[] { variables }));
      return self();
    }

    @Override public α to(final TempComponent... symbols) {
      this.w.add(new Invocation(Σ.to, new Object[] { symbols }));
      return self();
    }

    @Override public α toEpsilon() {
      this.w.add(new Invocation(Σ.toEpsilon, new Object[] {}));
      return self();
    }

    public α self() {
      return this;
    }

    @Override public α or(final TempComponent... symbols) {
      this.w.add(new Invocation(Σ.or, new Object[] { symbols }));
      return self();
    }

    @Override public α orNone() {
      this.w.add(new Invocation(Σ.orNone, new Object[] {}));
      return self();
    }

    @Override public EBNF build() {
      return new PlainBNFImporter(parse_PlainBNF(w)).getEbnf();
    }
  }
}
