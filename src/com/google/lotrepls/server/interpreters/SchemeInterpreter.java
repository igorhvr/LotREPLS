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

import jscheme.JScheme;

import javax.servlet.ServletContext;
import java.io.Serializable;

/**
 * A Scheme Interpreter.
 * 
 */
public class SchemeInterpreter implements Interpreter {
  public void init(ServletContext context) {
  }

  public Context createContext() {
    return new Ctx();
  }

  public String execute(Context context, String script) {
    Ctx ctx = (Ctx) context;
    return ctx.scheme.eval(script).toString();
  }

  static class Ctx implements Context, Serializable {
    private static final long serialVersionUID = 1L;

    JScheme scheme = new JScheme();
  }
}
