package org.spartan.fajita.api.examples.binaryExpressions;

import java.util.ArrayList;

import org.spartan.fajita.api.ast.Atomic;
import org.spartan.fajita.api.ast.Compound;
import org.spartan.fajita.api.ast.InheritedNonterminal;

public class BinaryExpressions {
  // 1)for each NT that has an inheritence rule we need an empty interface
  interface Expression {
    //
  }

  // 2)and and empty abstract class for compound
  static abstract class CompoundExpression extends Compound implements Expression {
    CompoundExpression(final Expr parent) {
      super(parent);
      parent.deriveTo(this);
    }
    @Override public Expr getParent() {
      return (Expr) super.getParent();
    }
  }

  // 3) and a node for that NT . it will always be the parent of the derived
  // nodes
  public static class Expr extends InheritedNonterminal implements Expression {
    Expr(final Compound parent) {
      super(parent);
    }
    Expr() {
      super(null);
    }
    // adding methods for all terminals in FIRST(S) for "flat" compilation
    public Not not() {
      return new Not(this);
    }
    public Literal bool(final boolean b) {
      return new Literal(this, b);
    }
    @Override public String getName() {
      return "EXPRESSION";
    }
  }

  public static class Or extends CompoundExpression {
    // for top-down
    Or(final Expr parent) {
      super(parent);
    }
    // for bottom-up
    Or(final Expr parent, final CompoundExpression e1, final CompoundExpression e2) {
      super(parent);
      ((Expr) getChild(0)).deriveTo(e1);
      ((Expr) getChild(2)).deriveTo(e2);
    }
    @Override public ArrayList<Compound> getChildren() {
      ArrayList<Compound> $ = new ArrayList<>();
      $.add(new Expr(this));
      $.add(new OrTerm(this));
      $.add(new Expr(this));
      return $;
    }
    @Override public String getName() {
      return "OR";
    }
  }

  public static class And extends CompoundExpression {
    // for top down
    And(final Expr parent) {
      super(parent);
    }
    // for bottom up
    And(final Expr parent, final CompoundExpression e1, final CompoundExpression e2) {
      super(parent);
      ((Expr) getChild(0)).deriveTo(e1);
      ((Expr) getChild(2)).deriveTo(e2);
    }
    @Override public ArrayList<Compound> getChildren() {
      ArrayList<Compound> $ = new ArrayList<>();
      $.add(new Expr(this));
      $.add(new AndTerm(this));
      $.add(new Expr(this));
      return $;
    }
    @Override public String getName() {
      return "AND";
    }
  }

  // whoever inherits from Expr need to get the parent Expr in
  // constructor and call deriveTo(this)
  public static class Not extends CompoundExpression {
    // for top down
    Not(final Expr parent) {
      super(parent);
      parent.deriveTo(this);
    }
    // for bottom up
    Not(final Expr parent, final CompoundExpression e) {
      super(parent);
      ((Expr) getChild(1)).deriveTo(e);
    }
    @Override public ArrayList<Compound> getChildren() {
      ArrayList<Compound> $ = new ArrayList<>();
      $.add(new NotTerm(this));
      $.add(new Expr(this));
      return $;
    }
    public Literal bool(final boolean b) {
      return new Literal((Expr) getChild(1), b);
    }
    public Not not() {
      return new Not((Expr) getChild(1));
    }
    @Override public String getName() {
      return "NOT";
    }
  }

  public static class Literal extends CompoundExpression {
    Literal(final Expr parent, final boolean b) {
      super(parent);
      ((BoolTerm) getChild(0)).setValue(b);
      parent.deriveTo(this);
    }
    @Override public ArrayList<Compound> getChildren() {
      ArrayList<Compound> $ = new ArrayList<>();
      $.add(new BoolTerm(this));
      return $;
    }
    // adding methods for all terminals in FIRST(S) for "flat" compilation
    public Expr and() {
      if (getParent() == null)
        setParent(new Expr());
      // Expr->AND and not Expr->LITERAL. fix the parent Expr
      And and = new And(getParent());
      ((Expr) and.getChild(0)).deriveTo(this);
      return (Expr) and.getChild(2);
    }
    public Expr or() {
      if (getParent() == null)
        setParent(new Expr());
      // Expr->OR and not Expr->LITERAL. fix the parent Expr
      Or or = new Or(getParent());
      ((Expr) or.getChild(0)).deriveTo(this);
      return (Expr) or.getChild(2);
    }
    @Override public String getName() {
      return "LITERAL";
    }
  }

  public static class OrTerm extends Atomic {
    OrTerm(final Compound parent) {
      super(parent);
    }
    @Override public String getName() {
      return "or";
    }
  }

  public static class AndTerm extends Atomic {
    AndTerm(final Compound parent) {
      super(parent);
    }
    @Override public String getName() {
      return "and";
    }
  }

  public static class NotTerm extends Atomic {
    NotTerm(final Compound parent) {
      super(parent);
    }
    @Override public String getName() {
      return "not";
    }
  }

  public static class BoolTerm extends Atomic {
    private boolean value;

    BoolTerm(final Compound parent) {
      super(parent);
    }
    @Override public String getName() {
      return "boolean";
    }
    public boolean getValue() {
      return value;
    }
    public void setValue(final boolean value) {
      this.value = value;
    }
    @Override public String toString() {
      return super.toString() + " = " + value;
    }
  }

  public static Literal bool(final boolean b) {
    return new Literal(new Expr(), b);
  }
  public static Or or(final CompoundExpression e1, final CompoundExpression e2) {
    return new Or(new Expr(), e1, e2);
  }
  public static And and(final CompoundExpression e1, final CompoundExpression e2) {
    return new And(new Expr(), e1, e2);
  }
  public static Not not(final CompoundExpression e) {
    return new Not(new Expr(), e);
  }
  public static Not not() {
    return new Not(new Expr());
  }
}