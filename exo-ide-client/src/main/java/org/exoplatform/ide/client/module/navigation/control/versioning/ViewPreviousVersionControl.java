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
package org.exoplatform.ide.client.module.navigation.control.versioning;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewPreviousVersionEvent;
import org.exoplatform.ide.client.module.vfs.api.Version;
import org.exoplatform.ide.client.module.vfs.property.ItemProperty;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 29, 2010 $
 *
 */
public class ViewPreviousVersionControl extends IDEControl implements EditorActiveFileChangedHandler
{

   private static final String ID = "View/Older Version";

   private final String TITLE = "Older Version";

   private final String PROMPT = "View Older Version";

   /**
    * @param id
    * @param eventBus
    */
   public ViewPreviousVersionControl(HandlerManager eventBus)
   {
      super(ID, eventBus);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setEvent(new ViewPreviousVersionEvent());
      setImages(IDEImageBundle.INSTANCE.viewOlderVersion(), IDEImageBundle.INSTANCE.viewOlderVersionDisabled());
      setDelimiterBefore(true);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#onRegisterHandlers()
    */
   @Override
   protected void onRegisterHandlers()
   {
      addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#onInitializeApplication()
    */
   @Override
   protected void onInitializeApplication()
   {
      setVisible(false);
      setEnabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      boolean isShow = (event.getFile() != null && event.getFile() instanceof Version);
      setVisible(isShow);
      if (isShow)
      {
         Version version = (Version)event.getFile();
         boolean isEnabled =
            (version.getProperty(ItemProperty.PREDECESSOR_SET) != null && version
               .getProperty(ItemProperty.PREDECESSOR_SET).getChildProperties().size() > 0);
         setEnabled(isEnabled);
      }
   }
}
