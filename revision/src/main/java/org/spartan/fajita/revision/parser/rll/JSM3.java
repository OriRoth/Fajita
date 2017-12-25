package org.spartan.fajita.revision.parser.rll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.spartan.fajita.revision.bnf.BNF;
import org.spartan.fajita.revision.parser.ll.BNFAnalyzer;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Verb;

public class JSM3 implements Cloneable {
  public static final JSM3 JAMMED = new JSM3();
  public static final JSM3 UNKNOWN = new JSM3();
  final BNF bnf;
  final BNFAnalyzer analyzer;
  private final Stack<Symbol> S0;
  private final Stack<Map<Verb, J>> S1;

  public JSM3(BNF bnf) {
    this(bnf, new BNFAnalyzer(bnf));
  }
  public JSM3(BNF bnf, BNFAnalyzer analyzer) {
    this.bnf = bnf;
    this.analyzer = analyzer;
    this.S0 = new Stack<>();
    this.S1 = new Stack<>();
  }
  public JSM3(BNF bnf, BNFAnalyzer analyzer, Symbol initial, List<Verb> baseLegalJumps) {
    this(bnf, analyzer);
    pushJumps(initial);
    S0.push(initial);
    for (Verb v : baseLegalJumps)
      if (S1.peek().get(v) == J.JJAMMED)
        S1.peek().put(v, J.JUNKNOWN);
  }
  private JSM3(JSM3 jsm) {
    this.bnf = jsm.bnf;
    this.analyzer = jsm.analyzer;
    this.S0 = new Stack<>();
    this.S1 = new Stack<>();
    this.S0.addAll(jsm.S0);
    for (Map<Verb, J> m : jsm.S1)
      this.S1.add(new HashMap<>(m));
  }
  private JSM3() {
    this.bnf = null;
    this.analyzer = null;
    this.S0 = null;
    this.S1 = null;
  }
  @Override public JSM3 clone() {
    return new JSM3(this);
  }
  public List<Symbol> getS0() {
    return new ArrayList<>(S0);
  }
  public Symbol peek() {
    return S0.peek();
  }
  public JSM3 pop() {
    JSM3 $ = clone();
    $.S0.pop();
    $.S1.pop();
    return $;
  }
  public JSM3 jump(Verb v) {
    if (S1.isEmpty())
      return JAMMED;
    J j = S1.peek().get(v);
    if (j == J.JJAMMED)
      return JAMMED;
    if (j == J.JUNKNOWN)
      return UNKNOWN;
    JSM3 $ = j.address;
    $ = $.pushAll(j.toPush);
    return $;
  }
  public JSM3 jumpReminder(Verb v) {
    if (S1.isEmpty())
      return JAMMED;
    J j = S1.peek().get(v);
    if (j == J.JJAMMED)
      return JAMMED;
    if (j == J.JUNKNOWN)
      return UNKNOWN;
    if (j.toPush.isEmpty())
      return new JSM3(bnf, analyzer);
    JSM3 $ = new JSM3(bnf, analyzer, j.toPush.get(0), legalJumps());
    $ = $.pushAll(j.toPush.subList(1, j.toPush.size()));
    return $;
  }
  public JSM3 pushAll(List<Symbol> items) {
    if (items.isEmpty())
      return clone();
    JSM3 $ = clone();
    $.pushJumps(items);
    return $;
  }
  public JSM3 pushAllReminder(List<Symbol> items) {
    if (items.isEmpty())
      return new JSM3(bnf, analyzer);
    if (isEmpty()) {
      JSM3 $ = new JSM3(bnf, analyzer);
      $.pushJumps(items);
      return $;
    }
    JSM3 $ = new JSM3(bnf, analyzer, items.get(0), legalJumps());
    $.pushJumps(items.subList(1, items.size()));
    return $;
  }
  private void pushJumps(List<Symbol> items) {
    for (Symbol s : items) {
      pushJumps(s);
      S0.push(s);
    }
  }
  private void pushJumps(Symbol s) {
    Map<Verb, J> m = S1.isEmpty() ? emptyMap() : new HashMap<>(S1.peek());
    if (s.isVerb()) {
      m.put(s.asVerb(), J.of(clone()));
      S1.push(m);
      return;
    }
    NonTerminal nt = s.asNonTerminal();
    for (Verb v : bnf.verbs) {
      List<Symbol> c = analyzer.llClosure(nt, v);
      if (c == null) {
        if (!analyzer.followSetOf(nt).contains(v))
          m.put(v, J.JJAMMED);
      } else {
        J j = J.of(clone(), c);
        m.put(v, j);
        List<Symbol> l = getS0();
        l.addAll(c);
      }
    }
    S1.push(m);
  }
  // TODO Roth: can be optimized
  public List<Verb> legalJumps() {
    assert this != JAMMED && this != UNKNOWN && !isEmpty();
    return new LinkedList<>(
        bnf.verbs.stream().filter(v -> !analyzer.firstSetOf(peek()).contains(v) && jump(v) != JAMMED).collect(Collectors.toList()));
  }
  public JSM3 trim() {
    return this == JAMMED || this == UNKNOWN || isEmpty() ? this
        : new JSM3(bnf, analyzer, peek(),
            new LinkedList<>(bnf.verbs.stream().filter(v -> jump(v) != JAMMED).collect(Collectors.toList())));
  }
  private Map<Verb, J> emptyMap() {
    Map<Verb, J> $ = new HashMap<>();
    for (Verb v : bnf.verbs)
      $.put(v, J.JJAMMED);
    return $;
  }
  @Override public int hashCode() {
    return S0 == null ? 1 : S0.hashCode();
  }
  @Override public boolean equals(Object obj) {
    return obj instanceof JSM3 && S0.equals(((JSM3) obj).S0);
  }
  @Override public String toString() {
    return this == JAMMED ? "JAMMED" : this == UNKNOWN ? "UNKNOWN" : toString(0, null, new HashSet<>(), new ArrayList<>());
  }
  String toString(int ind, Verb v, Set<J> seen, List<Symbol> toPush) {
    seen.add(J.of(this));
    StringBuilder $ = new StringBuilder();
    for (int i = 0; i < ind; ++i)
      $.append(" ");
    if (v != null)
      $.append(v).append(": ");
    if (this == JAMMED || this == UNKNOWN)
      return $.append(toString()).append(" + ").append(toPush).append("\n").toString();
    $.append(super.toString()).append(" {\n");
    for (int i = 0; i < ind; ++i)
      $.append(" ");
    $.append(" S0: ").append(S0);
    if (!toPush.isEmpty())
      $.append(" + ").append(toPush);
    $.append("\n");
    for (int i = 0; i < ind; ++i)
      $.append(" ");
    $.append(" S1: (").append(S1.size()).append(")\n");
    if (!S1.isEmpty())
      for (Verb x : bnf.verbs) {
        J j = S1.peek().get(x);
        if (j == J.JJAMMED) {
          for (int i = 0; i < ind; ++i)
            $.append(" ");
          $.append("  ").append(x).append(": JAMMED\n");
        } else if (j == J.JUNKNOWN) {
          for (int i = 0; i < ind; ++i)
            $.append(" ");
          $.append("  ").append(x).append(": UNKNOWN\n");
        } else {
          if (!seen.contains(j))
            $.append(j.address.toString(ind + 2, x, seen, j.toPush));
          else {
            for (int i = 0; i < ind; ++i)
              $.append(" ");
            $.append("  ").append(x).append(": ").append(j.address.id()).append(" + ").append(j.toPush).append("\n");
          }
        }
      }
    for (int i = 0; i < ind; ++i)
      $.append(" ");
    return $.append("}\n").toString();
  }
  private String id() {
    return super.toString();
  }
  public boolean isEmpty() {
    return S0.isEmpty();
  }

  private static class J {
    static final J JJAMMED = new J();
    static final J JUNKNOWN = new J();
    final JSM3 address;
    final List<Symbol> toPush;

    J(JSM3 address, List<Symbol> toPush) {
      this.address = address;
      this.toPush = toPush;
    }
    J() {
      address = null;
      toPush = null;
    }
    static J of(JSM3 address, List<Symbol> toPush) {
      return new J(address, toPush);
    }
    static J of(JSM3 address) {
      return address == JAMMED ? JJAMMED : address == UNKNOWN ? JUNKNOWN : of(address, new ArrayList<>());
    }
    @Override public int hashCode() {
      int $ = 1;
      if (this == JJAMMED || this == JUNKNOWN)
        return $;
      $ += 31 * address.hashCode();
      $ += 17 * toPush.hashCode();
      return $;
    }
    @Override public boolean equals(Object obj) {
      return obj == JJAMMED ? this == JJAMMED
          : obj == JUNKNOWN ? this == JUNKNOWN
              : obj instanceof J && address.equals(((J) obj).address) && toPush.equals(((J) obj).toPush);
    }
    @Override public String toString() {
      return this == JJAMMED ? "JJAMMED" : this == JUNKNOWN ? "JUNKNOWN" : address.toString(0, null, new HashSet<>(), toPush);
    }
  }
}
