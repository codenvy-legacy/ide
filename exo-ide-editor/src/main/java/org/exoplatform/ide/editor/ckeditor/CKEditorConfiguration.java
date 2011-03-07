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

import com.google.gwt.core.client.GWT;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $
 */

public class CKEditorConfiguration
{

   public enum Language 
   {
      ENGLISH("en"), FRENCH("fr"), RUSSIAN("ru"), UKRAINIAN("uk"), VIETNAMESE("vi"), DEFAULT("en");

      private String language;

      Language(String language)
      {
         this.language = language;
      }

      @Override
      public String toString()
      {
         return this.language;
      }
   }

   public final static Language LANGUAGE = Language.DEFAULT;

   public enum Toolbar 
   {
      IDEALL("IDEall"), DEFAULT("IDEall");

      private String toolbar;

      Toolbar(String toolbar)
      {
         this.toolbar = toolbar;
      }

      @Override
      public String toString()
      {
         return this.toolbar;
      }
   }

   public final static Toolbar TOOLBAR = Toolbar.IDEALL;

   public enum Theme 
   {
      DEFAULT("default");

      private String theme;

      Theme(String theme)
      {
         this.theme = theme;
      }

      @Override
      public String toString()
      {
         return this.theme;
      }
   }

   public final static Theme THEME = Theme.DEFAULT;

   public enum Skin 
   {
      IDEALL("ideall"), DEFAULT("ideall");

      private String skin;

      Skin(String skin)
      {
         this.skin = skin;
      }

      @Override
      public String toString()
      {
         return this.skin;
      }
   }

   public final static Skin SKIN = Skin.IDEALL; // Skin.V2

   public final static boolean READ_ONLY = false;

   public final static int CONTINUOUS_SCANNING = 100;

   public final static String BASE_PATH = GWT.getModuleBaseURL() + "ckeditor/";

   public enum StartupMode 
   {
      WYSIWYG("wysiwyg"), SOURCE("source");

      private String startupMode;

      StartupMode(String startupMode)
      {
         this.startupMode = startupMode;
      }

      @Override
      public String toString()
      {
         return this.startupMode;
      }
   }

   public final static StartupMode STARTUP_MODE = StartupMode.WYSIWYG;

   private static boolean fullPage = false;

   /** @param fullPage <b>true</b> - ckeditor will add <i>html, head, body</i> - tags of html-file;
    * <b>false</b> - ckeditor will remove <i>html, head, body</i> - tags of html-file **/
   public static void setFullPage(boolean fullPage)
   {
      CKEditorConfiguration.fullPage = fullPage;
   }

   /** @return <b>true</b> - ckeditor will add <i>html, head, body</i> - tags of html-file;
    * <b>false</b> - ckeditor will remove <i>html, head, body</i> - tags of html-file **/
   public static boolean isFullPage()
   {
      return fullPage;
   }

}