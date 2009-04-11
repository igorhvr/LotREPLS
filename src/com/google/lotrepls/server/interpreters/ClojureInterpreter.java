/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.lotrepls.server.interpreters;

import java.io.Serializable;

import javax.servlet.ServletContext;

import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

/**
 * A Clojure Interpreter.
 *
 */
public class ClojureInterpreter implements Interpreter {
  public void init(ServletContext context) {
  }

  public Context createContext() {
    return new Ctx();
  }

  public String execute(Context context, String script) throws Exception {
    // We don't actually need the context object here, but we need it to have
    // been initialized since the
    // constructor for Ctx sets static state in the Clojure runtime.

    Object result = clojure.lang.Compiler.eval(RT.readString(script));

    return RT.printString(result);
  }

  static class Ctx implements Context, Serializable {
    private static final long serialVersionUID = 1L;

    Symbol USER = Symbol.create("user");
    Symbol CLOJURE = Symbol.create("clojure.core");

    transient Var in_ns;
    transient Var refer;
    transient Var ns;
    transient Var compile_path;
    transient Var warn_on_reflection;
    transient Var print_meta;
    transient Var print_length;
    transient Var print_level;

    Ctx() {
      in_ns = RT.var("clojure.core", "in-ns");
      refer = RT.var("clojure.core", "refer");
      ns = RT.var("clojure.core", "*ns*");
      compile_path = RT.var("clojure.core", "*compile-path*");
      warn_on_reflection = RT.var("clojure.core", "*warn-on-reflection*");
      print_meta = RT.var("clojure.core", "*print-meta*");
      print_length = RT.var("clojure.core", "*print-length*");
      print_level = RT.var("clojure.core", "*print-level*");

      try {
        Var.pushThreadBindings(RT.map(ns, ns.get(), warn_on_reflection,
            warn_on_reflection.get(), print_meta, print_meta.get(),
            print_length, print_length.get(), print_level, print_level.get(),
            compile_path, "classes"));
        in_ns.invoke(USER);
        refer.invoke(CLOJURE);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } finally {
        Var.popThreadBindings();
      }
    }
  }

}
