package org.xserver.test;

import bsh.EvalError;
import bsh.Interpreter;

public class Bsh {
	public static void main(String[] args) throws EvalError {
		Interpreter interpreter = new Interpreter();
		Number i = (Number) interpreter.eval("(1+2) * 1.0/2");
		System.out.println(i);
	}
}
