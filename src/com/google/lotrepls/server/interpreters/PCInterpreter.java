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

/**
 * A PC Interpreter.
 *
 */
public class PCInterpreter implements Interpreter {
  static final int MEM_SIZE = 32 * 1024;  // 32k should be enough for anyone.
  char mem[] = new char[MEM_SIZE];

  public void init(ServletContext context) {
  }

  public Context createContext() {
    return new Ctx();
  }

  private String interpret(final String script, final String input) {
    StringBuffer sb = new StringBuffer();
    int ptr = 0;
    int inputPtr = 0;
    for (int i = 0; i < script.length(); i++) {
      switch (script.charAt(i)) {
        case '>':
          ptr++;
          if (ptr >= MEM_SIZE) {
            throw new RuntimeException("Tape index > MEM_SIZE (32k)");
          }
          break;
        case '<':
          ptr--;
          if (ptr < 0) {
            throw new RuntimeException("Tape index < 0");
          }
          break;
        case '+':
          mem[ptr]++;
          break;
        case '-':
          mem[ptr]--;
          break;
        case '.':
          sb.append(mem[ptr]);
          break;
        case ',':
          if (inputPtr >= input.length()) {
            mem[ptr] = 0;
          } else {
            mem[ptr] = input.charAt(inputPtr++);
          }
          break;
        case '[':
          if (mem[ptr] == 0) {
            int recDep = 1;
            for (++i; recDep != 0; i++) {
              if (i >= script.length()) {
                throw new RuntimeException("Unmatched [");
              }
              if (script.charAt(i) == ']') {
                recDep--;
              }
              if (script.charAt(i) == '[') {
                recDep++;
              }
            }
          }
          break;
        case ']':
          if (mem[ptr] != 0) {
            int recDep = 1;
            for (--i; recDep != 0; i--) {
              if (i < 0) {
                throw new RuntimeException("Unmatched ]");
              }
              if (script.charAt(i) == ']') {
                recDep++;
              }
              if (script.charAt(i) == '[') {
                recDep--;
              }
            }
          }
          break;
        case '"':
          sb.append(Integer.toString(mem[ptr]));
          break;
        default:
          throw new RuntimeException("Unknown char in program: "
              + script.charAt(i));
      }
    }
    return sb.toString();
  }

  public String execute(Context context, String script) throws Exception {
    for (int i = 0; i < MEM_SIZE; i++)
      mem[i] = 0;
    int splitPoint = script.indexOf('!');
    if (splitPoint != -1) {
      return interpret(script.substring(0, splitPoint),
          script.substring(splitPoint + 1));
    } else {
      return interpret(script, "");
    }
  }

  static class Ctx implements Context {
  }
}
