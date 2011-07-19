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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorCapability;
import org.exoplatform.ide.editor.api.EditorParameters;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.event.EditorFocusReceivedEvent;
import org.exoplatform.ide.editor.api.event.EditorInitializedEvent;
import org.exoplatform.ide.editor.api.event.EditorSaveContentEvent;

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
   
   public CKEditor(String content, HashMap<String, Object> params, HandlerManager eventBus)
   {
      super(content, params, eventBus);
      this.editorId = "CKEditor - " + String.valueOf(this.hashCode());
      
      if (params == null)
      {
         params = new HashMap<String, Object>();
      }

      label = new Label();
      DOM.setElementAttribute(label.getElement(), "id", getEditorId());
      DOM.setElementAttribute(label.getElement(), "style", "overflow: auto; width: 100%; height: 100%;"); // to show scrollbars and to display on the full tab         
      add(label);

      if (params.get(EditorParameters.CONFIGURATION) != null)
         configuration = (CKEditorConfiguration) params.get(EditorParameters.CONFIGURATION);
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
      super.onLoad();
      editorObject =
         initCKEditor(getEditorId(),
            CKEditorConfiguration.BASE_PATH,
            CKEditorConfiguration.TOOLBAR.toString(), // aditional default configuration can be found in config.js
            CKEditorConfiguration.THEME.toString(), 
            CKEditorConfiguration.SKIN.toString(),
            CKEditorConfiguration.LANGUAGE.toString(), 
            CKEditorConfiguration.CONTINUOUS_SCANNING,
            CKEditorConfiguration.isFullPage());       
   }

   private native JavaScriptObject initCKEditor(String id, String basePath, String toolbar,
      String theme, String skin, String language, int continuousScanning, boolean fullPage) /*-{     
      var instance = this;
      if (toolbar !== undefined) {
         $wnd.CKEDITOR.config.toolbar = toolbar;
      }       

      if (theme !== undefined) {
         $wnd.CKEDITOR.config.theme = theme;
      }       
      
      if (language !== undefined) {
         $wnd.CKEDITOR.config.language = language;
      }              
      
      if (basePath !== undefined) {
         $wnd.CKEDITOR.basePath = basePath;
         $wnd.CKEDITOR.config.contentsCss = basePath + "contents.css";   // reflects the CSS used in the final pages where the contents are to be used.
         $wnd.CKEDITOR.plugins.basePath = basePath + "plugins/";     // set base path to the plugins folder
         $wnd.CKEDITOR.config.templates_files[0] = basePath + "plugins/templates/templates/default.js";   // set default template path
         $wnd.CKEDITOR.config.smiley_path = basePath + "plugins/smiley/images/";   // The base path used to build the URL for the smiley images.
      }       
      
      if (skin !== undefined) {
         $wnd.CKEDITOR.config.skin = skin + ',' + basePath + 'skins/' + skin + '/';
      }
      
      if (fullPage !== undefined) {
         $wnd.CKEDITOR.config.fullPage = fullPage;
      }    
      
      // create editor instance      
      var editor = $wnd.CKEDITOR.appendTo(id, $wnd.CKEDITOR.config);  
      
      // add listeners
      if (editor !== null) {                         
         // init editor content variable
         editor.exoSavedContent = ""; 
         
         // set onContentChangeListener
         editor.exoChangeFunction = function(){
           // check if content was changed
           if (editor.checkDirty()) {            
              editor.resetDirty();
              if ( editor.getData() != editor.exoSavedContent ) {
                 editor.exoSavedContent = editor.getData();
                 instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onContentChanged()();
              }               
           }
         }
         instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onContentChangeListenerId = $wnd.setInterval(editor.exoChangeFunction, continuousScanning);
         
         // add Hot Key Listener
         instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::setHotKeysClickListener(Lcom/google/gwt/core/client/JavaScriptObject;)(editor);
//         var Ctrl_s_keycode = $wnd.CKEDITOR.CTRL + 115;
//         var Ctrl_S_keycode = $wnd.CKEDITOR.CTRL + 83;          
//         editor.exoSaveFunction = function(e) {
//            // test if was pressed "Ctrl + S" or "Ctrl + s"
//            if (e.data.keyCode == Ctrl_s_keycode || e.data.keyCode == Ctrl_S_keycode) {
//              instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onSaveContent()();  // call onSaveContent() listener                
//              return false;  // this disables default action (submitting the form)
//            }
//         }
//         editor.on('key', editor.exoSaveFunction);
         
         // add onCursorActitvity listener
         editor.exoCursorActivity = function() {
           instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onCursorActivity()();               
         }
         editor.on('key', editor.exoCursorActivity);

         // add onFocus listener
         editor.onFocusReceived = function() {
           instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onFocusReceived()();               
         }
         editor.on('focus', editor.onFocusReceived);
         
                   
         // set init callback
         editor.exoInitCallback = function() {          
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

   private void onSaveContent()
   {
      eventBus.fireEvent(new EditorSaveContentEvent(getEditorId()));
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
      eventBus.fireEvent(new EditorInitializedEvent(getEditorId()));
      setText(content);
   }

   public String getText()
   {
      // replace "\t" delimiter on space symbol
      return getTextNative().replace("\t", " ");
   }
   
   public native String getTextNative()/*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null) {
         editor.exoSavedContent = editor.getData();
         return this.@org.exoplatform.ide.editor.ckeditor.CKEditor::prefix +
                editor.exoSavedContent +
                this.@org.exoplatform.ide.editor.ckeditor.CKEditor::suffix;
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
      text.replace("\r", "");
      
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
   
   private native void setEditorMode(String mode)/*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null) {
         editor.setMode(mode);
      }
   }-*/;

   private native void setData(String data)/*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null) {
         editor.setData(data, function()
         {
            editor.checkDirty();    // reset ckeditor content changed indicator (http://docs.cksource.com/ckeditor_api/symbols/CKEDITOR.editor.html#setData)
         });
                     
         editor.exoSavedContent = data;
         editor.focus();
      }
   }-*/;

   public native void undo()/*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null) {
         editor.execCommand("undo");
      }
   }-*/;

   public native void redo()/*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null) {
         editor.execCommand("redo");
      }
   }-*/;

   @Deprecated
   public native void formatSource()/*-{
   }-*/;

   public native void replaceText(String text)/*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (editor != null) {
         // TODO
      }
   }-*/;

   @Deprecated
   public native void setLineNumbers(boolean showLineNumbers)/*-{
      }-*/;

   public native void setFocus()/*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      var instance = this;
      if (editor != null) {
         $wnd.setTimeout(function(a, b){
            editor.focus();
            instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onFocusReceived()();
         }, 200);
      }
   }-*/;

   public native boolean hasRedoChanges()/*-{
      return true;
   }-*/;

   public native boolean hasUndoChanges()/*-{
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

   private native void restoreNativeAlertAndConfirm() /*-{
      var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
      if (typeof editor.exoNativeAlert === "function"
          && typeof editor.exoNativeConfirm === "function"
         )
      {
         $wnd.alert = editor.exoNativeAlert;
         $wnd.confirm = editor.exoNativeConfirm;
      }            
   }-*/;

   private native void removeOnContentChangeListener() /*-{
     var onContentChangeListenerId = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::onContentChangeListenerId;
     if (onContentChangeListenerId !== null) {
        $wnd.clearInterval(onContentChangeListenerId);      
     }
   }-*/;

   private native void removeOnEditorResizeListener() /*-{
     var onEditorResizeListenerId = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::onEditorResizeListenerId;
     if (onEditorResizeListenerId !== null) {
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
   public native void setHeightNative(String height) /*-{
     var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;      
     if (editor !== null) {
          editor.resize("100%", height);
     }
   }-*/;
   
   private native void removeEditorListeners() /*-{
     var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
     if (editor !== null) {
       // remove 'instanceReady' listener
       if (editor.hasListeners('instanceReady')) {
         editor.removeListener('instanceReady', editor.exoInitCallback)                
       }        
       
       // remove 'key' listeners       
       if (editor.hasListeners('key')) {
         editor.removeListener('key', editor.exoCursorActivity);          
       }
       
       if (editor.hasListeners('key')) {
         editor.removeListener('key', editor.exoHotKeysClickListener);          
       }        
     }   
   }-*/;

   public boolean isReadOnly()
   {
      return (Boolean) params.get(EditorParameters.IS_READ_ONLY);
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
    * replace window.alert() function on org.exoplatform.gwtframework.ui.client.dialogs.Dialogs.showError() and hide window.confirm() function
    * */
   private native void overrideNativeAlertAndConfirm() /*-{ 
      (function(){
         var proxied = $wnd.alert;
         $wnd.alert = function(message){
            // test if this is a in context of ckeditor
            if (typeof $wnd.CKEDITOR !== "undefined" ) {
               @org.exoplatform.ide.editor.ckeditor.CKEditor::showErrorDialog(Ljava/lang/String;Ljava/lang/String;)("WYSIWYG Editor Error",message);
            } else {
               return proxied(message);
            }
         };
      })(this);
     
      (function(){
         var proxied = $wnd.confirm;

         $wnd.confirm = function(message) {
            // test if this is a ckeditor
            if (typeof $wnd.CKEDITOR !== "undefined" ) {
               return true;
            } else {
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
   
   public void deleteCurrentLine() {}

   public void goToLine(int lineNumber) {}
   
   public void goToPosition(int row, int column) {}

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

   public void replaceFoundedText(String find, String replace, boolean caseSensitive) {}
   
   private HandlerManager getEventBus() 
   {
      return eventBus;
   }

   private CKEditorConfiguration getConfiguration() 
   {
      return configuration;
   }
      
   /**
    * Set listeners of hot keys clicking 
    */
   private native void setHotKeysClickListener(JavaScriptObject editor) /*-{
      var instance = this;
      if (editor) {
         editor.exoHotKeysClickListener = function(e) {   
            // filter key pressed without ctrl 
            if (e.data.keyCode < $wnd.CKEDITOR.CTRL) return;
            
            // see doc at http://docs.cksource.com/ckeditor_api/symbols/CKEDITOR.html#event:key
            var keyPressed = "";
            if (e.data.keyCode < $wnd.CKEDITOR.ALT) {
               // after pressing Ctrl+something
               keyPressed += "Ctrl+" + String(e.data.keyCode - $wnd.CKEDITOR.CTRL);
            } else {
               // after pressing Alt+something
               keyPressed += "Alt+" + String(e.data.keyCode - $wnd.CKEDITOR.ALT);
            }              
            
            // find similar key ammong the hotKeyList 
            var hotKeyList = instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::getHotKeyList()();                  

            // listen Ctrl+S key pressing if hotKeyList is null
            if (hotKeyList === null) { 
               if (keyPressed == "Ctrl+" + "S".charCodeAt(0)) {
                  instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onSaveContent()();
                  return false;                        
               } else {
                  return;
               }                      
            }

            for (var i = 0; i < hotKeyList.@java.util.List::size()(); i++) {
              var currentHotKey = hotKeyList.@java.util.List::get(I)(i); 
              if (currentHotKey == keyPressed) {
                // fire EditorHotKeyCalledEvent
                var editorHotKeyCalledEventInstance = @org.exoplatform.ide.editor.api.event.EditorHotKeyCalledEvent::new(Ljava/lang/String;)(
                  currentHotKey
                );
                var eventBus = instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::getEventBus()();
                eventBus.@com.google.gwt.event.shared.HandlerManager::fireEvent(Lcom/google/gwt/event/shared/GwtEvent;)(editorHotKeyCalledEventInstance);

                return false;                
              }
            }
         }
         editor.on('key', editor.exoHotKeysClickListener);
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
      return (String) params.get(EditorParameters.MIME_TYPE);
   }
   
   private List<String> getHotKeyList()
   {
      return (List<String>) params.get(EditorParameters.HOT_KEY_LIST);
   }

   @Override
   public void setHotKeyList(List<String> hotKeyList)
   {
      params.put(EditorParameters.HOT_KEY_LIST, hotKeyList);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getLineContent(int)
    */
   @Override
   public String getLineContent(int line)
   {
      return null;
   }

   @Override
   public void getTokenListInBackground()
   {
   }
}