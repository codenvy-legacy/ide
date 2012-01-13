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
package org.exoplatform.ide.editor.java.client.codeassistant.services.marshal;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.editor.api.codeassitant.NumericProperty;
import org.exoplatform.ide.editor.api.codeassitant.ObjectProperty;
import org.exoplatform.ide.editor.api.codeassitant.StringProperty;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codeassistant.util.ModifierHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Response;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * @see Unmarshallable Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 5:00:56 PM evgen $
 * 
 */
public class ClassDescriptionUnmarshaller implements Unmarshallable
{

   private JavaClass classInfo;

   interface MyFactory extends AutoBeanFactory
   {
      AutoBean<TypeInfo> typeInfo();
   }

   /**
    * @param classInfo
    */
   public ClassDescriptionUnmarshaller(JavaClass classInfo)
   {
      this.classInfo = classInfo;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         if (response.getStatusCode() != HTTPStatus.NO_CONTENT)
         {
            MyFactory myFactory = GWT.create(MyFactory.class);
            AutoBean<TypeInfo> bean = AutoBeanCodex.decode(myFactory, TypeInfo.class, response.getText());
            TypeInfo info = bean.as();
            toJavaClass(info);
         }
      }
      catch (Exception e)
      {
         throw new UnmarshallerException("Can't parse class description");
      }
   }

   private void toJavaClass(TypeInfo info)
   {
      classInfo.getPublicFields().addAll(getPublicFields(info.getFields()));
      classInfo.getPublicMethods().addAll(getPublicMethods(info.getMethods()));
      classInfo.getAbstractMethods().addAll(getAbstractMethods(info.getMethods()));
      classInfo.getPublicConstructors().addAll(getPublicConstructors(info.getMethods()));
   }

   /**
    * @param list
    * @return
    */
   private Collection<? extends Token> getAbstractMethods(List<MethodInfo> list)
   {
      // TODO filter same methods
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
            token.setProperty(TokenProperties.PARAMETER_TYPES, new ObjectProperty(mi.getParameterTypes() != null ? mi
               .getParameterTypes().toArray() : null));
            token.setProperty(TokenProperties.GENERIC_EXCEPTIONTYPES, new ObjectProperty(mi.getExceptionTypes() != null
               ? mi.getExceptionTypes().toArray() : null));
            methods.put(mi.getName() + mi.getParameterTypes().toArray().toString(), token);
         }
      }
      return methods.values();
   }

   /**
    * Get all public methods
    * 
    * @param list
    * @return {@link List} of {@link TokenExt} that contains all public method of class
    */
   private List<? extends Token> getPublicMethods(List<MethodInfo> list)
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
            token.setProperty(TokenProperties.PARAMETER_TYPES, new ObjectProperty(mi.getParameterTypes() != null ? mi
               .getParameterTypes().toArray() : null));
            token.setProperty(TokenProperties.GENERIC_EXCEPTIONTYPES, new ObjectProperty(mi.getExceptionTypes() != null
               ? mi.getExceptionTypes().toArray() : null));
            methods.add(token);
         }
      }
      return methods;
   }

   /**
    * Get all public fields
    * 
    * @param list
    * @return {@link List} of {@link TokenExt} that represent public fields of Class
    */
   private List<? extends Token> getPublicFields(List<FieldInfo> list)
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
    * 
    * @param list
    * @return {@link List} of {@link TokenExt} that represent Class constructors
    */
   private List<? extends Token> getPublicConstructors(List<MethodInfo> list)
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
            token.setProperty(TokenProperties.PARAMETER_TYPES, new ObjectProperty(mi.getParameterTypes() != null ? mi
               .getParameterTypes().toArray() : null));
            token.setProperty(TokenProperties.GENERIC_EXCEPTIONTYPES, new ObjectProperty(mi.getExceptionTypes() != null
               ? mi.getExceptionTypes().toArray() : null));
            constructors.add(token);
         }
      }
      return constructors;
   }

}
