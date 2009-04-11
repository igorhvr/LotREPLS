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

import org.jruby.Ruby;
import org.jruby.RubyRuntimeAdapter;
import org.jruby.RubyInstanceConfig;
import org.jruby.javasupport.JavaEmbedUtils;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * A Ruby Interpreter.
 * 
 */
public class RubyInterpreter implements Interpreter {

  public void init(ServletContext context) {
  }

  public Context createContext() {
    return new Ctx();
  }

  public String execute(Context context, String script) throws Exception {
    Ctx ctx = (Ctx) context;
    return ctx.execute(script);
  }

  // NB: Not serializable
  private static class Ctx implements Context {
    private Ruby runtime;
    private RubyRuntimeAdapter evaler;
    private ByteArrayOutputStream bufStream;
    private PrintStream bufferedOut;

    @SuppressWarnings("unchecked")
    private Ctx() {
      bufStream = new ByteArrayOutputStream();
      try {
        bufferedOut = new PrintStream(bufStream, true, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
      RubyInstanceConfig config = new RubyInstanceConfig();
      config.setOutput(bufferedOut);
      config.setError(bufferedOut);
      runtime = JavaEmbedUtils.initialize(new ArrayList(), config);
      evaler = JavaEmbedUtils.newRuntimeAdapter();
    }

    String execute(String script) throws Exception {
      String result = evaler.eval(runtime, script).toString();
      String buf = new String(bufStream.toByteArray(), "UTF-8");
      bufStream.reset();
      if (result != null) {
        buf = buf + result;
      }
      return buf;
    }
  }
}
