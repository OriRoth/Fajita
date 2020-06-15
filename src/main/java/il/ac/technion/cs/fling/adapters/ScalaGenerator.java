package il.ac.technion.cs.fling.adapters;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import il.ac.technion.cs.fling.internal.compiler.Namer;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Model;
import il.ac.technion.cs.fling.internal.compiler.api.dom.TypeSignature;
import il.ac.technion.cs.fling.internal.compiler.api.dom.Method;
import il.ac.technion.cs.fling.internal.compiler.api.dom.MethodSignature;
import il.ac.technion.cs.fling.internal.compiler.api.dom.SkeletonType;
import il.ac.technion.cs.fling.internal.compiler.api.dom.TypeName;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

/** Scala API adapter.
 *
 * @author Ori Roth */
public class ScalaGenerator extends APIGenerator {

  public ScalaGenerator(final String endName, final Namer namer) {
    super(namer, endName);
  }

  @Override protected String comment(String comment) {
    return String.format("/* %s */", comment);
  }

  @Override public String render(final Model m) {
    namer.name(m);
    return String.format("%s\n%s", //
        m.types().map(this::render).collect(joining("\n")), //
        m.starts().map(this::render).collect(joining("\n")));
  }

  @Override public String render(final TypeName name, final List<SkeletonType> typeArguments) {
    return String.format("%s[%s]", //
        render(name), //
        typeArguments.stream().map(this::render).collect(joining(",")));
  }

  @Override public String renderMethod(final MethodSignature declaration, final SkeletonType returnType) {
    return String.format("def %s():%s=%s", //
        Constants.$$.equals(declaration.name) ? "__" : declaration.name.name(), //
        render(returnType), //
        printTypeInstantiation(returnType));
  }

  @Override public String renderTerminationMethod() {
    return String.format("def %s():Unit={}", endName);
  }

  @Override public String render(final MethodSignature declaration, final SkeletonType returnType) {
    final String _returnType = render(returnType);
    final String returnValue = printTypeInstantiation(returnType);
    return String.format("def %s(%s):%s=%s", //
        declaration.name.name(), //
        printParametersList(declaration), //
        _returnType, //
        returnValue);
  }

  @Override public String renderInterfaceTop() {
    return String.format("class TOP{\ndef %s():Unit={}\n}", endName);
  }

  @Override public String renderInterfaceBottom() {
    return "private class BOT{}";
  }

  @Override public String render(final TypeSignature declaration, final List<Method> methods) {
    return String.format("%s(%s){\n%s\n}", //
        render(declaration), //
        printClassParameters(declaration.parameters), //
        methods.stream().map(this::render).collect(joining("\n")));
  }

  @Override public String render(final TypeName name) {
    return render(name.q, name.α, name.legalJumps);
  }

  @Override public String render(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
    final String qn = q.name();
    return α == null ? qn
        : String.format("%s_%s%s", //
            q.name(), //
            α.stream().map(Named::name).collect(Collectors.joining()), //
            legalJumps == null ? "" : "_" + legalJumps.stream().map(Named::name).collect(Collectors.joining()));
  }

  @SuppressWarnings("static-method") public String printParametersList(final MethodSignature declaration) {
    return declaration.parmeters() //
        .map(parameter -> String.format("%s %s", parameter.parameterType, parameter.parameterName)) //
        .collect(joining(","));
  }

  @Override public String render(final TypeSignature declaration) {
    final String typeName = render(declaration.q, declaration.α, declaration.legalJumps);
    final String typeParameters = declaration.parameters().map(Named::name).collect(Collectors.joining(","));
    return String.format("class %s", //
        declaration.parameters.isEmpty() ? //
            typeName //
            : String.format("%s[%s]", typeName, typeParameters));
  }

  @SuppressWarnings("static-method") private String printClassParameters(final Word<Named> typeVariables) {
    return typeVariables.stream().map(Named::name) //
        .map(var -> String.format("val __%s:%s", var, var)) //
        .collect(joining(","));
  }

  public String printTypeInstantiation(final SkeletonType returnType) {
    final String _returnType = render(returnType);
    // TODO manage this HACK
    return !Arrays.asList("TOP", "BOT").contains(_returnType) //
        && !_returnType.contains("_") ? //
            "__" + _returnType
            : String.format("new %s(%s)", _returnType, //
                returnType.arguments() //
                    .map(this::printTypeInstantiation) //
                    .collect(joining(",")));
  }
}
