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
package org.exoplatform.ide.codeassistant.jvm.bean;

import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypesList;

import java.util.Collections;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class TypesListBean implements TypesList
{

   private List<ShortTypeInfo> types;

   public TypesListBean()
   {
   }

   public TypesListBean(List<ShortTypeInfo> types)
   {
      this.types = types;
   }

   @Override
   public void setTypes(List<ShortTypeInfo> types)
   {
      this.types = types;
   }

   @Override
   public List<ShortTypeInfo> getTypes()
   {
      if (types != null)
         return types;
      return Collections.emptyList();
   }

}
