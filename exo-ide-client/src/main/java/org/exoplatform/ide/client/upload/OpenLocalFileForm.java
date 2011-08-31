/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.upload;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

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
   
   private static final String TITLE = IDE.UPLOAD_CONSTANT.openLocalFileTitle();
   
   private static final String OPEN_BUTTON = IDE.UPLOAD_CONSTANT.openButton();
   
   private static final String FILE_TO_OPEN = IDE.UPLOAD_CONSTANT.fileToOpen();
   
   public OpenLocalFileForm(HandlerManager eventBus, List<Item> selectedItems, FolderModel folder, IDEConfiguration applicationConfiguration)
   {
      super(eventBus, selectedItems, folder, applicationConfiguration);
   }
   
   @Override
   protected void initTitles()
   {
      title = TITLE;
      buttonTitle = OPEN_BUTTON;
      labelTitle = FILE_TO_OPEN;
   }
   
   @Override
   protected UploadPresenter createPresenter(HandlerManager eventBus, List<Item> selectedItems, FolderModel folder)
   {
      return new OpenLocalFilePresenter(eventBus, selectedItems, folder);
   }
      
   @Override
   protected String buildUploadPath()
   {
      return applicationConfiguration.getLoopbackServiceContext() + "/";
   }

}
