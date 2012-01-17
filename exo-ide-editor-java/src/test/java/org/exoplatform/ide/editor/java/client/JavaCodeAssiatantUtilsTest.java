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
package org.exoplatform.ide.editor.java.client;

import static org.junit.Assert.*;

import org.exoplatform.ide.codeassistant.jvm.bean.ShortTypeInfoBean;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypesList;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class JavaCodeAssiatantUtilsTest
{
   
   @Test
   public void testTypes2tokens() throws Exception
   {
      TypesList types = new TypesList()
      {
         
         @Override
         public void setTypes(List<ShortTypeInfo> types)
         {
         }
         
         @Override
         public List<ShortTypeInfo> getTypes()
         {
            List<ShortTypeInfo> infos = new ArrayList<ShortTypeInfo>();
            infos.add(new ShortTypeInfoBean(JavaCodeAssistantUtils.class.getName(), 10, "CLASS"));
            infos.add(new ShortTypeInfoBean(this.getClass().getName(), 10, "CLASS"));
            return infos;
         }
      };
      
      List<Token> tokens = JavaCodeAssistantUtils.types2tokens(types);
      assertEquals(2, tokens.size());
      assertEquals(JavaCodeAssistantUtils.class.getSimpleName(), tokens.get(0).getName());
      assertEquals(JavaCodeAssistantUtils.class.getName(), tokens.get(0).getProperty("FQN").isStringProperty().stringValue());
      assertEquals("CLASS", tokens.get(0).getType().name());
   }
   
   @Test
   public void testTypes2tokensIfNull() throws Exception
   {
    
      List<Token> tokens = JavaCodeAssistantUtils.types2tokens(null);
      assertEquals(0, tokens.size());
   }

}
