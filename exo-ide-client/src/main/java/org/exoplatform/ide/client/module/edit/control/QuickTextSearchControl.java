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
package org.exoplatform.ide.client.module.edit.control;

import org.exoplatform.gwtframework.ui.client.component.command.TextInputControl;
import org.exoplatform.ide.client.IDEImageBundle;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class QuickTextSearchControl extends TextInputControl
{

   public static final String ID = "Quick search";
   
   public QuickTextSearchControl(HandlerManager eventBus)
   {
      super(ID);
      setPrompt("Quick search");
      setNormalImage(IDEImageBundle.INSTANCE.findText());
      setDisabledImage(IDEImageBundle.INSTANCE.findTextDisabled());
      setEnabled(true);
      setVisible(true);
      setSize(150);
   }

}
