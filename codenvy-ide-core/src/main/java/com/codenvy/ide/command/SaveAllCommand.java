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

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;

import com.codenvy.ide.api.expressions.EditorsDirtyExpression;
import com.codenvy.ide.api.expressions.Expression;

import com.codenvy.ide.api.ui.menu.ExtendedCommand;


import com.codenvy.ide.Resources;

import com.codenvy.ide.json.JsonStringMap;

import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Singleton
public class SaveAllCommand implements ExtendedCommand
{

   private EditorAgent editorAgent;

   private EditorsDirtyExpression expression;

   private final Resources resources;

   /**
    * Create command.
    * 
    * @param editorAgent
    * @param expression
    */
   @Inject
   public SaveAllCommand(EditorAgent editorAgent, EditorsDirtyExpression expression, Resources resources)
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
      JsonStringMap<EditorPartPresenter> editors = editorAgent.getOpenedEditors();
      editors.iterate(new JsonStringMap.IterationCallback<EditorPartPresenter>()
      {
         @Override
         public void onIteration(String key, EditorPartPresenter value)
         {
            if (value.isDirty())
            {
               value.doSave();
            }
         }
      });
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
      return "Save all changes for project";
   }
}
