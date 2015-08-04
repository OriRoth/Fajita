package org.spartan.fajita.api.ast;

import java.util.ArrayList;
import java.util.Arrays;

public class Atomic extends Compound {

	public Atomic(final Compound parent, final String name, final Object... parameters) {
		super(parent, name,parameters);
	}

	@Override
	public ArrayList<Compound> getChildren() {
		return new ArrayList<>();
	}
	
	@Override
	public String toString() {
		return super.toString() +" = "+ Arrays.toString(params);
	}
}