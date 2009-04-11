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

import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;

import javax.servlet.ServletContext;

/**
 * A JavaScript Interpreter
 *
 */
public class JavaScriptInterpreter implements Interpreter {

  public void init(ServletContext context) {
    // TODO Auto-generated method stub

  }

  public Context createContext() {
    return new Ctx();
  }

  public String execute(Context context, String script) throws Exception {
    Ctx ctx = (Ctx) context;
    Object ret = ctx.jsContext.evaluateString(ctx.scope, script, "<stdin>", 0,
        null);
    return org.mozilla.javascript.Context.toString(ret);
  }

  static class Ctx implements Context {
    org.mozilla.javascript.Context jsContext;
    Scriptable scope;

    Ctx() {
      ContextFactory cf = new ContextFactory();
      jsContext = cf.enterContext();
      scope = new ImporterTopLevel(jsContext);
    }
  }
}
