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

import com.google.web.bindery.event.shared.EventBus;

import com.google.inject.Inject;

import com.google.inject.Singleton;

import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.core.event.ActivePartChangedEvent;
import org.exoplatform.ide.core.event.ActivePartChangedHandler;
import org.exoplatform.ide.core.expressions.Expression;
import org.exoplatform.ide.editor.EditorPartPresenter;
import org.exoplatform.ide.menu.ExtendedCommand;

/**
 * Command for "Save" action
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
@Singleton
public class SaveCommand implements ExtendedCommand, ActivePartChangedHandler
{

   private EditorPartPresenter editor;
   private final EditorDirtyExpression expression;

   /**
    * 
    */
   @Inject
   public SaveCommand(EventBus eventBus, EditorDirtyExpression expression)
   {
      this.expression = expression;
      eventBus.addHandler(ActivePartChangedEvent.TYPE, this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void execute()
   {
      if (editor != null)
      {
         editor.doSave();
      }
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

   /**
    * {@inheritDoc}
    */
   @Override
   public void onActivePartChanged(ActivePartChangedEvent event)
   {
      if (event.getActivePart() instanceof EditorPartPresenter)
      {
         editor = (EditorPartPresenter)event.getActivePart();
      }
   }

}
