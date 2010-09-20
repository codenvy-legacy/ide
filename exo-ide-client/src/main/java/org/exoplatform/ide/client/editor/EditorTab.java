/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.editor;

import org.exoplatform.gwtframework.ui.client.smartgwteditor.SmartGWTTextEditor;
import org.exoplatform.ide.client.Utils;
import org.exoplatform.ide.client.module.vfs.api.File;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class EditorTab extends Tab
{

   private VLayout tabPane;

   private SmartGWTTextEditor textEditor;

   private File file;

   private boolean readOnly = false;

   public EditorTab(File file)
   {
      this.file = file;
      setTitle(getTabTitle());
      tabPane = new VLayout();
      setPane(tabPane);
   }

   public void showReadOnlyStatus()
   {
      readOnly = true;
      setTitle(getTabTitle());
   }

   public void hideReadOnlyStatus()
   {
      readOnly = false;
      setTitle(getTabTitle());
   }

   public SmartGWTTextEditor getTextEditor()
   {
      return textEditor;
   }

   public void setTextEditor(SmartGWTTextEditor textEditor)
   {
      if (this.textEditor != null)
      {
         tabPane.removeMember(this.textEditor);
      }

      this.textEditor = textEditor;
      tabPane.addMember(textEditor);
   }

   public File getFile()
   {
      return file;
   }

   public void setFile(File file)
   {
      this.file = file;
   }

   public String getTabTitle()
   {
      boolean fileChanged = file.isContentChanged() || file.isPropertiesChanged();
      String fileName = Utils.unescape(fileChanged ? file.getName() + "&nbsp;*" : file.getName());
      String hint = "File opened in read only mode. Use SaveAs command.";
      String readTitle = readOnly ? "&nbsp;<font color=\"#aa2233\" title=\"" + hint + "\">[ Read only ]</font>" : "";
      String title =
         "<span title=\"" + file.getHref() + "\">" + Canvas.imgHTML(file.getIcon()) + "&nbsp;" + fileName + readTitle
            + "</span>";
      return title;
   }

}
