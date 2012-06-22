/*
 * Copyright (C) 2011 eXo Platform SAS.
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
 */
package org.exoplatform.ide.editor.ckeditor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorCapability;
import org.exoplatform.ide.editor.api.EditorParameters;
import org.exoplatform.ide.editor.api.SelectionRange;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.event.EditorFocusReceivedEvent;
import org.exoplatform.ide.editor.api.event.EditorHotKeyPressedEvent;
import org.exoplatform.ide.editor.api.event.EditorInitializedEvent;
import org.exoplatform.ide.editor.text.Document;
import org.exoplatform.ide.editor.text.IDocument;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmitry Nochevnov</a>
 * @version $
 */

public class CKEditor extends Editor
{
   protected String editorId;

   private Label label;

   private JavaScriptObject editorObject;

   private int onContentChangeListenerId;

   private int onEditorResizeListenerId;

   private String prefix = "";

   private String suffix = "";

   private CKEditorConfiguration configuration;

   public CKEditor(String content, Map<String, Object> params, HandlerManager eventBus)
   {
      super(content, params, eventBus);
      this.editorId = "CKEditor - " + String.valueOf(this.hashCode());

      if (params == null)
      {
         params = new HashMap<String, Object>();
      }

      label = new Label();
      DOM.setElementAttribute(label.getElement(), "id", getEditorId());
      DOM.setElementAttribute(label.getElement(), "style", "overflow: auto; width: 100%; height: 100%;"); // to show scrollbars
                                                                                                          // and to display on the
                                                                                                          // full tab
      add(label);

      if (params.get(EditorParameters.CONFIGURATION) != null)
         configuration = (CKEditorConfiguration)params.get(EditorParameters.CONFIGURATION);
      else
         configuration = new CKEditorConfiguration();

      // switch on CKEditor fullPage mode only for html-files
      if (getMimeType().equals(MimeType.TEXT_HTML))
      {
         CKEditorConfiguration.setFullPage(true);
      }
   }

   protected void onLoad()
   {
      try
      {
         super.onLoad();
         editorObject = initCKEditor(getEditorId(),
               CKEditorConfiguration.BASE_PATH,
               CKEditorConfiguration.TOOLBAR.toString(), // aditional default configuration can be found in config.js
               CKEditorConfiguration.THEME.toString(), CKEditorConfiguration.SKIN.toString(),
               CKEditorConfiguration.LANGUAGE.toString(), CKEditorConfiguration.CONTINUOUS_SCANNING,
               CKEditorConfiguration.isFullPage());
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }      
   }

   private native JavaScriptObject initCKEditor(String id, String basePath, String toolbar, String theme, String skin,
      String language, int continuousScanning, boolean fullPage)
   /*-{
        var instance = this;
        if (toolbar !== undefined)
        {
           $wnd.CKEDITOR.config.toolbar = toolbar;
        }
   
        if (theme !== undefined)
        {
           $wnd.CKEDITOR.config.theme = theme;
        }
   
        if (language !== undefined)
        {
           $wnd.CKEDITOR.config.language = language;
        }
   
        if (basePath !== undefined)
        {
           $wnd.CKEDITOR.basePath = basePath;
           $wnd.CKEDITOR.config.contentsCss = basePath + "contents.css"; // reflects the CSS used in the final pages where the contents are to be used.
           $wnd.CKEDITOR.plugins.basePath = basePath + "plugins/"; // set base path to the plugins folder
           $wnd.CKEDITOR.config.templates_files[0] = basePath + "plugins/templates/templates/default.js"; // set default template path
           $wnd.CKEDITOR.config.smiley_path = basePath + "plugins/smiley/images/"; // The base path used to build the URL for the smiley images.
        }
   
        if (skin !== undefined)
        {
           $wnd.CKEDITOR.config.skin = skin + ',' + basePath + 'skins/' + skin + '/';
        }
   
        if (fullPage !== undefined)
        {
           $wnd.CKEDITOR.config.fullPage = fullPage;
        }
   
        // create editor instance      
        var editor = $wnd.CKEDITOR.appendTo(id, $wnd.CKEDITOR.config);
        
        // add listeners
        if (editor !== null)
        {
           // init editor content variable
           editor.exoSavedContent = "";
   
           // set onContentChangeListener
           editor.exoChangeFunction = function()
           {
              // check if content was changed
              if (editor.checkDirty())
              {
                 editor.resetDirty();
                 if (editor.getData() != editor.exoSavedContent)
                 {
                    editor.exoSavedContent = editor.getData();
                    instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onContentChanged()();
                 }
              }
           }
           
           instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onContentChangeListenerId = $wnd.setInterval(editor.exoChangeFunction, continuousScanning);
   
           // add Hot Keys Listener
           instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::setHotKeysClickListener(Lcom/google/gwt/core/client/JavaScriptObject;)(editor);
           
           // add onFocus listener
           editor.onFocusReceived = function()
           {
              instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onFocusReceived()();
           }
           editor.on('focus', editor.onFocusReceived);
   
           // set init callback
           editor.exoInitCallback = function()
           {
              instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onInitialized()();
           }
   
            editor.on('instanceReady', editor.exoInitCallback);
        }
   
        editor.exoNativeAlert = $wnd.alert;
        editor.exoNativeConfirm = $wnd.confirm;
        instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::overrideNativeAlertAndConfirm()();
   
        return editor;
   }-*/;

   private void onContentChanged()
   {
      eventBus.fireEvent(new EditorContentChangedEvent(getEditorId()));
   }

   private void onCursorActivity()
   {
      eventBus.fireEvent(new EditorCursorActivityEvent(getEditorId()));
   }

   private void onFocusReceived()
   {
      eventBus.fireEvent(new EditorFocusReceivedEvent(getEditorId()));
   }

   private void onInitialized()
   {
      setText(content);
      eventBus.fireEvent(new EditorInitializedEvent(getEditorId()));
   }

   public String getText()
   {
      // replace "\t" delimiter on space symbol
      return getTextNative().replace("\t", " ");
   }

   public native String getTextNative()
   /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null)
      {
         editor.exoSavedContent = editor.getData();
         return this.@org.exoplatform.ide.editor.ckeditor.CKEditor::prefix
            + editor.exoSavedContent
            + this.@org.exoplatform.ide.editor.ckeditor.CKEditor::suffix;
      }
   }-*/;

   public String extractHtmlCodeFromGoogleGadget(String text)
   {
      this.prefix = GoogleGadgetParser.getPrefix(text);
      String content = GoogleGadgetParser.getContentSection(text);
      this.suffix = GoogleGadgetParser.getSuffix(text);
      return content;
   };

   public void setText(String text)
   {
      // removed odd "\r" symbols
      text = text.replace("\r", "");

      // extract CDATA section from google gadget
      if (getMimeType().equals(MimeType.GOOGLE_GADGET))
      {
         this.prefix = this.suffix = "";

         // test if it is possible to localize CDATA section
         if (GoogleGadgetParser.hasContentSection(text))
         {
            // extract HTML-code from <Content> tag
            text = this.extractHtmlCodeFromGoogleGadget(text);
         }
      }

      this.setData(text);
   }

   private native void setEditorMode(String mode)
   /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null)
      {
         editor.setMode(mode);
      }
   }-*/;

   private native void setData(String data)
   /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null)
      {
         editor.setData(data, function()
         {
            editor.checkDirty(); // reset ckeditor content changed indicator (http://docs.cksource.com/ckeditor_api/symbols/CKEDITOR.editor.html#setData)
         });

         editor.exoSavedContent = data;
         editor.focus();
      }
   }-*/;

   public native void undo()
   /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null)
      {
         editor.execCommand("undo");
      }
   }-*/;

   public native void redo()
   /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null)
      {
         editor.execCommand("redo");
      }
   }-*/;

   @Deprecated
   public native void formatSource()
   /*-{
   }-*/;

   public native void replaceText(String text)
   /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null)
      {
         // TODO
      }
   }-*/;

   @Deprecated
   public native void setLineNumbers(boolean showLineNumbers)
   /*-{
   }-*/;

   public native void setFocus()
   /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      var instance = this;
      if (editor != null)
      {
         $wnd.setTimeout(function(a, b)
         {
            editor.focus();
            instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onFocusReceived()();
         }, 200);
      }
   }-*/;

   public native boolean hasRedoChanges()
   /*-{
      return true;
   }-*/;

   public native boolean hasUndoChanges()
   /*-{
      return true;
   }-*/;

   public boolean canFormatSource()
   {
      return false;
   }

   public boolean canSetLineNumbers()
   {
      return false;
   }

   /*
    * remove listeners and restore functions
    */
   protected void onUnload()
   {
      removeEditorListeners();
      removeOnContentChangeListener();
      removeOnEditorResizeListener();
      restoreNativeAlertAndConfirm();
   }

   private native void restoreNativeAlertAndConfirm()
   /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (typeof editor.exoNativeAlert === "function" && typeof editor.exoNativeConfirm === "function")
      {
         $wnd.alert = editor.exoNativeAlert;
         $wnd.confirm = editor.exoNativeConfirm;
      }
   }-*/;

   private native void removeOnContentChangeListener()
   /*-{
      var onContentChangeListenerId = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::onContentChangeListenerId;
      if (onContentChangeListenerId !== null)
      {
         $wnd.clearInterval(onContentChangeListenerId);
      }
   }-*/;

   private native void removeOnEditorResizeListener()
   /*-{
      var onEditorResizeListenerId = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::onEditorResizeListenerId;
      if (onEditorResizeListenerId !== null)
      {
         $wnd.clearInterval(onEditorResizeListenerId);
      }
   }-*/;

   @Override
   public void setHeight(String height)
   {
      super.setHeight(height);
      setHeightNative(height);
   }

   /*
    * set editor height
    */
   public native void setHeightNative(String height)
   /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor !== null)
      {
         editor.resize("100%", height);
      }
   }-*/;

   private native void removeEditorListeners()
   /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor !== null)
      {
         // remove 'instanceReady' listener
         if (editor.hasListeners('instanceReady'))
         {
            editor.removeListener('instanceReady', editor.exoInitCallback)
         }

         if (editor.hasListeners('key'))
         {
            editor.removeListener('key', editor.exoHotKeysClickListener);
         }
      }
   }-*/;

   public boolean isReadOnly()
   {
      return (Boolean)params.get(EditorParameters.IS_READ_ONLY);
   }

   public int getLabelOffsetHeight()
   {
      return label.getOffsetHeight();
   }

   private static void showErrorDialog(String title, String message)
   {
      Dialogs.getInstance().showError(title, message);
   }

   /**
    * replace window.alert() function on org.exoplatform.gwtframework.ui.client.dialogs.Dialogs.showError() and hide
    * window.confirm() function
    * */
   private native void overrideNativeAlertAndConfirm()
   /*-{
      (function()
      {
         var proxied = $wnd.alert;
         $wnd.alert = function(message)
         {
            // test if this is a in context of ckeditor
            if (typeof $wnd.CKEDITOR !== "undefined")
            {
               @org.exoplatform.ide.editor.ckeditor.CKEditor::showErrorDialog(Ljava/lang/String;Ljava/lang/String;)("WYSIWYG Editor Error",message);
            }
            else
            {
               return proxied(message);
            }
         };
      })(this);

      (function()
      {
         var proxied = $wnd.confirm;

         $wnd.confirm = function(message)
         {
            // test if this is a ckeditor
            if (typeof $wnd.CKEDITOR !== "undefined")
            {
               return true;
            }
            else
            {
               return proxied(message);
            }
         };
      })();
   }-*/;

   public boolean canDeleteCurrentLine()
   {
      return false;
   }

   public boolean canFindAndReplace()
   {
      return false;
   }

   public boolean canGoToLine(int lineNumber)
   {
      return false;
   }

   public boolean canGoToLine()
   {
      return false;
   }

   public void deleteCurrentLine()
   {
   }

   public void goToLine(int lineNumber)
   {
   }

   public void goToPosition(int row, int column)
   {
   }

   public int getCursorCol()
   {
      return 0;
   }

   public int getCursorRow()
   {
      return 0;
   }

   public boolean findAndSelect(String find, boolean caseSensitive)
   {
      return false;
   }

   public void replaceFoundedText(String find, String replace, boolean caseSensitive)
   {
   }

   private HandlerManager getEventBus()
   {
      return eventBus;
   }

   private CKEditorConfiguration getConfiguration()
   {
      return configuration;
   }
   
   private boolean handleShortcut(boolean isCtrl, boolean isAlt, boolean isShift, int keyCode)
   {      
      EditorHotKeyPressedEvent event = new EditorHotKeyPressedEvent(isCtrl, isAlt, isShift, keyCode);
      eventBus.fireEvent(event);
      return event.isHotKeyHandled();
   }   

   private void logMessage(String message)
   {
      System.out.println(message);
   }
   
   /**
    * Set listeners of hot keys clicking
    */
   private native void setHotKeysClickListener(JavaScriptObject editor)
   /*-{      
      var instance = this;
      
      try
      {
         editor.exoHotKeysClickListener = function(event)
         {
            var keyCode = event.data.keyCode;            
            var isShift = false;
            var isAlt = false;
            var isCtrl = false;
            
            if (event.data.keyCode > $wnd.CKEDITOR.ALT)
            {
               isAlt = true;
               keyCode = keyCode - $wnd.CKEDITOR.ALT;
            }
            
            if (event.data.keyCode > $wnd.CKEDITOR.SHIFT)
            {
               isShift = true;
               keyCode = keyCode - $wnd.CKEDITOR.SHIFT;
            }
         
            if (event.data.keyCode > $wnd.CKEDITOR.CTRL)
            {
               isCtrl = true;
               keyCode = keyCode - $wnd.CKEDITOR.CTRL;
            }
            
            var stopEvent = instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::handleShortcut(ZZZI)(isCtrl, isAlt, isShift, keyCode);
            if (stopEvent)
            {
               event.cancel();
               return false;
            }
            
            instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onCursorActivity()();
         }
         
         editor.on('key', editor.exoHotKeysClickListener);         
      }
      catch (e)
      {
         instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::logMessage(Ljava/lang/String;)("" + e.name + " exception. " + e.message);
      }
   }-*/;
   
   
   public boolean canCreateTokenList()
   {
      return false;
   }

   public List<Token> getTokenList()
   {
      return null;
   }

   @Override
   public String getEditorId()
   {
      return editorId;
   }

   @Override
   public void insertImportStatement(String fqn)
   {
   }

   @Override
   public boolean isCapable(EditorCapability capability)
   {
      switch (capability)
      {
         default :
            return false;
      }
   }

   @Override
   public void replaceTextAtCurrentLine(String line, int cursorPosition)
   {
   }

   @Override
   public void showLineNumbers(boolean showLineNumbers)
   {
   }

   public String getMimeType()
   {
      return (String)params.get(EditorParameters.MIME_TYPE);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getLineText(int)
    */
   @Override
   public String getLineText(int line)
   {
      return null;
   }

   @Override
   public void getTokenListInBackground()
   {
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getDocument()
    */
   @Override
   public IDocument getDocument()
   {
      return new Document(getText());
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getSelectionRange()
    */
   @Override
   public SelectionRange getSelectionRange()
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#selectAll()
    */
   @Override
   public native void selectAll()
   /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null)
      {
         editor.execCommand("SelectAll");
      }
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#cut()
    */
   @Override
   public native void cut()
   /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null)
      {
         editor.execCommand("cut");
      }
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#copy()
    */
   @Override
   public native void copy()
   /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null)
      {
         editor.execCommand("copy");
      }
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#paste()
    */
   @Override
   public native void paste()
   /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null)
      {
         editor.execCommand("paste");
      }
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#delete()
    */
   @Override
   public native void delete()
   /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null)
      {
         editor.execCommand("delete");
      }
   }-*/;

   @Override
   public void setLineText(int line, String text)
   {
   }

   @Override
   public int getNumberOfLines()
   {
      return 0;
   }

   @Override
   public void selectRange(int startLine, int startChar, int endLine, int endChar)
   {
   }
   
}
