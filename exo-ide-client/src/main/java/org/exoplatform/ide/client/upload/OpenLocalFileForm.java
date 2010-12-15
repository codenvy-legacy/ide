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

package org.exoplatform.ide.client.upload;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.vfs.Item;

import java.util.List;

/**
 * Form for opening local file in editor.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 13, 2010 $
 *
 */
public class OpenLocalFileForm extends UploadFileForm implements OpenLocalFilePresenter.Display
{
   public OpenLocalFileForm(HandlerManager eventBus, List<Item> selectedItems, String path, IDEConfiguration applicationConfiguration)
   {
      super(eventBus, selectedItems, path, applicationConfiguration);
   }
   
   @Override
   protected void initTitles()
   {
      title = "Open file";
      buttonTitle = "Open";
      labelTitle = "File to open";
   }
   
   @Override
   protected UploadPresenter createPresenter(HandlerManager eventBus, List<Item> selectedItems, String path)
   {
      return new OpenLocalFilePresenter(eventBus, selectedItems, path);
   }
      
   @Override
   protected String buildUploadPath()
   {
      return applicationConfiguration.getLoopbackServiceContext() + "/";
   }

}
