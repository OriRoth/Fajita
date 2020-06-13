package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.InterfaceDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.MethodDeclaration;
import il.ac.technion.cs.fling.internal.compiler.api.ParameterFragment;
import il.ac.technion.cs.fling.internal.compiler.api.TypeName;
import il.ac.technion.cs.fling.internal.compiler.api.dom.CompilationUnit;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Interface;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method.Chained;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Type;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

/** Java API adapter. Output contains the API types and a single concrete
 * implementation to be returned from the static method initiation method
 * chains.
 *
 * @author Ori Roth */
public class JavaGenerator extends AbstractGenerator {
  private final String packageName;
  private final String className;

  public JavaGenerator(final String packageName, final String className, final String terminationMethodName,
      final Namer namer) {
    super(terminationMethodName, namer);
    this.packageName = packageName;
    this.className = className;
  }

  @Override public String printFluentAPI(final CompilationUnit fluentAPI) {
    namer.name(fluentAPI);
    return String.format("%s\n%s@SuppressWarnings(\"all\")public interface %s{%s%s%s%s}", //
        startComment(), //
        packageName == null ? "" : String.format("package %s;\nimport java.util.*;\n\n\n", packageName), //
        className, //
        fluentAPI.startMethods.stream().map(this::printMethod).collect(joining()), //
        fluentAPI.interfaces.stream().map(this::printInterface).collect(joining()), //
        printConcreteImplementation(fluentAPI), //
        printAdditionalDeclarations());
  }

  private String startComment() {
    return String.format("/* This file was automatically generated by Fling (c) on %s */\n", new Date());
  }

  @Override public String topTypeName() {
    return "$";
  }

  @Override public String bottomTypeName() {
    return "ø";
  }

  @Override public String typeName(final TypeName name) {
    return printTypeName(name);
  }

  @Override public String typeName(final TypeName name, final List<Type> typeArguments) {
    return String.format("%s<%s>", //
        printTypeName(name), //
        typeArguments.stream().map(this::printType).collect(joining(",")));
  }

  @Override public String startMethod(final MethodDeclaration declaration, final Type returnType) {
    return String.format("public static %s %s(%s) {%s}", //
        printType(returnType), //
        Constants.$$.equals(declaration.name) ? "__" : declaration.name.name(), //
        declaration.getInferredParameters().stream() //
            .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
            .collect(joining(",")), //
        printStartMethodBody(declaration.name, declaration.getInferredParameters()));
  }

  @Override public String printTerminationMethod() {
    return String.format("%s %s();", //
        printTerminationMethodReturnType(), //
        terminationMethodName);
  }

  @Override public String printIntermediateMethod(final MethodDeclaration declaration, final Type returnType) {
    return String.format("%s %s(%s);", //
        printType(returnType), //
        declaration.name.name(), //
        declaration.getInferredParameters().stream() //
            .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
            .collect(joining(",")));
  }

  @Override public String printTopInterface() {
    return String.format("interface ${%s}", printTopInterfaceBody());
  }

  public String printTopInterfaceBody() {
    return String.format("%s %s();", //
        printTerminationMethodReturnType(), //
        terminationMethodName);
  }

  @Override public String printBotInterface() {
    return "interface ø {}";
  }

  @Override public String printInterface(final InterfaceDeclaration declaration, final List<Method> methods) {
    return String.format("interface %s%s{%s}", //
        printInterfaceDeclaration(declaration), //
        !declaration.isAccepting ? "" : " extends " + topTypeName(), //
        methods.stream() //
            .filter(method -> !method.isTerminationMethod()) //
            .map(this::printMethod) //
            .collect(joining()));
  }

  public String printTypeName(final TypeName name) {
    return printTypeName(name.q, name.α, name.legalJumps);
  }

  public String printTypeName(final InterfaceDeclaration declaration) {
    return printTypeName(declaration.q, declaration.α, declaration.legalJumps);
  }

  public String printTypeName(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
    return α == null ? q.name()
        : String.format("%s_%s%s", //
            q.name(), //
            α.stream().map(Named::name).collect(Collectors.joining()), //
            legalJumps == null ? "" : "_" + legalJumps.stream().map(Named::name).collect(Collectors.joining()));
  }

  public String printInterfaceDeclaration(final InterfaceDeclaration declaration) {
    return String.format("%s<%s>", printTypeName(declaration), //
        declaration.typeVariables.stream().map(Named::name).collect(Collectors.joining(",")));
  }

  public String printTypeName(final Interface interfaze) {
    return interfaze.isTop() ? "$" : interfaze.isBot() ? "ø" : printTypeName(interfaze.declaration);
  }

  public String printConcreteImplementation(final CompilationUnit fluentAPI) {
    return String.format("static class α implements %s{%s%s%s}", //
        fluentAPI.interfaces.stream().map(this::printTypeName).collect(joining(",")), //
        printConcreteImplementationClassBody(), fluentAPI.concreteImplementation.methods.stream() //
            .map(Method::asChainedMethod) //
            .map(Chained::declaration) //
            .map(declaration -> String.format("public α %s(%s){%sreturn this;}", //
                declaration.name.name(), //
                declaration.getInferredParameters().stream() //
                    .map(parameter -> String.format("%s %s", //
                        parameter.parameterType, //
                        parameter.parameterName)) //
                    .collect(joining(",")), //
                printConcreteImplementationMethodBody(declaration.name, declaration.getInferredParameters()))) //
            .collect(joining()),
        String.format("public %s %s(){%s}", //
            printTerminationMethodReturnType(), //
            terminationMethodName, //
            printTerminationMethodConcreteBody()));
  }

  /** Start static method body.
   *
   * @param σ          inducing token
   * @param parameters method parameters
   * @return method body */
  @SuppressWarnings("unused") protected String printStartMethodBody(final Token σ,
      final List<ParameterFragment> parameters) {
    return "return new α();";
  }

  /** Prints additional definition in concrete implementation class's body.
   *
   * @return additional definition */
  protected String printConcreteImplementationClassBody() {
    return "";
  }

  /** Concrete implementation's method's body. Making the recording of terminals
   * and their parameters possible.
   *
   * @param σ          current token
   * @param parameters method parameters
   * @return method body */
  @SuppressWarnings("unused") protected String printConcreteImplementationMethodBody(final Token σ,
      final List<ParameterFragment> parameters) {
    return "";
  }

  /** Return type of the termination method.
   *
   * @return return type */
  protected String printTerminationMethodReturnType() {
    return "void";
  }

  /** Concrete implementation's termination method body. Might be used to create
   * and return the processed terminal.
   *
   * @return method body */
  protected String printTerminationMethodConcreteBody() {
    return "";
  }

  /** Additional declaration within the top class.
   *
   * @return additional declarations */
  protected String printAdditionalDeclarations() {
    return "";
  }
}
