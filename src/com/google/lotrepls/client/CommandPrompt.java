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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.lotrepls.shared.InterpreterType;

import java.util.List;

/**
 * This class manages the prompt and input area. When the user indicates that
 * they wish to evaluate the script a CommandEnteredEvent is fired at all
 * registered listeners. A CommandPrompt can also create a read-only copy of
 * itself to leave for history.
 */
public class CommandPrompt {
  /**
   * Simple listener interface for user-entered commands.
   */
  public interface CommandEnteredCallback {
    public void onCommandEntered(InterpreterType type, String script);
  }

  private CommandEnteredCallback commandEnteredCallback;

  private final HorizontalPanel panel;
  private Label prompt;
  private TextArea inputArea;
  private SimplePanel inputAreaDiv;
  private InterpreterType type = InterpreterType.Scheme;

  private CommandHistory history;
  private int currentHistoryIdx = 0;

  public CommandPrompt(String language, CommandEnteredCallback callback) {
    panel = new HorizontalPanel();
    prompt = new Label();
    prompt.setStyleName("prompt");
    panel.add(prompt);

    prompt.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        showSelectorListBox();
      }
    });

    inputArea = new TextArea();
    inputArea.setStyleName("script");
    inputArea.setVisibleLines(2);
    inputAreaDiv = new SimplePanel();
    inputAreaDiv.add(inputArea);
    inputAreaDiv.getElement().getStyle().setProperty("width", "100%");
    panel.add(inputAreaDiv);
    panel.setCellWidth(inputAreaDiv, "100%");

    updatePromptText();

    inputArea.addKeyDownHandler(new KeyDownHandler() {
      public void onKeyDown(KeyDownEvent event) {
        onInputAreaKeyDown(event);
      }
    });

    history = new CommandHistory();
    setLanguage(language);
    commandEnteredCallback = callback;
  }

  public Widget panel() {
    return panel;
  }

  public void claimFocus() {
    inputArea.setFocus(true);
  }

  public void setLanguage(String lang) {
    languageSelected(lang);
  }

  public void setScript(String script) {
    inputArea.setText(script);
    resizeInputArea(true);
  }

  public void clearInputArea() {
    inputArea.setText("");
    inputArea.setVisibleLines(1);
  }

  private static String buildUrl(InterpreterType t, String script) {
    return Location.getHref() + "?language=" + t.name() + "&script="
        + URL.encode(script);
  }

  private static String trimUrl(String s) {
    if (s.length() < 78)
      return s;
    return s.substring(0, 78) + "...";
  }

  /**
   * This creates an immutable copy of the prompt and input area suitable for
   * adding to the page.
   */
  public Widget createImmutablePanel() {
    HorizontalPanel panelCopy = new HorizontalPanel();

    Label promptCopy = new Label(prompt.getText());
    promptCopy.setStyleName(prompt.getStyleName());
    promptCopy.getElement().getStyle().setProperty(
        "width", prompt.getElement().getStyle().getProperty("width"));
    panelCopy.add(promptCopy);

    final InterpreterType t = type;
    final String scriptText = inputArea.getText();

    TextArea inputAreaCopy = new TextArea() {
      {
        this.addDomHandler(new DoubleClickHandler() {
          public void onDoubleClick(DoubleClickEvent event) {
            final DialogBox box = new DialogBox();
            VerticalPanel boxPanel = new VerticalPanel();

            boxPanel.add(new Label(
                "Use the following URL to share this script with friends:"));

            String url = buildUrl(t, scriptText);

            boxPanel.add(new Anchor(trimUrl(url), url));

            Button close = new Button("Close");
            close.addClickHandler(new ClickHandler() {
              public void onClick(ClickEvent event) {
                box.hide();
              }
            });
            close.setStyleName("closeButton");

            boxPanel.add(close);
            box.add(boxPanel);

            box.getElement().getStyle().setProperty("border", "1px solid");
            box.getElement().getStyle().setProperty("borderColor", "green");
            box.getElement().getStyle().setProperty("backgroundColor", "black");
            box.center();
          }
        }, DoubleClickEvent.getType());
      }
    };
    inputAreaCopy.setStyleName(inputArea.getStyleName());
    resizeInputArea(true);
    inputAreaCopy.setText(scriptText);
    inputAreaCopy.setVisibleLines(inputArea.getVisibleLines());
    inputAreaCopy.setReadOnly(true);

    SimplePanel inputAreaDivCopy = new SimplePanel();

    inputAreaDivCopy.add(inputAreaCopy);

    inputAreaDivCopy.getElement().setAttribute("style",
        inputAreaDiv.getElement().getAttribute("style"));

    panelCopy.add(inputAreaDivCopy);
    panelCopy.setCellWidth(inputAreaDivCopy, "100%");

    return panelCopy;
  }

  private void showSelectorListBox() {
    panel.remove(0);
    final ListBox lb = new ListBox();
    for (InterpreterType t : InterpreterType.values()) {
      if (t != InterpreterType.PC) { // Don't show the easter egg language!
        lb.addItem(t.name());
        if (t == type) {
          lb.setSelectedIndex(lb.getItemCount() - 1);
        }
      }
    }
    lb.setStyleName("prompt");
    lb.addChangeHandler(new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        languageSelected(lb.getItemText(lb.getSelectedIndex()));
      }
    });
    lb.addBlurHandler(new BlurHandler() {
      @Override
      public void onBlur(BlurEvent event) {
        languageSelected(lb.getItemText(lb.getSelectedIndex()));
      }
    });
    panel.insert(lb, 0);
    lb.setFocus(true);
  }

  private void languageSelected(String newLanguage) {
    panel.remove(0);
    switchType(newLanguage);
    updatePromptText();
    panel.insert(prompt, 0);
    claimFocus();
  }
  
  /*
   * This is a hacky way to see how many pixels wide a string of text would be
   * if it was added to the document.  This works in our case because a string
   * in a <span> attached directly to the <body> has the same font as it will
   * in the prompt, but in general this won't do what you want.
   */
  private int measureTextWidthInPixels(String text) {
    SpanElement span = Document.get().createSpanElement();
    RootPanel.get().getElement().appendChild(span);
    span.setAttribute("position", "absolute");
    span.setInnerText(text);
    int width = span.getOffsetWidth();
    RootPanel.get().getElement().removeChild(span);
    return width;
  }
  
  private void updatePromptText() {
    String promptText = type.name() + " >>>";
    prompt.setText(promptText);
    prompt.getElement().getStyle().setPropertyPx(
        "width", measureTextWidthInPixels(promptText) + 5);
  }

  private void processScript() {
    currentHistoryIdx = 0;

    final String script = inputArea.getText();
    if (script.startsWith("/")) {
      processMetaCommand(script.substring(1));
      inputArea.setText("");
      inputArea.setVisibleLines(1);
      return;
    }

    history.addCommand(type, script);

    commandEnteredCallback.onCommandEntered(type, script);
  }

  private void historyPrevious() {
    List<String> langHistory = history.historyForLanguage(type);
    inputArea.setText(langHistory.get(currentHistoryIdx));
    currentHistoryIdx = Math.min(currentHistoryIdx + 1, langHistory.size() - 1);
    resizeInputArea(true);
  }

  private void historyNext() {
    if (currentHistoryIdx == 0) {
      inputArea.setText("");
      return;
    }
    currentHistoryIdx = Math.max(currentHistoryIdx - 1, 0);
    List<String> langHistory = history.historyForLanguage(type);
    inputArea.setText(langHistory.get(currentHistoryIdx));
    resizeInputArea(true);
  }

  private void processMetaCommand(final String command) {
    if (command.startsWith("switch ")) {
      switchType(command.substring("switch ".length()).trim());
      updatePromptText();
      currentHistoryIdx = 0;
    } else {
      // TODO(jamesr): handle unknown command
    }
  }

  private void switchType(String newType) {
    if (newType != null) {
      for (InterpreterType t : InterpreterType.values()) {
        if (newType.equalsIgnoreCase(t.name())) {
          type = t;
          return;
        }
      }
    }
  }

  private void resizeInputArea(boolean shrink) {
    final String s = inputArea.getText();
    int numNewlines = s.split("\\n").length;

    inputArea.setVisibleLines(shrink ? numNewlines + 1 : Math.max(
        inputArea.getVisibleLines(), numNewlines + 1));
    Window.scrollTo(0, 100000);
  }

  private void onInputAreaKeyDown(KeyDownEvent event) {
    switch (event.getNativeKeyCode()) {
      case KeyCodes.KEY_ENTER:
        if (event.isControlKeyDown()) {
          processScript();
        } else {
          resizeInputArea(false);
        }
        break;
      case KeyCodes.KEY_UP:
        if (event.isControlKeyDown()) {
          historyPrevious();
          event.preventDefault();
        }
        break;
      case KeyCodes.KEY_DOWN:
        if (event.isControlKeyDown()) {
          historyNext();
          event.preventDefault();
        }
        break;
      case ' ':
        if (event.isControlKeyDown()) {
          showSelectorListBox();
          event.preventDefault();
        }
        break;
      default:
        break;
    }
  }
}
