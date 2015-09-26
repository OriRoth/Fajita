package org.spartan.fajita.api.examples.balancedParenthesis.states;

import org.spartan.fajita.api.parser.stack.EmptyStack;
import org.spartan.fajita.api.parser.stack.IStack;

@SuppressWarnings("rawtypes") public abstract class BaseState<Stack extends IStack<?>, lp extends BaseState, rp extends BaseState, $, BALANCED extends BaseState>
    implements IStack<Stack> {
  Stack t;

  public BaseState(final Stack t) {
    this.t = t;
  }
  protected BALANCED BALANCED() {
    throw new ParseError("unexpected symbol on state " + this.getClass().getSimpleName());
  }
  protected lp lp() {
    throw new ParseError("unexpected symbol on state " + this.getClass().getSimpleName());
  }
  protected rp rp() {
    throw new ParseError("unexpected symbol on state " + this.getClass().getSimpleName());
  }
  protected $ $() {
    throw new ParseError("unexpected symbol on state " + this.getClass().getSimpleName());
  }

  public static class ParseError extends RuntimeException {
    public ParseError(final String message) {
      super(message);
    }

    private static final long serialVersionUID = 5785838846631116912L;
  }

  public static class Error extends BaseState<EmptyStack, Error, Error, Error, Error> {
    public Error() {
      super(new EmptyStack());
    }
  }
}