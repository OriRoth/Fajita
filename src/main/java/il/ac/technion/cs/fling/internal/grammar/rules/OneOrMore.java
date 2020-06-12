package il.ac.technion.cs.fling.internal.grammar.rules;

import static java.util.Arrays.asList;

import java.util.*;
import java.util.function.*;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.FieldNode.FieldNodeFragment;
import il.ac.technion.cs.fling.internal.grammar.sententials.quantifiers.JavaCompatibleQuantifier;
import il.ac.technion.cs.fling.internal.grammar.types.ClassParameter;

@JavaCompatibleQuantifier public class OneOrMore extends Quantifier.Sequence {
  public OneOrMore(List<Symbol> symbols) {
    super(symbols);
  }

  @Override public Variable expand(final Namer namer, final Consumer<Variable> variableDeclaration,
      final Consumer<ERule> ruleDeclaration) {
    List<Component> expandedSymbols = new ArrayList<>();
    for (Symbol s : symbols)
      expandedSymbols.add(!s.isQuantifier() ? s : //
        s.asQuantifier().expand(namer, variableDeclaration, ruleDeclaration));
    final Variable head = namer.createQuantificationChild(symbols);
    final Variable tail = namer.createQuantificationChild(symbols);
    variableDeclaration.accept(head);
    variableDeclaration.accept(tail);
      List<Component> rhs = new ArrayList<>(expandedSymbols);
    rhs.add(tail);
    ruleDeclaration.accept(new ERule(head, asList( //
        new Body(rhs))));
    ruleDeclaration.accept(new ERule(tail, asList(//
        new Body(rhs), //
        new Body())));
    return head;
  }
  
  @Override protected String getVisitingStatement(Symbol symbol, BiFunction<Variable, String, String> variableVisitingSolver,
      String accessor, Supplier<String> variableNamesGenerator) {
    if (!symbol.isVariable() && !symbol.isQuantifier())
      return null;
    final String streamingVariable = variableNamesGenerator.get();
    String action = symbol.isVariable() ? //
        variableVisitingSolver.apply(symbol.asVariable(), streamingVariable) : //
        String.format("{%s}", symbol.asQuantifier().symbols() //
            .map(s -> s.asQuantifier().getVisitingStatement(s, variableVisitingSolver, streamingVariable, variableNamesGenerator)));
    return String.format("{%s.stream().forEach(%s->%s);}", //
        accessor, //
        streamingVariable, //
        action);
  }

  @Override public List<FieldNodeFragment> getFields(Function<Component, List<FieldNodeFragment>> fieldsSolver,
      @SuppressWarnings("unused") Function<String, String> nameFromBaseSolver) {
    List<FieldNodeFragment> $ = new ArrayList<>();
    for (Symbol symbol : symbols)
      for (FieldNodeFragment rawField : fieldsSolver.apply(symbol))
        $.add(new FieldNodeFragment( //
            String.format("%s<%s>", //
                List.class.getCanonicalName(), //
                ClassParameter.unPrimitiveType(rawField.parameterType)), //
            rawField.parameterName) {
          @Override public String visitingStatement(final BiFunction<Variable, String, String> variableVisitingSolver,
              final String accessor, final Supplier<String> variableNamesGenerator) {
            return getVisitingStatement(symbol, variableVisitingSolver, accessor, variableNamesGenerator);
          }
        });
    return $;
  }

  @Override public boolean isNullable(Predicate<Component> nullabilitySolver) {
    return symbols().allMatch(nullabilitySolver);
  }

  @Override public Set<Token> getFirsts(final Function<List<? extends Component>, Set<Token>> firstsSolver) {
    return firstsSolver.apply(symbols);
  }

  @SuppressWarnings("unchecked") public static List<List<Object>> abbreviate(final List<Object> rawNode,
      final int fieldCount) {
    final List<List<Object>> $ = new ArrayList<>();
    for (int i = 0; i < fieldCount; ++i)
      $.add(new ArrayList<>());
    List<Object> currentRawNode = rawNode;
    while (!currentRawNode.isEmpty()) {
      assert currentRawNode.size() == fieldCount + 1;
      final List<Object> rawArguments = currentRawNode.subList(0, fieldCount);
      for (int i = 0; i < fieldCount; ++i)
        $.get(i).add(rawArguments.get(i));
      currentRawNode = (List<Object>) currentRawNode.get(fieldCount);
    }
    return $;
  }

  @Override public String marker() {
    return "+";
  }
}
