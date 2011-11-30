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

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.editor.api.codeassitant.NumericProperty;
import org.exoplatform.ide.editor.api.codeassitant.StringProperty;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codeassistant.util.ModifierHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @see Unmarshallable
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 5:00:56 PM evgen $
 *
 */
public class ClassDescriptionUnmarshaller implements Unmarshallable
{
   private static final String PARAMETER_TYPES = "parameterTypes";

   private static final String RETURN_TYPE = "returnType";

   private static final String DECLARING_CLASS = "declaringClass";

   private static final String JAVA_TYPE = "type";

   private static final String NAME = "name";

   private static final String MODIFIERS = "modifiers";

   private JavaClass classInfo;

   private static String METHODS = "methods";

   private static String DECLARED_METHODS = "declaredMethods";

   private static String CONSTRUCTORS = "constructors";

   private static String DECLARED_CONSTRUCTORS = "declaredConstructors";

   private static String FIELDS = "fields";

   private static String DECLARED_FIELDS = "declaredFields";

   /**
    * @param classInfo
    */
   public ClassDescriptionUnmarshaller(JavaClass classInfo)
   {
      super();
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
            parseClassDescription(response.getText());
      }
      catch (Exception e)
      {
         throw new UnmarshallerException("Can't parse class description");
      }
   }

   private void parseClassDescription(String json)
   {

      JSONObject jObject = JSONParser.parseLenient(json).isObject();
      if (jObject.containsKey(CONSTRUCTORS))
      {
         classInfo.getPublicConstructors().addAll(getPublicConstructors(jObject.get(CONSTRUCTORS)));
      }

      if (jObject.containsKey(FIELDS))
      {
         classInfo.getPublicFields().addAll(getPublicFields(jObject.get(FIELDS)));
      }

      if (jObject.containsKey(METHODS))
      {
         classInfo.getPublicMethods().addAll(getPublicMethods(jObject.get(METHODS)));
      }

   }

   /**
    * Get all public methods
    * @param jsonValue
    * @return {@link List} of {@link TokenExt} that contains all public method of class
    */
   private List<? extends Token> getPublicMethods(JSONValue jsonValue)
   {
      List<Token> methods = new ArrayList<Token>();
      if (jsonValue.isArray() != null)
      {
         JSONArray methodsArray = jsonValue.isArray();
         for (int i = 0; i < methodsArray.size(); i++)
         {
            JSONObject me = methodsArray.get(i).isObject();
            int modifier = (int)me.get(MODIFIERS).isNumber().doubleValue();
            if (ModifierHelper.isPublic(modifier))
            {
               Token token = new TokenImpl(me.get(NAME).isString().stringValue(), TokenType.METHOD);
               token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifier));
               token.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(me.get(DECLARING_CLASS).isString()
                  .stringValue()));
               token.setProperty(TokenProperties.RETURN_TYPE, new StringProperty(me.get(RETURN_TYPE).isString()
                  .stringValue()));
               token.setProperty(TokenProperties.PARAMETER_TYPES, new StringProperty(me.get(PARAMETER_TYPES).isString()
                  .stringValue()));

               methods.add(token);
            }
         }
      }

      return methods;
   }

   /**
    * Get all public fields
    * @param jsonValue
    * @return {@link List} of {@link TokenExt} that represent public fields of Class
    */
   private List<? extends Token> getPublicFields(JSONValue jsonValue)
   {
      List<Token> fields = new ArrayList<Token>();
      if (jsonValue.isArray() != null)
      {
         JSONArray fieldsArray = jsonValue.isArray();
         for (int i = 0; i < fieldsArray.size(); i++)
         {
            JSONObject fi = fieldsArray.get(i).isObject();
            int modifier = (int)fi.get(MODIFIERS).isNumber().doubleValue();
            if (ModifierHelper.isPublic(modifier))
            {
               Token token = new TokenImpl(fi.get(NAME).isString().stringValue(), TokenType.FIELD);
               token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifier));
               token.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(fi.get(DECLARING_CLASS).isString()
                  .stringValue()));
               token.setProperty(TokenProperties.ELEMENT_TYPE, new StringProperty(fi.get(JAVA_TYPE).isString()
                  .stringValue()));
               fields.add(token);
            }
         }
      }

      return fields;
   }

   /**
    * Get all public constructors 
    * @param jsonValue
    * @return {@link List} of {@link TokenExt} that represent Class constructors
    */
   private List<? extends Token> getPublicConstructors(JSONValue jsonValue)
   {
      List<Token> constructors = new ArrayList<Token>();

      if (jsonValue.isArray() != null)
      {
         JSONArray con = jsonValue.isArray();
         for (int i = 0; i < con.size(); i++)
         {
            JSONObject c = con.get(i).isObject();
            int modifier = (int)c.get(MODIFIERS).isNumber().doubleValue();
            if (ModifierHelper.isPublic(modifier))
            {
               String name = c.get(NAME).isString().stringValue();
               name = name.substring(name.lastIndexOf('.') + 1);
               Token token = new TokenImpl(name, TokenType.CONSTRUCTOR);
               token.setProperty(TokenProperties.MODIFIERS, new NumericProperty(modifier));
               token.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(c.get(DECLARING_CLASS).isString()
                  .stringValue()));
               token.setProperty(TokenProperties.PARAMETER_TYPES, new StringProperty(c.get(PARAMETER_TYPES).isString()
                  .stringValue()));
               constructors.add(token);
            }

         }
      }

      return constructors;
   }

}
