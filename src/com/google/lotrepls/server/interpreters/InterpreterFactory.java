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

import com.google.lotrepls.shared.InterpreterType;

/**
 * Creates new {@link Interpreter Interpreters}. 
 * 
 */
public class InterpreterFactory {

  /**
   * Returns a new {@code Interpreter} for the given {@code type} 
   * 
   * @param type Must not be null
   * @return a newly-created, matching {@code Interpreter}
   */
  public Interpreter createInterpreter(InterpreterType type) {
    switch (type) {
      case Beanshell:
        return new BeanShellInterpreter();

      case Clojure:
        return new ClojureInterpreter();

      case Groovy:
        return new GroovyInterpreter();

      case JavaScript:
        return new JavaScriptInterpreter();

      case PC:
        return new PCInterpreter();

      case Python:
        return new PythonInterpreter();

      case Ruby:
        return new RubyInterpreter();

      case Scala:
        return new ScalaInterpreter();

      case Scheme:
        return new SchemeInterpreter();

      default:
        throw new RuntimeException(type + " is not yet supported");
    }
  }
}
