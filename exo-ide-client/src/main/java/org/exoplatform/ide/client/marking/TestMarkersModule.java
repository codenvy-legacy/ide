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

package org.exoplatform.ide.client.marking;

import com.google.gwt.event.shared.HandlerRegistration;

import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.problem.Markable;
import org.exoplatform.ide.editor.problem.Problem;
import org.exoplatform.ide.editor.problem.ProblemClickEvent;
import org.exoplatform.ide.editor.problem.ProblemClickHandler;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TestMarkersModule implements MarkProblemHandler, EditorActiveFileChangedHandler, ProblemClickHandler
{

   private HandlerRegistration problemClickHandler;

   private Editor editor;

   public TestMarkersModule()
   {
      IDE.getInstance().addControl(new MarkErrorControl(), Docking.TOOLBAR);
      IDE.getInstance().addControl(new MarkWarningControl(), Docking.TOOLBAR);
      IDE.getInstance().addControl(new UnMarkProblemControl(), Docking.TOOLBAR);

      IDE.addHandler(MarkProblemEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   @Override
   public void onMarkProblem(MarkProblemEvent event)
   {
      if (editor == null)
      {
         System.out.println("file is not opened");
         return;
      }

      if (!(editor instanceof Markable))
      {
         System.out.println("editor is not implements Markable");
         
         editor.setFocus();
         
         return;
      }

      if (event.isMark())
      {
         if (event.getProblemType() == MarkProblemEvent.ProblemType.ERROR)
         {
            addError();
         }
         else
         {
            addWarning();
         }
      }
      else
      {
         removeAllMarkers();
      }
      
      editor.setFocus();
   }

   private int nextProblemId = 0;

   private void addWarning()
   {
      Markable markable = (Markable)editor;
      int row = editor.getCursorRow();

      ProblemWarning warning =
         new ProblemWarning(nextProblemId, "Variable " + nextProblemId + " is not used.", row, 5, 10);
      markable.markProblem(warning);

      nextProblemId++;
   }

   private void addError()
   {
      Markable markable = (Markable)editor;
      int row = editor.getCursorRow();

      ProblemError error =
         new ProblemError(nextProblemId, "Variable " + nextProblemId + " is not defined.", row, 15, 20);
      markable.markProblem(error);

      nextProblemId++;
   }

   private void removeAllMarkers()
   {
      System.out.println("unmarking...");
      Markable markable = (Markable)editor;
      markable.unmarkAllProblems();
      System.out.println(">>> complete!");
   }

   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      editor = event.getEditor();

      if (problemClickHandler != null)
      {
         problemClickHandler.removeHandler();
         problemClickHandler = null;
      }

      if (editor != null && editor instanceof Markable)
      {
         Markable markable = (Markable)editor;
         markable.addProblemClickHandler(this);
      }
   }

   @Override
   public void onProblemClick(ProblemClickEvent event)
   {
      System.out.println("PROBLEM CLICKED!!!!");

      for (Problem prb : event.getProblems())
      {
         System.out.println(" problem > " + prb.getID());
      }

   }

}
