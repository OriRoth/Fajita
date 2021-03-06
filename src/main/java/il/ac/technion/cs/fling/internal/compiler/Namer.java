package il.ac.technion.cs.fling.internal.compiler;
import java.util.List;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.ast.nodes.ASTCompilationUnitNode;
import il.ac.technion.cs.fling.internal.grammar.rules.Component;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
/** Names elements in the generated code.
 *
 * @author Ori Roth */
public interface Namer {
  /** Create new variable subject to the given.
   *
   * @param variable parent variable
   * @return child variable */
  // TODO add context to variable creation.
  Variable createASTChild(Variable variable);
  /** Create new variable subject to given symbol in notation's context.
   *
   * @param inner parent symbol
   * @return child variable */
  Variable createQuantificationChild(List<? extends Component> inner);
  /** Inner API type name.
   *
   * @param variable inducing head variable
   * @return API type name */
  String headVariableClassName(Variable variable);
  /** Inner API acceptance type name.
   *
   * @return API type name */
  String headVariableConclusionTypeName();
  /** AST type name of given variable
   *
   * @param variable inducing variable
   * @return AST type name */
  String getASTClassName(Variable variable);
  /** Name elements within given AST. Declarations pending naming are types and
   * fields.
   *
   * @param compilationUnit AST */
  void name(ASTCompilationUnitNode compilationUnit);
  /** Name elements within given API. Declarations pending naming are method
   * parameters.
   *
   * @param m API */
  void name(Model m);
}
