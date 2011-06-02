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
package org.exoplatform.ide.client.documentation;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: ShowDocumentationControl Jan 21, 2011 11:10:08 AM evgen $
 *
 */
@RolesAllowed({"administrators", "developers"})
public class ShowDocumentationControl extends SimpleControl implements IDEControl
{

   public static final String ID = "View/Show \\ Hide Documentation";

   public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.documentationTitle();

   public static final String PROMPT_SHOW = IDE.IDE_LOCALIZATION_CONSTANT.showDocumentationControl();

   public static final String PROMPT_HIDE = IDE.IDE_LOCALIZATION_CONSTANT.hideDocumentationControl();

   public ShowDocumentationControl()
   {
      super(ID);
      setTitle(TITLE);
      setImages(IDEImageBundle.INSTANCE.documentation(), IDEImageBundle.INSTANCE.documentation_Disabled());
      setEvent(new ShowDocumentationEvent(true));
      setEnabled(true);
      setDelimiterBefore(true);
      setCanBeSelected(true);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   public void initialize(HandlerManager eventBus)
   {
   }

}
