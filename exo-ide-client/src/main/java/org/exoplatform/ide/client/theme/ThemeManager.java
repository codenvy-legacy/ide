/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.theme;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.user.client.DOM;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.codemirror.CodeMirror;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class ThemeManager implements EditorFileOpenedHandler, EditorFileClosedHandler
{
   
   public static final String DEFAULT_THEME_NAME = "Default";
   
   private Map<String, Theme> themes = new HashMap<String, Theme>();
   
   private String activeThemeName = DEFAULT_THEME_NAME;
   
   public List<Theme> getThemes()
   {
      return new ArrayList<Theme>(themes.values());
   }
   
   public String getActiveThemeName()
   {
      return activeThemeName;
   }
   
   private static ThemeManager instance;
   
   public static ThemeManager getInstance()
   {
      return instance;
   }
   
   public ThemeManager() {
      instance = this;
      
      Theme defaultTheme = addTheme(DEFAULT_THEME_NAME, null, null);
      defaultTheme.setActive(true);
      addTheme("Darkula", "theme/intellij-darkula-mainframe.css", "theme/intellij-darkula-codemirror-091.css");
      
      IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
      IDE.addHandler(EditorFileClosedEvent.TYPE, this);
   }
   
   private Theme addTheme(String name, String mainFrameCss, String codemirrorCss)
   {
      Theme theme = new Theme(name, mainFrameCss, codemirrorCss);
      themes.put(theme.getName(), theme);
      return theme;
   }
   
   public void changeTheme(String themeName)
   {
      if (activeThemeName.equals(themeName))
      {
         return;
      }
      
      if (themeName == null || themeName.isEmpty())
      {
         return;
      }
      
      Theme activeTheme = themes.get(activeThemeName);
      activeTheme.setActive(false);
      removeCSSFromMainFrame(activeTheme);
      
      activeThemeName = themeName;
      Theme newTheme = themes.get(themeName);
      newTheme.setActive(true);
      addCssToMainFrame(newTheme);
      
      for (String key : editors.keySet())
      {
         Editor editor = editors.get(key);
         if (!(editor instanceof CodeMirror))
         {
            continue;
         }
         
         injectCssToCodeMirror((CodeMirror)editor, newTheme.getCodemirrorCss());
      }
      
      IDE.fireEvent(new ThemeChangedEvent(themeName));
   }
   
   private com.google.gwt.dom.client.Node getChildByTagName(com.google.gwt.dom.client.Node parent, String tagName)
   {
      for (int i = 0; i < parent.getChildCount(); i++)
      {
         com.google.gwt.dom.client.Node child = parent.getChild(i);
         if (tagName.equalsIgnoreCase(child.getNodeName()))
         {
            return child;
         }
      }
      
      return null;
   }
   
   private void removeCSSFromMainFrame(Theme theme)
   {
      if (theme.getMainFrameCss() == null)
      {
         return;
      }
      
      String cssURL = GWT.getModuleBaseURL();
      if (!cssURL.endsWith("/"))
      {
         cssURL += "/";
      }
      cssURL += theme.getMainFrameCss();
      
      com.google.gwt.dom.client.Node headNode = getChildByTagName(Document.get().getDocumentElement(), "head");      
      for (int i = 0; i < headNode.getChildCount(); i++)
      {
         com.google.gwt.dom.client.Node child = headNode.getChild(i);
         if ("link".equalsIgnoreCase(child.getNodeName()))
         {
            String href = DOM.getElementProperty((com.google.gwt.user.client.Element)child.cast(), "href");
            if (cssURL.equals(href))
            {
               child.removeFromParent();
               return;
            }
         }
      }
   }
   
   private void addCssToMainFrame(Theme theme)
   {
      if (theme.getMainFrameCss() == null)
      {
         return;
      }
      
      String cssURL = GWT.getModuleBaseURL();
      if (!cssURL.endsWith("/"))
      {
         cssURL += "/";
      }
      cssURL += theme.getMainFrameCss();
      
      com.google.gwt.dom.client.Node headNode = getChildByTagName(Document.get().getDocumentElement(), "head");
      
      LinkElement linkElement = Document.get().createLinkElement();
      linkElement.setHref(cssURL);
      linkElement.setRel("stylesheet");
      headNode.appendChild(linkElement);
   }
   
   private Map<String, Editor> editors = new HashMap<String, Editor>();
   

   @Override
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      editors.put(event.getFile().getId(), event.getEditor());
      
      Editor editor = event.getEditor();
      if (!(editor instanceof CodeMirror)) {
         return;
      }
      
      if (DEFAULT_THEME_NAME.equals(activeThemeName))
      {
         return;
      }

      Theme theme = themes.get(activeThemeName);
      injectCssToCodeMirror((CodeMirror)editor, theme.getCodemirrorCss());
   }
   
   private void injectCssToCodeMirror(CodeMirror codeMirror, String css)
   {
      if (css != null)
      {
         String cssURL = GWT.getModuleBaseURL();
         if (!cssURL.endsWith("/"))
         {
            cssURL += "/";
         }
         css = cssURL + css;
      }

      codeMirror.injectStyle(css);      
   }

   @Override
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      editors.remove(event.getFile().getId());
   }

}
