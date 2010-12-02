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
package org.exoplatform.ide.client.module.groovy.service.codeassistant.marshal;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.codeassistant.ModifierHelper;
import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtProperties;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtType;
import org.exoplatform.ide.client.module.groovy.codeassistant.autocompletion.GroovyClass;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

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

   private GroovyClass classInfo;

   private static String METHODS = "methods";

   private static String DECLARED_METHODS = "declaredMethods";

   private static String CONSTRUCTORS = "constructors";

   private static String DECLARED_CONSTRUCTORS = "declaredConstructors";

   private static String FIELDS = "fields";

   private static String DECLARED_FIELDS = "declaredFields";

   /**
    * @param classInfo
    */
   public ClassDescriptionUnmarshaller(GroovyClass classInfo)
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
         parseClassDescription(response.getText());
      }
      catch (Exception e)
      {
         throw new UnmarshallerException("Can't parse class description");
      }
   }

   private native JavaScriptObject getClasses(String json)/*-{
      return eval('(' + json + ')');
   }-*/;

   private void parseClassDescription(String json)
   {

      JSONObject jObject = new JSONObject(getClasses(json));
      if (jObject.containsKey(CONSTRUCTORS))
      {
         classInfo.getPublicConstructors().addAll(getPublicConstructors(jObject.get(CONSTRUCTORS)));
      }
      //for now we use only public methods and fields

      //      if (jObject.containsKey(DECLARED_CONSTRUCTORS))
      //      {
      //         classInfo.getPublicConstructors().addAll(getPublicConstructors(jObject.get(DECLARED_CONSTRUCTORS)));
      //      }
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
   private List<? extends TokenExt> getPublicMethods(JSONValue jsonValue)
   {
      List<TokenExt> methods = new ArrayList<TokenExt>();
      if (jsonValue.isArray() != null)
      {
         JSONArray methodsArray = jsonValue.isArray();
         for (int i = 0; i < methodsArray.size(); i++)
         {
            JSONObject me = methodsArray.get(i).isObject();
            int modifier = (int)me.get(MODIFIERS).isNumber().doubleValue();

            TokenExt token = new TokenExt(me.get(NAME).isString().stringValue(), TokenExtType.METHOD);
            token.setProperty(TokenExtProperties.MODIFIERS, String.valueOf(modifier));
            token.setProperty(TokenExtProperties.DECLARINGCLASS, me.get(DECLARING_CLASS).isString().stringValue());
            token.setProperty(TokenExtProperties.RETURNTYPE, me.get(RETURN_TYPE).isString().stringValue());
            token.setProperty(TokenExtProperties.PARAMETERTYPES, me.get(PARAMETER_TYPES).isString().stringValue());

            methods.add(token);
         }
      }

      return methods;
   }

   /**
    * Get all public fields
    * @param jsonValue
    * @return {@link List} of {@link TokenExt} that represent public fields of Class
    */
   private List<? extends TokenExt> getPublicFields(JSONValue jsonValue)
   {
      List<TokenExt> fields = new ArrayList<TokenExt>();
      if (jsonValue.isArray() != null)
      {
         JSONArray fieldsArray = jsonValue.isArray();
         for (int i = 0; i < fieldsArray.size(); i++)
         {
            JSONObject fi = fieldsArray.get(i).isObject();
            int modifier = (int)fi.get(MODIFIERS).isNumber().doubleValue();
            if (ModifierHelper.isPublic(modifier))
            {
               TokenExt token = new TokenExt(fi.get(NAME).isString().stringValue(), TokenExtType.FIELD);
               token.setProperty(TokenExtProperties.MODIFIERS, String.valueOf(modifier));
               token
                  .setProperty(TokenExtProperties.DECLARINGCLASS, fi.get(DECLARING_CLASS).isString().stringValue());
               token.setProperty(TokenExtProperties.TYPE, fi.get(JAVA_TYPE).isString().stringValue());
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
   private List<? extends TokenExt> getPublicConstructors(JSONValue jsonValue)
   {
      List<TokenExt> constructors = new ArrayList<TokenExt>();

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
               TokenExt token = new TokenExt(name, TokenExtType.CONSTRUCTOR);
               token.setProperty(TokenExtProperties.MODIFIERS, String.valueOf(modifier));
               token.setProperty(TokenExtProperties.DECLARINGCLASS, c.get(DECLARING_CLASS).isString().stringValue());
               token.setProperty(TokenExtProperties.PARAMETERTYPES, c.get(PARAMETER_TYPES).isString()
                  .stringValue());
               constructors.add(token);
            }

         }
      }

      return constructors;
   }

}
