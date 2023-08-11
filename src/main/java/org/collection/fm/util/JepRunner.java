package org.collection.fm.util;

import jep.Interpreter;
import jep.MainInterpreter;
import jep.SharedInterpreter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class JepRunner {

    public static void main(String args[]) throws Exception {
        Interpreter interp = new SharedInterpreter();
        interp.exec("x = 2+2");
        interp.exec("print('hello world')");
        System.out.println(interp.getValue("x"));
    }
}
