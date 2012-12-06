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

import com.google.inject.Inject;

import com.google.inject.Singleton;

import org.exoplatform.ide.core.expressions.AbstractExpression;
import org.exoplatform.ide.core.expressions.ActivePartConstraintExpression;
import org.exoplatform.ide.core.expressions.EditorDirtyConstraintExpression;
import org.exoplatform.ide.core.expressions.ExpressionManager;
import org.exoplatform.ide.editor.EditorPartPresenter;
import org.exoplatform.ide.part.PartPresenter;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
@Singleton
public class EditorDirtyExpression extends AbstractExpression
   implements EditorDirtyConstraintExpression, ActivePartConstraintExpression
{

   private EditorPartPresenter activeEditor;

   /**
    * @param expressionManager
    */
   @Inject
   public EditorDirtyExpression(ExpressionManager expressionManager)
   {
      super(expressionManager, false);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean onEditorDirtyChanged(EditorPartPresenter editor)
   {
      value = editor == activeEditor && editor.isDirty();
      return value;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean onActivePartChanged(PartPresenter part)
   {
      if (part instanceof EditorPartPresenter)
      {
         activeEditor = (EditorPartPresenter)part;
      }
      value = calculateValue();
      return value;
   }

   private boolean calculateValue()
   {
      return activeEditor!=null && activeEditor.isDirty();
   }
}
