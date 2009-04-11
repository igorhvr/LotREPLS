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

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletContext;
import scala.tools.nsc.Settings;

/**
 * A Scala Interpreter.
 *
 */
public class ScalaInterpreter implements Interpreter {
  private ByteArrayOutputStream interpOutStream;

  private scala.tools.nsc.Interpreter interp;

  public void init(ServletContext context) {
    String rtJarPath = getJarFromResource(getClass().getResource(
        "/java/lang/Object.class"));
    String scalaLibPath = getJarFromResource(getClass().getResource(
        "/scala/Array.class"));

    interpOutStream = new ByteArrayOutputStream();

    Settings settings = new Settings();
    String codebasePath = rtJarPath + " " + scalaLibPath;

    settings.Xcodebase().value_$eq(codebasePath.toString());
    settings.classpath().value_$eq("");
    settings.bootclasspath().value_$eq("");
    settings.outdir().value_$eq(".");
    settings.extdirs().value_$eq("");
    interp = new scala.tools.nsc.Interpreter(settings, new PrintWriter(
        new OutputStreamWriter(interpOutStream))) {
      @Override
      public ClassLoader parentClassLoader() {
        return Thread.currentThread().getContextClassLoader();
      }
    };
  }

  public Context createContext() {
    return new Ctx();
  }

  public String execute(Context context, String script) throws Exception {
    StringBuffer strBuffer = new StringBuffer();

    interp.reset();
    interpOutStream.reset();

    PrintStream output = new PrintStream(interpOutStream);
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
      interp.interpret(script);
    } finally {
      System.setOut(out);
      System.setErr(err);
    }

    output.flush();

    strBuffer.append(new String(interpOutStream.toByteArray(), "UTF-8"));

    return interpOutStream.toString();
  }

  /**
   * Returns a URL to a Jar file in which a resource from that Jar file is
   * located. This is the easiest way that we can locate certain Jar files.
   */
  private static String getJarFromResource(URL resource) {
    String path = resource.toString();
    int indexOfFile = path.indexOf("file:");
    int indexOfSeparator = path.lastIndexOf('!');
    return path.substring(indexOfFile, indexOfSeparator);
  }

  // NB: not serializable
  static class Ctx implements Context {
  }
}
