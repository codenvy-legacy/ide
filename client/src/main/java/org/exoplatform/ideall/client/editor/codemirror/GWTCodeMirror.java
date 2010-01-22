/**
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */

package org.exoplatform.ideall.client.editor.codemirror;

import org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorActivityEvent;
import org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorContentChangedEvent;
import org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorInitializedEvent;
import org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorSaveContentEvent;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GWTCodeMirror extends Composite
{

   private TextArea textArea;

   private CodeMirrorConfig configuration;

   private String id;

   private String jsDirectory = CodeMirrorConfig.PATH + "js/";

   private JavaScriptObject editorObject;

   private HandlerManager eventBus;

   private AbsolutePanel panel;

   public GWTCodeMirror(HandlerManager eventBus)
   {
      this(eventBus, new CodeMirrorConfig());
   }

   public GWTCodeMirror(HandlerManager eventBus, CodeMirrorConfig configuration)
   {
      this.eventBus = eventBus;
      this.configuration = configuration;
      initWidget();
      setWidth("100%");
      setHeight("100%");
   }

   private void initWidget()
   {
      id = configuration.getId();
      //VerticalPanel panel = new VerticalPanel();
      panel = new AbsolutePanel();
      DOM.setElementAttribute(panel.getElement(), "id", "my-generated-id-" + Random.nextInt());
      panel.setWidth(configuration.getWidth());
      panel.setHeight(configuration.getHeight());

      textArea = new TextArea();
      DOM.setElementAttribute(textArea.getElement(), "id", id);
      panel.add(textArea);
      initWidget(panel);
   }

   protected void onLoad()
   {
      super.onLoad();
      editorObject = initCodeMirror(configuration, this);
   }

   public void setWidth(int width)
   {
      super.setWidth("" + width + "px");
      panel.setWidth("" + width + "px");

      //      int childs = DOM.getChildCount(panel.getElement());
      //
      //      for (int i = 0; i < childs; i++)
      //      {
      //         Element c = DOM.getChild(panel.getElement(), i);
      //         if ("DIV".equals(c.getNodeName()))
      //         {
      //            DOM.setStyleAttribute(c, "width", "" + width + "px");
      //            updateWidthOfIFrame(c, width);
      //         }
      //      }

      hack3Parent();
   }

   public void setHeight(int height)
   {
      super.setHeight("" + height + "px");
      panel.setHeight("" + height + "px");
   }

   //   private void updateWidthOfIFrame(Element parentElement, int width)
   //   {
   //      int childs = DOM.getChildCount(parentElement);
   //
   //      for (int i = 0; i < childs; i++)
   //      {
   //         Element c = DOM.getChild(parentElement, i);
   //         if ("IFRAME".equalsIgnoreCase(c.getNodeName()))
   //         {
   //            DOM.setStyleAttribute(c, "width", "" + width + "px");
   //            updateWidthOfIFrame(c, width);
   //         }
   //      }
   //   }

   private void hack3Parent()
   {
      try
      {
         Element el = panel.getElement();
         Element p1 = DOM.getParent(el);
         Element p2 = DOM.getParent(p1);
         Element p3 = DOM.getParent(p2);
         DOM.setStyleAttribute(p3, "overflow", "visible");
      }
      catch (Exception e)
      {
         //e.printStackTrace();
      }
   }

   private native JavaScriptObject initCodeMirror(CodeMirrorConfig config, GWTCodeMirror instance) /*-{
        var id = config.@org.exoplatform.ideall.client.editor.codemirror.CodeMirrorConfig::getId()();
        var w = config.@org.exoplatform.ideall.client.editor.codemirror.CodeMirrorConfig::getWidth()();
        var h = config.@org.exoplatform.ideall.client.editor.codemirror.CodeMirrorConfig::getHeight()();
        var readOnly = config.@org.exoplatform.ideall.client.editor.codemirror.CodeMirrorConfig::isReadOnly()();
        var cs = config.@org.exoplatform.ideall.client.editor.codemirror.CodeMirrorConfig::getContinuousScanning()();
        var lineNumbers = config.@org.exoplatform.ideall.client.editor.codemirror.CodeMirrorConfig::isLineNumbers()();
        var tr = config.@org.exoplatform.ideall.client.editor.codemirror.CodeMirrorConfig::isTextWrapping()();
        
        var styleURLs = config.@org.exoplatform.ideall.client.editor.codemirror.CodeMirrorConfig::getStyleUrl()();
        var parserNames = config.@org.exoplatform.ideall.client.editor.codemirror.CodeMirrorConfig::getParserNames()();
   
        var changeFunction = function() {
           instance.@org.exoplatform.ideall.client.editor.codemirror.GWTCodeMirror::onContentChanged()();
        }
   
        var saveFunction = function() {
          instance.@org.exoplatform.ideall.client.editor.codemirror.GWTCodeMirror::onSaveContent()();
        }
        
        var cursorActivity = function() {
          instance.@org.exoplatform.ideall.client.editor.codemirror.GWTCodeMirror::onCursorActivity()();
        }
   
        var initCallback = function() {
          instance.@org.exoplatform.ideall.client.editor.codemirror.GWTCodeMirror::onInitialized()();
        }
   
        var editor = $wnd.CodeMirror.fromTextArea(id, {
            width: w,
            height: h,
            parserfile: eval(parserNames),
            stylesheet: eval(styleURLs),
            path: this.@org.exoplatform.ideall.client.editor.codemirror.GWTCodeMirror::jsDirectory,
            continuousScanning: cs,
            lineNumbers: lineNumbers,
            readOnly: readOnly,
            textWrapping: tr,
            tabMode: "spaces",
            content: " ",     // to fix bug with blocked deleting function of CodeMirror just after opening file [WBT-223]
            onChange: changeFunction,
            saveFunction: saveFunction,
            cursorActivity: cursorActivity,
            initCallback: initCallback
          });
          
        return editor;
     }-*/;

   private void onContentChanged()
   {
      eventBus.fireEvent(new CodeMirrorContentChangedEvent(id));
   }

   private void onSaveContent()
   {
      eventBus.fireEvent(new CodeMirrorSaveContentEvent());
   }

   private void onCursorActivity()
   {
      eventBus.fireEvent(new CodeMirrorActivityEvent());
   }

   private void onInitialized()
   {
      eventBus.fireEvent(new CodeMirrorInitializedEvent(configuration.getId()));
   }

   public native String getText()/*-{
      var editor = this.@org.exoplatform.ideall.client.editor.codemirror.GWTCodeMirror::editorObject; 
      return editor.getCode();
   }-*/;

   public native void setText(String text)/*-{
      var editor = this.@org.exoplatform.ideall.client.editor.codemirror.GWTCodeMirror::editorObject;
      $wnd.setTimeout(function(a, b){
         editor.setCode(text);
         editor.focus();         
      }, 200);
   }-*/;
  
   public native void undo()/*-{
      var editor = this.@org.exoplatform.ideall.client.editor.codemirror.GWTCodeMirror::editorObject;
      editor.undo();
   }-*/;

   public native void redo()/*-{
      var editor = this.@org.exoplatform.ideall.client.editor.codemirror.GWTCodeMirror::editorObject;
      editor.redo();
   }-*/;

   public native void reindentEditor()/*-{
      if (this.@org.exoplatform.ideall.client.editor.codemirror.GWTCodeMirror::getText()() != ' ') {
         var editor = this.@org.exoplatform.ideall.client.editor.codemirror.GWTCodeMirror::editorObject;
         editor.reindent();
      }
   }-*/;

   public native void replaceText(String text)/*-{
      var editor = this.@org.exoplatform.ideall.client.editor.codemirror.GWTCodeMirror::editorObject;
      editor.replaceSelection(text);
   }-*/;

   public native void setLineNumbers(boolean isShowLineNumbers)/*-{
      var editor = this.@org.exoplatform.ideall.client.editor.codemirror.GWTCodeMirror::editorObject;
      if (editor == null) {
         return;
      }
      editor.setLineNumbers(isShowLineNumbers);            
   }-*/;

   public String getEditorId()
   {
      return id;
   }

   public native void setFocus()/*-{
      var editor = this.@org.exoplatform.ideall.client.editor.codemirror.GWTCodeMirror::editorObject;
      if (editor != null)
      {
         $wnd.setTimeout(function(a, b){
            editor.focus();         
         }, 200);
      }
   }-*/;

   public native boolean hasRedoChanges()/*-{
      var editor = this.@org.exoplatform.ideall.client.editor.codemirror.GWTCodeMirror::editorObject;
      if (editor == null) {
        return false;
      }
      
      if (editor.editor.history.redoHistory.length == null)
      {
         return false;
      }
      
      return editor.editor.history.redoHistory.length > 0
   }-*/;

   public native boolean hasUndoChanges()/*-{
      var editor = this.@org.exoplatform.ideall.client.editor.codemirror.GWTCodeMirror::editorObject;
      if (editor == null) {
        return false;
      }
      
      if (editor.editor.history.history.length == null)
      {
         return false;
      }
      
      return editor.editor.history.history.length > 0
   }-*/;   

}
