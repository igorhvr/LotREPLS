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

import groovy.lang.GroovyShell;

import javax.servlet.ServletContext;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * A Groovy Interpreter.
 *
 */
public class GroovyInterpreter implements Interpreter {
  private static final Charset UTF8 = Charset.forName("UTF-8");

  @Override
  public void init(ServletContext context) {
  }

  // TODO(tobyr) See if we can optimize this so less work
  // is done per context
  @Override
  public Context createContext() {
    return new Ctx();
  }

  @Override
  public String execute(Context context, String script) {
    GroovyShell shell = new GroovyShell();

    ByteArrayOutputStream bufStream = new ByteArrayOutputStream();
    PrintStream output = new PrintStream(bufStream);
    
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
      Object result = shell.evaluate(script);
      output.flush();
      StringBuffer strBuffer = new StringBuffer();
      strBuffer.append(new String(bufStream.toByteArray(), UTF8));
      if (result != null && ! strBuffer.toString().trim().isEmpty()) {        
        strBuffer.append("\n");
        strBuffer.append(result.toString());
      }
      return strBuffer.toString();
    } finally {
      System.setOut(out);
      System.setErr(err);
    }
  }

  // NB(tobyr) This context isn't Serializable.
  static class Ctx implements Context {
  }
}
