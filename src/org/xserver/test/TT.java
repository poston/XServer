package org.xserver.test;

import java.text.ParseException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.xserver.common.util.DateUtil;

public class TT {
	static {
		i = 9;
	}

	static int i;

	public static void main(String[] args) throws ScriptException,
			ParseException {
		System.out.println(i);
	}
}
