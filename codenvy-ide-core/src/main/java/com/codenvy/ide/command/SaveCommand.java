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
package com.codenvy.ide.command;

import com.codenvy.ide.Resources;
import com.codenvy.ide.core.editor.EditorAgent;
import com.codenvy.ide.core.expressions.Expression;
import com.codenvy.ide.menu.ExtendedCommand;

import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Command for "Save" action
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Singleton
public class SaveCommand implements ExtendedCommand
{

   private final EditorDirtyExpression expression;

   private EditorAgent editorAgent;

   private final Resources resources;

   /**
    *
    */
   @Inject
   public SaveCommand(EditorAgent editorAgent, EditorDirtyExpression expression, Resources resources)
   {
      this.editorAgent = editorAgent;
      this.expression = expression;
      this.resources = resources;
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
   public ImageResource getIcon()
   {
      // TODO need correct image
      return resources.file();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Expression inContext()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Expression canExecute()
   {
      return expression;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getToolTip()
   {
      return "Save changes for current file";
   }
}
