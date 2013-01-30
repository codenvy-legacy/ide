/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.impl.fs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

/**
* @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
* @version $Id: $
*/
public class AclSerializer implements DataSerializer<Map<String, Set<BasicPermissions>>>
{
   /*
    * Serialize set of enums as set of plain strings.
    * At the moment use EnumSet for better performance when check user permissions.
    * If we change this in future all already serialized ACL may be accessible as well.
    */
   @Override
   public void write(DataOutput output, Map<String, Set<BasicPermissions>> accessList) throws IOException
   {
      output.writeInt(accessList.size());
      for (Map.Entry<String, Set<BasicPermissions>> entry : accessList.entrySet())
      {
         String principal = entry.getKey();
         Set<BasicPermissions> permissions = entry.getValue();
         if (permissions != null && permissions.size() > 0)
         {
            output.writeUTF(principal);
            output.writeInt(permissions.size());
            for (BasicPermissions permission : permissions)
            {
               output.writeUTF(permission.value());
            }
         }
      }
   }

   @Override
   public Map<String, Set<BasicPermissions>> read(DataInput input) throws IOException
   {
      int recordsNum = input.readInt();
      HashMap<String, Set<BasicPermissions>> accessList = new HashMap<String, Set<BasicPermissions>>(recordsNum);
      int readRecords = 0;
      while (readRecords < recordsNum)
      {
         String principal = input.readUTF();
         int permissionsNum = input.readInt();
         if (permissionsNum > 0)
         {
            Set<BasicPermissions> permissions = EnumSet.noneOf(BasicPermissions.class);
            int readPermissions = 0;
            while (readPermissions < permissionsNum)
            {
               permissions.add(BasicPermissions.fromValue(input.readUTF()));
               ++readPermissions;
            }
            accessList.put(principal, permissions);
         }
         ++readRecords;
      }
      return accessList;
   }
}
