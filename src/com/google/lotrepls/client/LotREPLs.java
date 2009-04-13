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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.lotrepls.client.CommandPrompt.CommandEnteredCallback;
import com.google.lotrepls.shared.LotREPLsApi;
import com.google.lotrepls.shared.LotREPLsApiAsync;
import com.google.lotrepls.shared.InterpreterException;
import com.google.lotrepls.shared.InterpreterType;

/**
 * Entry point class for the UI. This sets up the command prompt area and sets
 * up the event handlers.
 */
public class LotREPLs implements EntryPoint {

  private final LotREPLsApiAsync api = GWT.create(LotREPLsApi.class);

  private final FlowPanel content = new FlowPanel();

  private CommandPrompt commandPrompt;

  private void showMotd() {
    SimplePanel motd = new SimplePanel();
    motd.setStyleName("motd");
    motd.getElement().setInnerHTML(LotREPLsResources.motd());
    content.add(motd);
  }

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    showMotd();

    String language = Location.getParameter("language");

    commandPrompt = new CommandPrompt(language, new CommandEnteredCallback() {
      /**
       * The command entered handler does a little switch-a-roo. It removes the
       * input area and prompt, replacing them with immutable copies, and then
       * waits for the response. Once the script results (or error) are ready,
       * it inserts a result area and then re-adds the command prompt.
       */
      public void onCommandEntered(InterpreterType type, String script) {
        content.remove(commandPrompt.panel());

        Widget enteredScript = commandPrompt.createImmutablePanel();
        content.add(enteredScript);

        commandPrompt.clearInputArea();

        Window.scrollTo(0, 100000);

        try {
          api.eval(type, script, new ScriptCallback());
        } catch (InterpreterException e) {
          setResult(e.getMessage(), false);
        }
      }
    });

    String script = Location.getParameter("script");
    if (script != null && !script.equals("")) {
      commandPrompt.setScript(script);
    }

    content.add(commandPrompt.panel());

    content.setWidth("100%");
    RootPanel.get().add(content);
    commandPrompt.claimFocus();
  }

  private class ScriptCallback implements AsyncCallback<String> {
    public void onFailure(Throwable caught) {
      if (caught instanceof InterpreterException) {
        setResult("Interpreter exception: " + caught, false);
      } else {
        setResult("Something bad happened - click your heels three times and try again", false);
      }
    }

    public void onSuccess(String result) {
      setResult(result, true);
    }
  }

  private void setResult(String result, boolean succeeded) {
    Element e = Document.get().createPreElement();
    e.setInnerText(result);
    e.setClassName(succeeded ? "script" : "error");
    e.setAttribute("tabIndex", "-1");
    content.getElement().appendChild(e);
    content.add(commandPrompt.panel());
    commandPrompt.claimFocus();
    Window.scrollTo(0, 100000);
  }
}
