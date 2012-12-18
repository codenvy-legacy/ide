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
package org.exoplatform.ide.command;

import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.exoplatform.ide.core.editor.EditorAgent;
import org.exoplatform.ide.core.expressions.Expression;
import org.exoplatform.ide.menu.ExtendedCommand;

/**
 * Command for "Save" action
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
@Singleton
public class SaveCommand implements ExtendedCommand
{

   private final EditorDirtyExpression expression;

   private EditorAgent editorAgent;

   /**
    *
    */
   @Inject
   public SaveCommand(EditorAgent editorAgent, EditorDirtyExpression expression)
   {
      this.editorAgent = editorAgent;
      this.expression = expression;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void execute()
   {
      editorAgent.getActiveEditor().doSave();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Image getIcon()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Expression visibleWhen()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Expression enabledWhen()
   {
      return expression;
   }

}
