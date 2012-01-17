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

import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypesList;
import org.exoplatform.ide.editor.api.codeassitant.NumericProperty;
import org.exoplatform.ide.editor.api.codeassitant.ObjectProperty;
import org.exoplatform.ide.editor.api.codeassitant.StringProperty;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codeassistant.util.ModifierHelper;
import org.exoplatform.ide.editor.java.client.codeassistant.services.marshal.JavaClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class JavaCodeAssistantUtils
{
   
   /**
    * Convert TypesList object to List<Token> 
    * 
    * 
    * @param types
    * @return
    */
    // TODO this methods need temporary and maybe 
   //removed in future after rewriting codeAssitant API
   public static List<Token> types2tokens(TypesList types) 
   {
      if (types != null)
      {
         List<Token> tokens = new ArrayList<Token>(types.getTypes().size());
         for (ShortTypeInfo typeInfo : types.getTypes())
         {
            String fqn = typeInfo.getName();
            String name = fqn.substring(fqn.lastIndexOf(".") + 1);
            String type = typeInfo.getType();
            int modifiers = typeInfo.getModifiers();
            Token token = new TokenImpl(name, TokenType.valueOf(type));
            token.setProperty(TokenProperties.FQN, new StringProperty(fqn));
            token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifiers));
            tokens.add(token);
         }
         return tokens;
      }
      return Collections.emptyList();
   }
   
   
   /**
    * Convert Type object to JavaClass 
    * 
    * 
    * @param types
    * @return
    */
    // TODO this methods need temporary and maybe 
   //removed in future after rewriting codeAssitant API
   public static JavaClass type2javaClass(TypeInfo type) 
   {
      JavaClass classInfo = new JavaClass();
      classInfo.getPublicFields().addAll(getPublicFields(type.getFields()));
      classInfo.getPublicMethods().addAll(getPublicMethods(type.getMethods()));
      classInfo.getAbstractMethods().addAll(getAbstractMethods(type.getMethods()));
      classInfo.getPublicConstructors().addAll(getPublicConstructors(type.getMethods()));
      return classInfo;
   }
   
   

   /**
    * @param list
    * @return
    */
   private static Collection<? extends Token> getAbstractMethods(List<MethodInfo> list)
   {
      //TODO filter same methods
      Map<String, Token> methods = new HashMap<String, Token>();
      for (MethodInfo mi : list)
      {
         int modifier = mi.getModifiers();
         if (ModifierHelper.isAbstract(modifier))
         {
            Token token = new TokenImpl(mi.getName(), TokenType.METHOD);
            token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifier));
            token.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(mi.getDeclaringClass()));
            token.setProperty(TokenProperties.GENERIC_RETURN_TYPE, new StringProperty(mi.getReturnType()));
            token.setProperty(TokenProperties.PARAMETER_TYPES, new StringProperty(array2string(mi.getParameterTypes())));
            token.setProperty(TokenProperties.GENERIC_EXCEPTIONTYPES, new StringProperty(array2string(mi.getExceptionTypes())));
            methods.put(mi.getName() + mi.getParameterTypes().toArray().toString(), token);
         }
      }
      return methods.values();
   }

   /**
    * Get all public methods
    * @param list
    * @return {@link List} of {@link TokenExt} that contains all public method of class
    */
   private static List<? extends Token> getPublicMethods(List<MethodInfo> list)
   {
      List<Token> methods = new ArrayList<Token>();
      for (MethodInfo mi : list)
      {
         int modifier = (int)mi.getModifiers();
         if (ModifierHelper.isPublic(modifier))
         {
            Token token = new TokenImpl(mi.getName(), TokenType.METHOD);
            token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifier));
            token.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(mi.getDeclaringClass()));
            token.setProperty(TokenProperties.RETURN_TYPE, new StringProperty(mi.getReturnType()));
            token.setProperty(TokenProperties.PARAMETER_TYPES, new StringProperty(array2string(mi.getParameterTypes())));
            token.setProperty(TokenProperties.GENERIC_EXCEPTIONTYPES, new StringProperty(array2string(mi.getExceptionTypes())));
            methods.add(token);
         }
      }
      return methods;
   }

   /**
    * Get all public fields
    * @param list
    * @return {@link List} of {@link TokenExt} that represent public fields of Class
    */
   private static List<? extends Token> getPublicFields(List<FieldInfo> list)
   {
      List<Token> fields = new ArrayList<Token>();
      for (FieldInfo fi : list)
      {
         int modifier = fi.getModifiers();
         if (ModifierHelper.isPublic(modifier))
         {
            Token token = new TokenImpl(fi.getName(), TokenType.FIELD);
            token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifier));
            token.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(fi.getDeclaringClass()));
            token.setProperty(TokenProperties.ELEMENT_TYPE, new StringProperty(fi.getType()));
            fields.add(token);
         }
      }
      return fields;
   }

   /**
    * Get all public constructors 
    * @param list
    * @return {@link List} of {@link TokenExt} that represent Class constructors
    */
   private static List<? extends Token> getPublicConstructors(List<MethodInfo> list)
   {
      List<Token> constructors = new ArrayList<Token>();
      for (MethodInfo mi : list)
      {
         int modifier = mi.getModifiers();
         if (!ModifierHelper.isInterface(modifier))
         {
            String name = mi.getName();
            name = name.substring(name.lastIndexOf('.') + 1);
            Token token = new TokenImpl(name, TokenType.CONSTRUCTOR);
            token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifier));
            token.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(mi.getDeclaringClass()));
            token.setProperty(TokenProperties.PARAMETER_TYPES,  new StringProperty(array2string(mi.getParameterTypes())));
            token.setProperty(TokenProperties.GENERIC_EXCEPTIONTYPES, new StringProperty(array2string(mi.getExceptionTypes())));
            constructors.add(token);
         }
      }
      return constructors;
   }
   
   
   
   private static String array2string(List<String> a)                                                                                                                                                              
   {                                                                                                                                                                                                           
      if (a == null)                                                                                                                                                                                           
         return "null";                                                                                                                                                                                        
      int iMax = a.size() - 1;                                                                                                                                                                                 
      if (iMax == -1)                                                                                                                                                                                          
         return "()";                                                                                                                                                                                          
                                                                                                                                                                                                               
      StringBuilder b = new StringBuilder();                                                                                                                                                                   
      b.append('(');                                                                                                                                                                                           
      for (int i = 0;; i++)                                                                                                                                                                                    
      {                                                                                                                                                                                                        
         b.append(String.valueOf(a.get(i)));                                                                                                                                                                       
         if (i == iMax)                                                                                                                                                                                        
            return b.append(')').toString();                                                                                                                                                                   
         b.append(", ");                                                                                                                                                                                       
      }                                                                                                                                                                                                        
   }               

}
