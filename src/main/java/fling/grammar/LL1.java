package fling.grammar;

import static fling.grammar.sententials.Alphabet.ε;
import static fling.util.Collections.reversed;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import fling.automata.DPDA;
import fling.automata.DPDA.δ;
import fling.compiler.Namer;
import fling.grammar.sententials.Constants;
import fling.grammar.sententials.DerivationRule;
import fling.grammar.sententials.Named;
import fling.grammar.sententials.SententialForm;
import fling.grammar.sententials.Symbol;
import fling.grammar.sententials.Variable;
import fling.grammar.sententials.Verb;
import fling.grammar.sententials.Word;

public class LL1 extends Grammar {
  public LL1(BNF bnf, Namer namer) {
    super(bnf, namer);
  }
  @Override public DPDA<Named, Verb, Named> buildAutomaton(BNF bnf) {
    Set<Named> Q = new LinkedHashSet<>();
    Set<Verb> Σ = new LinkedHashSet<>();
    Set<Named> Γ = new LinkedHashSet<>();
    Set<δ<Named, Verb, Named>> δs = new LinkedHashSet<>();
    Set<Named> F = new LinkedHashSet<>();
    Named q0;
    Word<Named> γ0;
    Σ.addAll(bnf.Σ);
    Σ.remove(Constants.$$);
    Q.addAll(Σ);
    Named q0$ = Named.by("q0$"), q0ø = Named.by("q0ø"), qT = Named.by("qT");
    Q.add(q0$);
    Q.add(q0ø);
    Q.add(qT);
    Γ.addAll(Σ);
    Γ.add(Constants.$$);
    Γ.addAll(bnf.V);
    assert Γ.contains(Constants.S);
    Set<Named> A = bnf.V.stream().filter(v -> isNullable(bnf, v)).map(this::getAcceptingVariable).collect(toSet());
    Γ.addAll(A);
    F.add(q0$);
    q0 = q0$;
    γ0 = getPossiblyAcceptingVariables(bnf, new SententialForm(Constants.$$, Constants.S), true);
    /* Computing automaton transitions for q0ø */
    // Moving from q0ø to q0$ with ε + $.
    δs.add(new δ<>(q0ø, ε(), Constants.$$, q0$, new Word<>()));
    // Moving from q0ø to q0$ with ε + accepting variable.
    for (Named v : A)
      δs.add(new δ<>(q0ø, ε(), v, q0$, new Word<>(v)));
    // Moving from q0ø to qσ with σ + appropriate variable.
    for (DerivationRule r : bnf.R)
      for (SententialForm sf : r.rhs)
        for (Verb σ : bnf.firsts(sf))
          if (!Constants.$$.equals(σ))
            δs.add(new δ<>(q0ø, σ, r.lhs, σ, reversed(getPossiblyAcceptingVariables(bnf, sf, false))));
    for (Variable v : bnf.V)
      for (Verb σ : bnf.Σ)
        if (!Constants.$$.equals(σ) && !bnf.firsts(v).contains(σ) && isNullable(bnf, v))
          δs.add(new δ<>(q0ø, σ, v, σ, new Word<>()));
    // Moving from q0ø to q0ø with σ + σ.
    for (Verb σ : bnf.Σ)
      if (!Constants.$$.equals(σ))
        δs.add(new δ<>(q0ø, σ, σ, q0ø, new Word<>()));
    // Get stuck in q0ø with σ + inappropriate variable.
    /* Computing automaton transitions for q0$ */
    // Moving from q0$ to q0ø with ε + non-accepting variable/terminal.
    for (Named v : bnf.V)
      δs.add(new δ<>(q0$, ε(), v, q0ø, new Word<>(v)));
    for (Verb σ : bnf.Σ)
      if (!Constants.$$.equals(σ))
        δs.add(new δ<>(q0$, ε(), σ, q0ø, new Word<>(σ)));
    // Moving from q0$ to qσ with σ + appropriate variable.
    for (DerivationRule r : bnf.R)
      for (SententialForm sf : r.rhs)
        for (Verb σ : bnf.firsts(sf))
          if (!Constants.$$.equals(σ))
            δs.add(new δ<>(q0$, σ, getAcceptingVariable(r.lhs), σ, reversed(getPossiblyAcceptingVariables(bnf, sf, true))));
    for (Variable v : bnf.V)
      for (Verb σ : bnf.Σ)
        if (!Constants.$$.equals(σ) && !bnf.firsts(v).contains(σ) && isNullable(bnf, v))
          δs.add(new δ<>(q0$, σ, getAcceptingVariable(v), σ, new Word<>()));
    // Get stuck in q0$ with σ + inappropriate variable.
    /* Computing automaton transitions for qσ */
    // Moving from qσ to q0$ with ε + σ.
    for (Verb σ : bnf.Σ)
      if (!Constants.$$.equals(σ))
        δs.add(new δ<>(σ, ε(), σ, q0$, new Word<>()));
    // Moving from qσ to qσ with ε + appropriate variable.
    for (DerivationRule r : bnf.R)
      for (SententialForm sf : r.rhs)
        for (Verb σ : bnf.firsts(sf))
          if (!Constants.$$.equals(σ)) {
            δs.add(new δ<>(σ, ε(), getAcceptingVariable(r.lhs), σ, reversed(getPossiblyAcceptingVariables(bnf, sf, true))));
            δs.add(new δ<>(σ, ε(), r.lhs, σ, reversed(getPossiblyAcceptingVariables(bnf, sf, false))));
          }
    // Moving from qσ to qT with ε + inappropriate symbol.
    for (Verb σ : bnf.Σ)
      if (!Constants.$$.equals(σ)) {
        Set<Named> legalTops = new HashSet<>();
        legalTops.add(σ);
        for (DerivationRule r : bnf.R)
          for (SententialForm sf : r.rhs)
            if (bnf.firsts(sf).contains(σ)) {
              legalTops.add(r.lhs);
              legalTops.add(getAcceptingVariable(r.lhs));
            }
        for (Named γ : Γ)
          if (!legalTops.contains(γ))
            δs.add(new δ<>(σ, ε(), γ, qT, new Word<>()));
      }
    // Automaton should not stop in qσ.
    // Automaton gets stuck after reaching qT.
    return new DPDA<>(Q, Σ, Γ, δs, F, q0, γ0);
  }
  @SuppressWarnings("static-method") private boolean isNullable(BNF bnf, Symbol s) {
    return bnf.isNullable(s);
  }
  private Named getAcceptingVariable(Variable v) {
    return Named.by(v.name() + "$");
  }
  private Word<Named> getPossiblyAcceptingVariables(BNF bnf, SententialForm sf, boolean isFromQ0$) {
    List<Named> $ = new ArrayList<>();
    boolean isAccepting = isFromQ0$;
    for (Symbol s : reversed(sf)) {
      $.add(!s.isVariable() || !isAccepting ? s : getAcceptingVariable(s.asVariable()));
      isAccepting &= isNullable(bnf, s);
    }
    return new Word<>(reversed($));
  }
}
