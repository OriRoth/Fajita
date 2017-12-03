package org.spartan.fajita.api.junk;

public class DatalogAST {
  public static class RULE {
  }

  public static class LITERAL {
    java.lang.String name;
    java.lang.String[] terms;
  }

  public static class BODY {
    org.spartan.fajita.api.junk.DatalogAST.LITERAL[] body;
  }

  public static class S {
    org.spartan.fajita.api.junk.DatalogAST.RULE[] s1;
  }

  public static class RULE$2 extends RULE {
    org.spartan.fajita.api.junk.DatalogAST.LITERAL head;
    org.spartan.fajita.api.junk.DatalogAST.BODY body;
  }

  public static class RULE$1 extends RULE {
    org.spartan.fajita.api.junk.DatalogAST.LITERAL fact;
  }
}