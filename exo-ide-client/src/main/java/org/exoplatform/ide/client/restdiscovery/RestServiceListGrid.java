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
package org.exoplatform.ide.client.restdiscovery;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.ListGrid;
import org.exoplatform.ide.client.model.discovery.marshal.RestService;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 22, 2010 9:48:58 AM evgen $
 *
 */
public class RestServiceListGrid extends ListGrid<RestService>
{

   private static String FQN = "FQN";

   private static String PATH = "Path";
   
   private static final String ID = "ideRestServiceListGrid";

   public RestServiceListGrid()
   {
      setCanSort(true);
      setCanEdit(false);
      setEditEvent(ListGridEditEvent.CLICK);
      setHeaderHeight(22);
      setID(ID);

      ListGridField fieldPath = new ListGridField(PATH, PATH);
      fieldPath.setAlign(Alignment.LEFT);
      fieldPath.setCanEdit(true);
      fieldPath.setWidth("40%");

      ListGridField fieldFQN = new ListGridField(FQN, FQN);
      fieldFQN.setAlign(Alignment.LEFT);
      fieldFQN.setCanEdit(true);
      fieldFQN.setWidth("100%");

      setData(new ListGridRecord[0]);

      setFields(fieldPath, fieldFQN);
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.smartgwt.component.ListGrid#setRecordFields(com.smartgwt.client.widgets.grid.ListGridRecord, java.lang.Object)
    */
   @Override
   protected void setRecordFields(ListGridRecord record, RestService item)
   {
      record.setAttribute(PATH, item.getPath());
      record.setAttribute(FQN, item.getFqn());
   }

}
