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

package org.exoplatform.ide.client.log;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.exoplatform.gwtframework.commons.util.Log;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorChangeActiveFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorCloseFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorDeleteCurrentLineEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFindTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFormatTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorGoToLineEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorRedoTypingEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceAndFindTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorSetFocusEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorTextFoundEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorUndoTypingEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.event.BeforeViewLoseActivityEvent;
import org.exoplatform.ide.client.framework.ui.api.event.BeforeViewLoseActivityHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewLostActivityEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewLostActivityHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PopupEventLogger
{
   
   public class Msg
   {
      
      private String className;
      
      private Map<String, String> params = new LinkedHashMap<String, String>();
      
      public Msg(GwtEvent<?> event) {
         className = event.getClass().getName();
         className = className.substring(className.lastIndexOf(".") + 1);
      }
      
      public Msg param(String name, String value) {
         params.put(name, value);
         return this;
      }
      
      public void log() {
         String html = "<b>" + className + "</b>";

         if (params.size() > 0) {
            html += "<font style='color:#880000;'>" +
            		"<table style='width:100%; margin-top:5px; margin-bottom:10px;' border='0'>";
            
            Iterator<String> keyIter = params.keySet().iterator();
            while (keyIter.hasNext()) {
               String name = keyIter.next();
               String value = params.get(name);
               html += "<tr>" +
               		//"<td class='msg' style='width:50px;'>&nbsp;</td>" +
               		"<td class='msg' style='width:100px; text-align:right;'>" + name + "&nbsp;<b>:</b>&nbsp;</td>" +
               		"<td class='msg' style=''>" + value + "</td></tr>";
            }
            
            html += "</table>" +
            		"</font>";
         }
         
         Log.info(html);
      }
      
   }
   
   public PopupEventLogger() {
      new PopupWindowLogger();
      
      logViewEvents();
      logEditorEvents();
   }
   
   private void logViewEvents() {

      IDE.addHandler(ViewOpenedEvent.TYPE, new ViewOpenedHandler()
      {
         @Override
         public void onViewOpened(ViewOpenedEvent event)
         {
            new Msg(event)
            .param("View ID", event.getView().getId())
            .log();
         }
      });

      IDE.addHandler(ViewClosedEvent.TYPE, new ViewClosedHandler()
      {
         @Override
         public void onViewClosed(ViewClosedEvent event)
         {
            new Msg(event)
            .param("View ID", event.getView().getId())
            .log();
         }
      });
      
      IDE.addHandler(ClosingViewEvent.TYPE, new ClosingViewHandler()
      {
         @Override
         public void onClosingView(ClosingViewEvent event)
         {
            new Msg(event)
            .param("View ID", event.getView().getId())
            .log();
         }
      });
      
      IDE.addHandler(ViewVisibilityChangedEvent.TYPE, new ViewVisibilityChangedHandler()
      {
         @Override
         public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
         {
            new Msg(event)
            .param("View ID", event.getView().getId())
            .param("isViewVisible", "" + event.getView().isViewVisible())
            .log();
         }
      });
      
      IDE.addHandler(ViewActivatedEvent.TYPE, new ViewActivatedHandler()
      {
         @Override
         public void onViewActivated(ViewActivatedEvent event)
         {
            new Msg(event)
            .param("View ID", event.getView().getId())
            .param("isActive", "" + event.getView().isActive())
            .log();
         }
      });
      
      IDE.addHandler(ViewLostActivityEvent.TYPE, new ViewLostActivityHandler()
      {
         @Override
         public void onViewLostActivity(ViewLostActivityEvent event)
         {
            new Msg(event)
            .param("View ID", event.getView().getId())
            .log();
         }
      });
      
      IDE.addHandler(BeforeViewLoseActivityEvent.TYPE, new BeforeViewLoseActivityHandler()
      {
         @Override
         public void onBeforeViewLoseActivity(BeforeViewLoseActivityEvent event)
         {
            new Msg(event)
            .param("View ID", event.getView().getId())
            .log();
         }
      });
      
   }
   
   
   private void logEditorEvents() {
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, new EditorActiveFileChangedHandler()
      {
         @Override
         public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
         {
            Msg msg = new Msg(event);
            if (event.getFile() == null) {
               msg.param("file", "null");
            } else {
               msg.param("file name", event.getFile().getPath());
               msg.param("editor ID", event.getEditor().getEditorId());               
            }

            msg.log();
         }
      });
      
//      IDE.addHandler(EditorChangeActiveFileEvent.TYPE, null);
//      IDE.addHandler(EditorCloseFileEvent.TYPE, null);
//      IDE.addHandler(EditorDeleteCurrentLineEvent.TYPE, null);
//      IDE.addHandler(EditorFileClosedEvent.TYPE, null);
//      IDE.addHandler(EditorFileContentChangedEvent.TYPE, null);
//      IDE.addHandler(EditorFileOpenedEvent.TYPE, null);
      
      //EditorFindTextEvent
      //EditorFormatTextEvent
      //EditorGoToLineEvent
      //EditorOpenFileEvent
      //EditorRedoTypingEvent
      //EditorReplaceAndFindTextEvent
      //EditorReplaceFileEvent
      //EditorReplaceTextEvent
      //EditorSetFocusEvent
      //EditorTextFoundEvent
      //EditorUndoTypingEvent
      
   }
   
   private void logNavigationEvents() {
   }

}
