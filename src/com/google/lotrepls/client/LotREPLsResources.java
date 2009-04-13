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

package com.google.lotrepls.client;

/**
 * This is a convenience class for keeping track of resources needed by the UI.
 */
public class LotREPLsResources {

  public static final String motd() {
    return "<h1>Welcome to LotREPLs (source <a href=\"http://code.google.com/p/lotrepls/\">here</a>)! "
        + "Feel free to play around and tell your friends, "
        + "but be aware that everything might not quite work perfectly. :)<br/></h1>"
        + " <br/>"
        + "Enter commands at the prompt and hit CTRL+ENTER to evaluate.  Hit CTRL+UP "
        + "and CTRL+DOWN to navigate your language-specific history.  Hit CTRL+SPACE to switch "
        + "languages or use the metacommand '/switch', for example '/switch clojure' to start "
        + "coding in clojure.  Supported languages include: <br/>"
        + " <br/>"
        + "<ul>"
        + "<li>beanshell *</li>"
        + "<li>clojure</li>"
        + "<li>groovy *</li>"
        + "<li>javascript *</li>"
        + "<li>python *</li>"
        + "<li>ruby *</li>"
        + "<li>scala *</li>"
        + "<li>scheme</li>"
        + "</ul>"
        + "<br/>"
        + "<h3>* These runtimes will not preserve state between requests (yet). "
        + "Click <a href=\"http://code.google.com/p/lotrepls/issues/list\">here</a> for details</h3>"
        + " <br/>"
        + "If you want to share a nifty script, first evaluate it, then double-click it."
        + " <br/>";
  }
}
