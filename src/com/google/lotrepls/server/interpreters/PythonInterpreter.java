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

import org.python.core.PyDictionary;
import org.python.core.PyObject;
import org.python.core.PySystemState;

import java.io.ByteArrayOutputStream;

import javax.servlet.ServletContext;

/**
 * A Python Interpreter.
 *
 */
public class PythonInterpreter implements Interpreter {

  public void init(ServletContext context) {
//    System.getProperties().setProperty("python.path", context.getRealPath("/WEB-INF/lib-python"));
    org.python.util.PythonInterpreter.initialize(System.getProperties(), null,
        new String[0]);
  }

  // TODO(tobyr) See if we can optimize this so less work
  // is done per context
  public Context createContext() {
    return new Ctx();
  }

  public String execute(Context context, String script) {
    ByteArrayOutputStream str = new ByteArrayOutputStream();
    Ctx ctx = (Ctx) context;
    ctx.interpreter.setOut(str);
    ctx.interpreter.setErr(str);
    ctx.interpreter.exec(script);
    return str.toString();
  }

  // NB(tobyr) This context isn't Serializable.
  static class Ctx implements Context {
    PyObject dict = new PyDictionary();
    PySystemState systemState = new PySystemState();

    transient org.python.util.PythonInterpreter interpreter;

    Ctx() {
      interpreter = new org.python.util.PythonInterpreter(dict, systemState);
    }
  }
}
