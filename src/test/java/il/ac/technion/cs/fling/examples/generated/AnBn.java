/* This file was automatically generated by Fling (c) on Mon Feb 24 19:22:06 IST 2020 */

package il.ac.technion.cs.fling.examples.generated;

@SuppressWarnings("all")
public interface AnBn {
  public static q0_EX_q2<$> a() {
    return new α();
  }

  interface $ {
    void $();
  }

  interface q0_EX_q2<q2> {
    q0_XX_q0q1q2<q0_E_q2<q2>, q2, q2_E_q2<q2>> a();

    q2 b();
  }

  interface q0_E_q2<q2> {
    q0_EX_q2<q2> a();
  }

  interface q2_E_q2<q2> extends $ {}

  interface q0_XX_q0q1q2<q0, q1, q2> {
    q0_XX_q0q1q2<q0_X_q0q1q2<q0, q1, q2>, q1_X_q0q1q2<q0, q1, q2>, q2_X_q0q1q2<q0, q1, q2>> a();

    q1_X_q0q1q2<q0, q1, q2> b();
  }

  interface q0_X_q0q1q2<q0, q1, q2> {
    q0_XX_q0q1q2<q0, q1, q2> a();

    q1 b();
  }

  interface q1_X_q0q1q2<q0, q1, q2> {
    q1 b();
  }

  interface q2_X_q0q1q2<q0, q1, q2> extends $ {}

  static class α
      implements $,
          q0_EX_q2,
          q0_E_q2,
          q2_E_q2,
          q0_XX_q0q1q2,
          q0_X_q0q1q2,
          q1_X_q0q1q2,
          q2_X_q0q1q2 {
    public α a() {
      return this;
    }

    public α b() {
      return this;
    }

    public void $() {}
  }
}

