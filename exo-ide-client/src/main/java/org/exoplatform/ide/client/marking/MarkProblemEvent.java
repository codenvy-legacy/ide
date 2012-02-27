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

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MarkProblemEvent extends GwtEvent<MarkProblemHandler>
{

   public enum ProblemType {
      WARNING, ERROR
   }

   public static final GwtEvent.Type<MarkProblemHandler> TYPE = new GwtEvent.Type<MarkProblemHandler>();

   private ProblemType problemType = ProblemType.ERROR;

   private boolean mark = true;

   public MarkProblemEvent(ProblemType problemType)
   {
      this.problemType = problemType;
   }

   public MarkProblemEvent(boolean mark)
   {
      this.mark = mark;
   }

   public ProblemType getProblemType()
   {
      return problemType;
   }

   public boolean isMark()
   {
      return mark;
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<MarkProblemHandler> getAssociatedType()
   {
      return TYPE;
   }

   @Override
   protected void dispatch(MarkProblemHandler handler)
   {
      handler.onMarkProblem(this);
   }

}
