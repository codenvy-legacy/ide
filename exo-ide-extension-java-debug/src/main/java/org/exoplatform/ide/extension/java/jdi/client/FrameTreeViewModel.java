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
package org.exoplatform.ide.extension.java.jdi.client;

import org.exoplatform.ide.extension.java.jdi.shared.Field;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;

import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class FrameTreeViewModel implements TreeViewModel
{
   private SingleSelectionModel<Variable> selectionModel;

   private ListDataProvider<Variable> dataProvider = new ListDataProvider<Variable>();
   
   public FrameTreeViewModel(SingleSelectionModel<Variable> selectionModel)
   {
      this.selectionModel = selectionModel;
   }

   @Override
   public <T> NodeInfo<?> getNodeInfo(T value)
   {
      if (value == null)
      {
         return new DefaultNodeInfo<Variable>(dataProvider, new VariableCell(), selectionModel, null);
      }

      if (value instanceof Field)
      {
         return new DefaultNodeInfo<Variable>(new ValueDataProvider((Field)value), new VariableCell(), selectionModel, null);
      }
      else
      {
         return new DefaultNodeInfo<Variable>(new ValueDataProvider((Variable)value), new VariableCell(), selectionModel, null);
      }

   }

   @Override
   public boolean isLeaf(Object value)
   {
      if (value != null && value instanceof Variable)
         return ((Variable)value).isPrimitive();
      return false;
   }

   public ListDataProvider<Variable> getDataProvider()
   {
      return dataProvider;
   }

}
