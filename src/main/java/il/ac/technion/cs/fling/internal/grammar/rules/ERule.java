package il.ac.technion.cs.fling.internal.grammar.rules;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/** Extended EBNF Rule, allowing quantifiers
 *
 * @author Yossi Gil
 *
 * @since 2020-06-23 */
public class ERule {
  public final Variable variable;
  private final Collection<Body> bodies = new ArrayList<>();
  public boolean of(final Variable v) {
    return v.equals(variable);
  }
  /** Construct an epsilon rule */
  public ERule(final Variable variable) {
    Objects.requireNonNull(variable);
    this.variable = variable;
    bodies.add(new Body());
  }
  /** Construct an ordinary rule */
  public ERule(final Variable variable, final Body body) {
    Objects.requireNonNull(variable);
    Objects.requireNonNull(body);
    this.variable = variable;
    bodies.add(body);
  }
  public ERule(final Variable variable, final Collection<Body> forms) {
    Objects.requireNonNull(variable);
    Objects.requireNonNull(forms);
    this.variable = variable;
    bodies.addAll(forms);
    assert !bodies.isEmpty();
  }
  public Stream<Body> bodies() {
    return bodies.stream();
  }
  @Override public boolean equals(final Object o) {
    if (this == o)
      return true;
    if (!(o instanceof ERule other))
      return false;
    return variable.equals(other.variable) && bodies.equals(other.bodies);
  }
  @Override public int hashCode() {
    return variable.hashCode() + 31 * bodies.hashCode();
  }
  private Stream<Component> quantifiedSymbols() {
    return quantifiers().flatMap(Quantifier::symbols);
  }
  private Stream<Quantifier> quantifiers() {
    return components().filter(Component::isQuantifier).map(Component::asQuantifier);
  }
  public Iterable<Body> bodiesList() {
    return bodies;
  }
  @Override public String toString() {
    return String.format("%s->%s", variable,
        bodies().map(b -> b.isEmpty() ? "ε" : b.toString()).collect(Collectors.joining("|")));
  }
  public Stream<Variable> variables() {
    return Stream.concat(variables(components()), variables(quantifiedSymbols()));
  }
  public Stream<Token> tokens() {
    return Stream.concat(tokens(components()), tokens(quantifiedSymbols()));
  }
  private Stream<Component> components() {
    return bodies().flatMap(Collection::stream);
  }
  private static Stream<Token> tokens(final Stream<? extends Component> symbols) {
    return symbols.filter(Component::isToken).map(Component::asToken);
  }
  private static Stream<Variable> variables(final Stream<? extends Component> symbols) {
    return symbols.filter(Component::isVariable).map(Component::asVariable);
  }
}
