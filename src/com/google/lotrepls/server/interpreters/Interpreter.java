/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.lotrepls.server.interpreters;

import javax.servlet.ServletContext;

/**
 * A virtual interpreter for executing statements for a particular
 * programming language. 
 * 
 * In theory, {@code Interpreter} would be unnecessary given JSR 223 
 * (Scripting for the Java Platform}, but in practice it's simpler to 
 * create our own specific interface for this application.
 *
 */
public interface Interpreter {
  
  /**
   * Initializes the {@code Interpreter} with {@code context}
   *  
   * @param context a non-null {@code ServletContext}.
   */
  public void init(ServletContext context);

  /**
   * Creates a new {@code Context} for the {@code Interpreter},
   * which represents a single session of state. 
   *  
   * @return a non-null {@code Context}
   */
  public Context createContext();

  /**
   * Executes a script in the {@code Interpreter}.
   * 
   * @param context a non-null {@code Context} 
   * @param script a non-null {@code} snippet of code
   * @return the result of executing {@code script}
   *  
   * @throws Exception if an error occurs during execution
   */
  public String execute(Context context, String script) throws Exception;
}
