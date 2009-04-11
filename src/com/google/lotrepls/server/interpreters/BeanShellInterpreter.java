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

import javax.servlet.ServletContext;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * A BeanShell Interpreter.
 *
 */
public class BeanShellInterpreter implements Interpreter {
  public void init(ServletContext context) {
  }

  public Context createContext() {
    return new Ctx();
  }

  public String execute(Context context, String script) throws Exception {
    Ctx ctx = (Ctx) context;
    return ctx.execute(script);
  }

  static class Ctx implements Context, Serializable {
    private static final long serialVersionUID = 1L;
    private static final Charset UTF8 = Charset.forName("UTF-8");
    bsh.Interpreter interpreter;

    Ctx() {
      interpreter = new bsh.Interpreter(null, null, null, false);

      // Make the servlet request and response available to users,
      // if we so chose.

      // bsh.set("bsh.httpServletRequest", request);
      // bsh.set("bsh.httpServletResponse", response);
    }

    String execute(String script) throws Exception {

      StringBuffer strResult = new StringBuffer();
      Object result = null;

      // out and err are transient in bsh.Interpreter, so we reset them
      // on each request just to make sure they're there.
      ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
      PrintStream output = new PrintStream(outputBuffer);

      interpreter.setOut(output);
      interpreter.setErr(output);

      // We set System.out and System.err such that we capture anything
      // that runs during interpretation (Such as a System.out.println
      // call). The _only_ reason this doesn't fail due to concurrency,
      // is because we don't get concurrent requests to a single JVM.
      PrintStream out = System.out;
      PrintStream err = System.err;
      System.setOut(output);
      System.setErr(output);

      try {
        // Eval the user text
        result = interpreter.eval(script);
      } finally {
        System.setOut(out);
        System.setErr(err);
      }

      output.flush();
      strResult.append(new String(outputBuffer.toByteArray(), UTF8));
      
      if (result != null) {
        strResult.append(result.toString());
      }

      String ret = strResult.toString();
      final String oddPrefix = "experiment: creating class manager\n";
      if (ret.startsWith(oddPrefix))
        ret = ret.substring(oddPrefix.length());
      return ret;
    }
  }
}
