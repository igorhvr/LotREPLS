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

package com.google.lotrepls.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.lotrepls.server.interpreters.Context;
import com.google.lotrepls.server.interpreters.Interpreter;
import com.google.lotrepls.server.interpreters.InterpreterFactory;
import com.google.lotrepls.shared.LotREPLsApi;
import com.google.lotrepls.shared.InterpreterType;
import com.google.lotrepls.shared.InterpreterException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server-side implementation of LotREPLsApi. This class manages the
 * interpreters for each language and routes commands appropriately.
 * 
 */
public class LotREPLsApiImpl extends RemoteServiceServlet implements
    LotREPLsApi {
  private static InterpreterFactory factory = new InterpreterFactory();
  private static Map<InterpreterType, Interpreter> interpreters = new HashMap<InterpreterType, Interpreter>();
  private final Logger log = Logger.getLogger(LotREPLsApiImpl.class.getName());
  
  @Override
  public void init() {
    // TODO(tobyr) Consider doing some lazy pre-initialization
    // of interpreters, so people don't have a wait when switching
    // for the first time.
  }

  public String eval(InterpreterType type, String script)
      throws InterpreterException {

    // Look up a cached interpreter
    Interpreter interpreter;

    synchronized (interpreters) {
      interpreter = interpreters.get(type);
      if (interpreter == null) {
        interpreter = factory.createInterpreter(type);
        interpreter.init(getServletContext());
        interpreters.put(type, interpreter);
      }
    }

    // Find the user's context for this language
    HttpServletRequest request = getThreadLocalRequest();
    HttpSession session = request.getSession();
    Context context = null;
    try {
      context = (Context) session.getAttribute(type.name());
    } catch(Exception e) {
      // If there was a deserialization error, throw the session away
      session.removeAttribute(type.name());
      log.log(Level.WARNING, "Could not deserialize context " + type.name(), e);
    }
    if (context == null) {
      // If there isn't a context stored on the session, make a new one.
      // This could either mean the user hasn't entered any commands in this
      // language in this session or that the language's context object is not
      // serializable.
      context = interpreter.createContext();
    }

    try {
      log.info("Executing script for " + type.name() + "\n" + script);
      String result = interpreter.execute(context, script);
      if (context instanceof Serializable) {
        session.setAttribute(type.name(), copy((Serializable) context));
      }
      return result;
    } catch (Exception e) {
      super.getServletContext().log("Script failure", e);
      throw new InterpreterException(e.toString());
    }
  }

  @SuppressWarnings("unchecked")
  private <T extends Serializable> T copy(T t) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ObjectOutputStream objOut = new ObjectOutputStream(out);
      objOut.writeObject(t);
      ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
      ObjectInputStream objIn = new ObjectInputStream(in);
      return (T) objIn.readObject();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
