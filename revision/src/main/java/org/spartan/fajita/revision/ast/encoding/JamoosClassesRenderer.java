package org.spartan.fajita.revision.ast.encoding;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.spartan.fajita.revision.bnf.EBNF;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;
import org.spartan.fajita.revision.symbols.Verb;
import org.spartan.fajita.revision.symbols.types.ClassType;
import org.spartan.fajita.revision.symbols.types.NestedType;
import org.spartan.fajita.revision.symbols.types.ParameterType;
import org.spartan.fajita.revision.util.DAG;

public class JamoosClassesRenderer {
  EBNF ebnf;
  public String topClassName;
  public final String packagePath;
  DAG.Tree<NonTerminal> inheritance = new DAG.Tree<>();
  private List<String> innerClasses = new LinkedList<>();
  public String topClass;
  public Map<String, Integer> innerClassesUsedNames = new HashMap<>();
  public Map<String, Map<String, Integer>> innerClassesFieldUsedNames = new HashMap<>();
  public Map<String, LinkedHashMap<String, String>> innerClassesFieldTypes = new HashMap<>();

  public JamoosClassesRenderer(EBNF bnf, String packagePath) {
    this.ebnf = bnf;
    this.packagePath = packagePath;
    // NOTE should correspond to the namer in Fajita
    Map<NonTerminal, Integer> counter = an.empty.map();
    Function<NonTerminal, String> namer = lhs -> {
      counter.putIfAbsent(lhs, Integer.valueOf(1));
      return lhs.name() + counter.put(lhs, Integer.valueOf(counter.get(lhs).intValue() + 1));
    };
    parseTopClass(x -> NonTerminal.of(namer.apply(x)));
  }
  private void parseTopClass(Function<NonTerminal, NonTerminal> producer) {
    StringBuilder $ = new StringBuilder();
    $.append("package " + packagePath + ";");
    $.append("public class " + (topClassName = ebnf.name + "AST") + "{");
    parseInnerClasses(producer);
    for (String i : innerClasses)
      $.append(i);
    topClass = $.append("}").toString();
  }
  private void parseInnerClasses(Function<NonTerminal, NonTerminal> producer) {
    Map<NonTerminal, List<List<Symbol>>> n = sortRules(ebnf.normalizedForm(producer));
    for (Entry<NonTerminal, List<List<Symbol>>> r : n.entrySet()) {
      NonTerminal lhs = r.getKey();
      List<List<Symbol>> rhs = r.getValue();
      if (!isInheritanceRule(rhs)) {
        innerClassesFieldTypes.putIfAbsent(lhs.name(), new LinkedHashMap<>());
        for (List<Symbol> clause : r.getValue())
          for (Symbol s : clause)
            parseSymbol(lhs.name(), s);
      }
    }
    for (Entry<NonTerminal, List<List<Symbol>>> r : n.entrySet()) {
      StringBuilder $ = new StringBuilder();
      NonTerminal lhs = r.getKey();
      List<List<Symbol>> rhs = r.getValue();
      $.append("public static class ") //
          .append(lhs.name()) //
          .append((!inheritance.containsKey(lhs) ? "" : " extends " + inheritance.get(lhs).iterator().next())) //
          .append("{");
      if (!isInheritanceRule(rhs))
        for (Entry<String, String> e : innerClassesFieldTypes.get(lhs.name()).entrySet())
          $.append(e.getValue()).append(" ").append(e.getKey()).append(";");
      innerClasses.add($.append("}").toString());
    }
  }
  private void parseSymbol(String lhs, Symbol s) {
    for (String t : parseType(lhs, s))
      innerClassesFieldTypes.get(lhs).put(generateFieldName(lhs, s), t);
  }
  @SuppressWarnings("unused") private List<String> parseTypes(String lhs, List<Symbol> ss) {
    return ss.stream().map(x -> parseType(lhs, x)).reduce(new LinkedList<>(), (l1, l2) -> {
      l1.addAll(l2);
      return l1;
    });
  }
  private List<String> parseType(@SuppressWarnings("unused") String lhs, Symbol s) {
    List<String> $ = new LinkedList<>();
    // if (s instanceof Optional) {
    // Optional o = (Optional) s;
    // for (String x : parseTypes(lhs, o.symbols))
    // $.add("java.util.Optional<" + x + ">");
    // } else if (s instanceof Either)
    // $.add(generateEither((Either) s));
    // else if (s instanceof OneOrMore) {
    // OneOrMore o = (OneOrMore) s;
    // for (String x : parseTypes(lhs, o.symbols))
    // $.add(x + "[]");
    // for (String x : parseTypes(lhs, o.separators))
    // $.add(x + "[]");
    // } else if (s instanceof NoneOrMore || s instanceof NoneOrMore.Separator
    // || s instanceof NoneOrMore.IfNone) {
    // NoneOrMore n = s instanceof NoneOrMore ? (NoneOrMore) s
    // : s instanceof NoneOrMore.Separator ? ((NoneOrMore.Separator) s).parent()
    // : ((NoneOrMore.IfNone) s).parent();
    // if (n.ifNone.isEmpty()) {
    // for (String x : parseTypes(lhs, n.symbols))
    // $.add(x + "[]");
    // for (String x : parseTypes(lhs, n.separators))
    // $.add(x + "[]");
    // } else
    // $.add(generateEither((NoneOrMore) s));
    // } else if (s instanceof EVerb) {
    // EVerb e = (EVerb) s;
    // $.addAll(parseType(lhs, e.ent));
    // } else //
    if (s instanceof Verb) {
      Verb v = (Verb) s;
      for (ParameterType t : v.type)
        if (t instanceof ClassType)
          $.add(((ClassType) t).clazz.getTypeName());
        else if (t instanceof NestedType)
          $.add(((NestedType) t).toString(packagePath, topClassName));
        else
          $.add(t.toString());
    } else if (s instanceof NonTerminal)
      $.add(((NonTerminal) s).name(packagePath, topClassName));
    return $;
  }
  // // NOTE this method (maybe others too) assume "either" accepts simple
  // symbols
  // private String generateEither(Either e) {
  // StringBuilder $ = new StringBuilder();
  // String name = generateClassName("Either");
  // $.append("static class ").append(name).append("{");
  // List<String> enumContent = an.empty.list();
  // $.append("public Object $;").append("public Tag tag;");
  // for (Symbol x : e.symbols) {
  // String verbType, typeName, capitalName;
  // $.append("boolean is").append(capitalName =
  // capital(x.name())).append("(){return Tag.").append(capitalName)
  // .append(".equals(tag);}");
  // $.append(typeName = x.isVerb() ? ("".equals(verbType = ((Verb)
  // x).type.toString()) ? "Void" : verbType) : "Void")
  // .append(" get").append(capitalName).append("(){return (") //
  // .append(typeName).append(")$;}");
  // enumContent.add(capitalName);
  // }
  // $.append("public enum Tag{");
  // for (String x : enumContent)
  // $.append(x).append(",");
  // $.append("}}");
  // innerClasses.add($.toString());
  // return name;
  // }
  // private String generateEither(NoneOrMore n) {
  // StringBuilder $ = new StringBuilder();
  // String name = generateClassName("Either");
  // $.append("static class ").append(name).append("{public boolean exist;");
  // for (String type : parseTypes(name, n.symbols)) {
  // String varName;
  // $.append("private ").append(type + "[]").append(" ") //
  // .append(varName = generateFieldName(name, type)).append(";");
  // $.append(type).append(" get").append(capital(type)).append("(){return
  // ").append(varName).append(";}");
  // }
  // for (String type : parseTypes(name, n.separators)) {
  // String varName;
  // $.append("private ").append(type + "[]").append(" ") //
  // .append(varName = generateFieldName(name, type)).append(";");
  // $.append(type).append(" get").append(capital(type)).append("(){return
  // ").append(varName).append(";}");
  // }
  // for (String type : parseTypes(name, n.ifNone)) {
  // String varName;
  // $.append("private ").append(type + "[]").append(" ") //
  // .append(varName = generateFieldName(name, type)).append(";");
  // $.append(type).append(" get").append(capital(type)).append("(){return
  // ").append(varName).append(";}");
  // }
  // $.append("boolean isList(){return exist;}boolean isNone(){return
  // !exist;}}");
  // innerClasses.add($.toString());
  // return name;
  // }
  private String generateFieldName(String lhs, String name) {
    if (!innerClassesFieldUsedNames.containsKey(lhs))
      return name;
    Map<String, Integer> names = innerClassesFieldUsedNames.get(lhs);
    if (!names.containsKey(name)) {
      names.put(lhs, Integer.valueOf(0));
      return name;
    }
    if (names.get(lhs).intValue() == 0) {
      String type = innerClassesFieldTypes.get(lhs).get(name);
      innerClassesFieldTypes.get(lhs).remove(name);
      innerClassesFieldTypes.get(lhs).put(name + "1", type);
      innerClassesFieldUsedNames.get(lhs).put(name, Integer.valueOf(1));
    }
    int n;
    innerClassesFieldUsedNames.get(lhs).put(name,
        Integer.valueOf(n = innerClassesFieldUsedNames.get(lhs).get(name).intValue() + 1));
    return name + n;
  }
  private String generateFieldName(String lhs, Symbol s) {
    return generateFieldName(lhs, s.head().name().toLowerCase());
  }
  @SuppressWarnings("unused") private String generateClassName(String name) {
    if (!innerClassesUsedNames.containsKey(name)) {
      innerClassesUsedNames.put(name, Integer.valueOf(1));
      return name + 1;
    }
    int n;
    innerClassesUsedNames.put(name, Integer.valueOf(n = innerClassesUsedNames.get(name).intValue() + 1));
    return name + n;
  }
  private Map<NonTerminal, List<List<Symbol>>> sortRules(Map<NonTerminal, List<List<Symbol>>> orig) {
    clearEmptyRules(orig);
    inheritance.clear();
    for (Entry<NonTerminal, List<List<Symbol>>> e : orig.entrySet())
      if (isInheritanceRule(e.getValue()))
        for (List<Symbol> rhs : e.getValue())
          for (Symbol s : rhs)
            if (s.isNonTerminal()) {
              inheritance.initialize((NonTerminal) s);
              inheritance.add((NonTerminal) s, e.getKey());
            }
    Map<NonTerminal, List<List<Symbol>>> $ = new LinkedHashMap<>(), remain = new HashMap<>(orig);
    orig.keySet().stream().filter(x -> !inheritance.containsKey(x)).forEach(x -> {
      $.put(x, orig.get(x));
      remain.remove(x);
    });
    while (!remain.isEmpty()) {
      remain.entrySet().stream()
          .filter(
              e -> e.getValue().stream().allMatch(c -> c.stream().allMatch(s -> (!(s instanceof NonTerminal) || $.containsKey(s)))))
          .forEach(e -> $.put(e.getKey(), e.getValue()));
      $.keySet().forEach(x -> remain.remove(x));
    }
    return $;
  }
  private static void clearEmptyRules(Map<NonTerminal, List<List<Symbol>>> rs) {
    List<Symbol> tbr = an.empty.list();
    rs.keySet().stream().forEach(k -> //
    rs.get(k).stream().forEach(c -> //
    c.stream().filter(l -> //
    l.isVerb() && ((Verb) l).type.length == 0).forEach(e -> tbr.add(e))));
    rs.values().stream().forEach(r -> r.stream().forEach(c -> c.removeAll(tbr)));
  }
  private static boolean isInheritanceRule(List<List<Symbol>> rhs) {
    return rhs.size() > 1 || rhs.size() == 1 && rhs.get(0) instanceof NonTerminal;
  }
  public static String capital(String s) {
    if (s == null)
      throw new IllegalArgumentException("Should not capitalize null String");
    if (s.length() == 0)
      return s;
    return s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
  }
  public static JamoosClassesRenderer render(EBNF ebnf, String packagePath) {
    return new JamoosClassesRenderer(ebnf, packagePath);
  }
}
