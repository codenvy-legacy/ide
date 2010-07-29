/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.module.gadget.controls;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.module.gadget.Images;
import org.exoplatform.ide.client.module.gadget.event.UndeployGadgetEvent;

import com.google.gwt.event.shared.HandlerManager;


/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class UndeployGadgetCommand extends IDEControl implements EditorActiveFileChangedHandler
{

   private static final String ID = "Run/UnDeploy Gadget";

   private static final String TITLE = "UnDeploy Gadget from GateIn";

   public UndeployGadgetCommand(HandlerManager eventBus)
   {
      super(ID, eventBus);
      setTitle(TITLE);
      setPrompt(TITLE);
//      setImages(GadgetPluginImageBundle.INSTANCE.undeployGadget(), GadgetPluginImageBundle.INSTANCE
//         .undeployGadgetDisabled());
      setIcon(Images.UNDEPLOY_GADGET);
      setEvent(new UndeployGadgetEvent());
   }


   protected void onRegisterHandlers()
   {
      addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null)
      {
         setEnabled(false);
         setVisible(false);
         return;
      }

//      setVisible(true);

      if (MimeType.GOOGLE_GADGET.equals(event.getFile().getContentType()))
      {
         setVisible(true);

         if (event.getFile().isNewFile())
         {
            setEnabled(false);
         }
         else
         {
            setEnabled(true);
         }
      }
      else
      {
         setVisible(false);
         setEnabled(false);
      }
   }

}
