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
package org.exoplatform.ide.extension.java.server.parser;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.model.TypeVariable;
import com.thoughtworks.qdox.model.WildcardType;

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.bean.FieldInfoBean;
import org.exoplatform.ide.codeassistant.jvm.bean.MethodInfoBean;
import org.exoplatform.ide.codeassistant.jvm.bean.ShortTypeInfoBean;
import org.exoplatform.ide.codeassistant.jvm.bean.TypeInfoBean;
import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.JavaType;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Nov 29, 2011 9:54:16 AM evgen $
 * 
 */
public class JavaTypeToTypeInfoConverter
{

   public enum Modifier {
      STATIC(0x00000008), FINAL(0x00000010), PRIVATE(0x00000002), PUBLIC(0x00000001), PROTECTED(0x00000004), ABSTRACT(
         0x00000400), STRICTFP(0x00000800), SYNCHRONIZED(0x00000020), THREADSAFE(0), TRANSIENT(0x00000080), VOLATILE(
         0x00000040), NATIVE(0x00000100);
      private final int mod;

      Modifier(int i)
      {
         this.mod = i;
      }

      public int value()
      {
         return mod;
      }
   }

   private static final Log LOG = ExoLogger.getLogger(JavaTypeToTypeInfoConverter.class);

   private CodeAssistantStorage storage;

   /**
    * @param storage
    */
   public JavaTypeToTypeInfoConverter(CodeAssistantStorage storage)
   {
      super();
      this.storage = storage;
   }

   public TypeInfo convert(JavaClass clazz)
   {
      TypeInfo type = new TypeInfoBean();
      type.setName(clazz.getFullyQualifiedName());
      type.setType(getType(clazz).name());
      if (clazz.getSuperJavaClass() != null)
         type.setSuperClass(clazz.getSuperJavaClass().getFullyQualifiedName());
      else
         type.setSuperClass("java.lang.Object");

      type.setModifiers(modifiersToInteger(clazz.getModifiers()));

      type.setInterfaces(toListFqn(clazz.getImplements()));
      type.setFields(toFieldInfo(clazz));
      JavaMethod[] methods = clazz.getMethods();
      type.setMethods(toMethods(clazz, methods));
      type.setSignature(createTypeSignature(clazz));
      return type;
   }

   /**
    * @param clazz
    * @return
    */
   private String createTypeSignature(JavaClass clazz)
   {
      StringBuilder signature = new StringBuilder();
      boolean isClassGeneric = false;
      if (clazz.getTypeParameters().length != 0)
      {
         isClassGeneric = true;
         signature.append(createTypeParameters(clazz.getTypeParameters()));
      }
      if (isClassGeneric)
      {
         appendSuperClassAndInterfaces(clazz, signature);
      }
      else
      {
         boolean isInterfacesGeneric = false;
         for (Type t : clazz.getImplements())
         {
            if (t.getActualTypeArguments() != null)
            {
               isInterfacesGeneric = true;
               break;
            }
         }
         if (isInterfacesGeneric || clazz.getSuperClass().getActualTypeArguments() != null)
            appendSuperClassAndInterfaces(clazz, signature);
      }
      return signature.length() == 0 ? null : signature.toString();
   }

   /**
    * @param typeVariables
    * @param signature
    */
   private String createTypeParameters(TypeVariable[] typeVariables)
   {
      StringBuilder signature = new StringBuilder("<");

      for (TypeVariable var : typeVariables)
      {
         signature.append(var.getName());

         Type[] bounds = getParameterBounds(var);
         if (bounds != null)
         {
            Type type = bounds[0];
            if (type.isResolved())
               if (type.getJavaClass().isInterface())
                  signature.append(':');
               else
               {
                  try
                  {
                     TypeInfo typeInfo = storage.getTypeByFqn(type.getJavaClass().getFullyQualifiedName());
                     if (typeInfo != null && JavaType.valueOf(typeInfo.getType()) == JavaType.INTERFACE)
                        signature.append(':');
                  }
                  catch (CodeAssistantException e)
                  {
                     LOG.error(e.getMessage(), e);
                  }
               }
            signature.append(':').append(createSignatureForType(type));
            for (int i = 1; i < bounds.length; i++)
            {
               signature.append(':').append(createSignatureForType(bounds[i]));
            }
         }
         else
            signature.append(':').append("Ljava/lang/Object;");
      }
      return signature.append('>').toString();
   }

   /**
    * @param var
    * @return
    */
   private static Type[] getParameterBounds(TypeVariable var)
   {
      try
      {
         Field boundsField = TypeVariable.class.getDeclaredField("bounds");
         boundsField.setAccessible(true);
         Type[] bounds = (Type[])boundsField.get(var);
         return bounds;
      }
      catch (Exception e)
      {
         LOG.error("Can't read bounds for TypeVariable -" + var.getFullyQualifiedName(), e);
         return null;
      }

   }

   /**
    * @param clazz
    * @param signature
    */
   private static void appendSuperClassAndInterfaces(JavaClass clazz, StringBuilder signature)
   {
      signature.append(createSignatureForType(clazz.getSuperClass()));
      for (Type t : clazz.getImplements())
      {
         signature.append(createSignatureForType(t));
      }
   }

   /**
    * @param t
    * @return
    */
   private static String createSignatureForType(Type type)
   {
      StringBuilder signature = new StringBuilder();
      if (type instanceof WildcardType)
         signature.append(getWildcards((WildcardType)type));
      signature.append(SignatureCreator.createByteCodeTypeSignature(type.getFullyQualifiedName()));
      if (type.getActualTypeArguments() != null)
      {
         // remove trailing ';'
         signature.setLength(signature.length() - 1);
         signature.append('<');
         for (Type t : type.getActualTypeArguments())
         {
            if (t.getActualTypeArguments() != null)

               signature.append(createSignatureForType(t));
            else
            {
               if (t.getFullyQualifiedName().contains("."))
               {
                  if (t instanceof WildcardType)
                  {
                     signature.append(getWildcards((WildcardType)t));
                  }
                  if (t.isArray())
                     signature.append('[');
                  signature.append(SignatureCreator.createByteCodeTypeSignature(t.getFullyQualifiedName()));
               }
               else
               {
                  if (t.getFullyQualifiedName().equals("?"))
                  {
                     signature.append('*');
                  }
                  else
                  {
                     if (t instanceof WildcardType)
                     {
                        signature.append(getWildcards((WildcardType)t));
                     }
                     if (t.isArray())
                        signature.append('[');
                     signature.append('T').append(t.getFullyQualifiedName()).append(';');
                  }
               }
            }
         }
         signature.append(">;");
      }
      return signature.toString();
   }

   /**
    * @param t
    * @return
    */
   private static char getWildcards(WildcardType wildcardType)
   {
      try
      {
         Field field = WildcardType.class.getDeclaredField("wildcardExpressionType");
         field.setAccessible(true);
         String value = (String)field.get(wildcardType);
         if ("extends".equals(value))
            return '+';
         else if ("super".equals(value))
            return '-';
         return 0;
      }
      catch (Exception e)
      {
         LOG.error("Can't read wildcardExpressionType in type " + wildcardType.getFullyQualifiedName(), e);
         return 0;
      }

   }

   public static JavaType getType(JavaClass clazz)
   {
      if (clazz.isInterface())
      {
         return JavaType.INTERFACE;
      }
      if (clazz.isEnum())
         return JavaType.ENUM;

      return JavaType.CLASS;
   }

   /**
    * @param methods
    * @return
    */
   private List<MethodInfo> toMethods(JavaClass clazz, JavaMethod[] methods)
   {
      List<MethodInfo> con = new ArrayList<MethodInfo>();
      boolean hasConstructor = false;
      for (JavaMethod m : methods)
      {
         MethodInfo info = new MethodInfoBean();
         info.setExceptionTypes(toListFqn(m.getExceptions()));
         info.setModifiers(modifiersToInteger(m.getModifiers()));
         Type[] parameterTypes = m.getParameterTypes(true);
         info.setParameterTypes(toParameters(parameterTypes));
         info.setParameterNames(toParametersName(m.getParameters()));
         info.setName(m.getName());
         info.setDeclaringClass(m.getParentClass().getFullyQualifiedName());
         info.setDescriptor(SignatureCreator.createMethodSignature(m));
         info.setSignature(createMethodSignature(m, clazz));
         if (!m.isConstructor())
         {
            String returnType = m.getReturnType().getFullyQualifiedName();
            info.setReturnType(returnType);
            info.setConstructor(false);
         }
         else
         {
            info.setConstructor(true);
            hasConstructor = true;
         }
         con.add(info);
      }
      // if class don't has a constructor - add default
      if (!hasConstructor)
      {
         MethodInfo defaultConstructor = new MethodInfoBean();
         defaultConstructor.setDeclaringClass(clazz.getFullyQualifiedName());
         defaultConstructor.setDescriptor("()V;");
         defaultConstructor.setModifiers(Modifier.PUBLIC.value());
         defaultConstructor.setConstructor(true);
         con.add(defaultConstructor);
      }
      return con;
   }

   /**
    * @param m
    * @param clazz
    * @return
    */
   private String createMethodSignature(JavaMethod m, JavaClass clazz)
   {
      boolean isMethodGeneric = false;
      StringBuilder signature = new StringBuilder();
      if (m.getTypeParameters() != null && m.getTypeParameters().length != 0)
      {
         isMethodGeneric = true;
         signature.append(createTypeParameters(m.getTypeParameters()));
      }
      signature.append('(');
      for (JavaParameter parameter : m.getParameters())
      {
         if (parameter.getType().getActualTypeArguments() != null)
            isMethodGeneric = true;
         signature.append(createSignatureForType(parameter.getType()));
      }
      signature.append(')');
      if (m.isConstructor())
      {
         signature.append('V');
      }
      else
      {
         Type returnType = m.getReturnType();
         if (!returnType.isPrimitive() && !returnType.getFullyQualifiedName().contains("."))
            signature.append('T').append(returnType.getFullyQualifiedName()).append(';');
         else
            signature.append(createSignatureForType(m.getReturnType()));
      }
      return isMethodGeneric ? signature.toString() : null;
   }

   /**
    * @param parameters
    * @return
    */
   private static List<String> toParametersName(JavaParameter[] parameters)
   {
      List<String> paramsNames = new ArrayList<String>(parameters.length);
      for (JavaParameter p : parameters)
      {
         paramsNames.add(p.getName());
      }
      return paramsNames;
   }

   /**
    * @param parameterTypes
    * @return
    */
   public static List<String> toParameters(Type[] parameterTypes)
   {
      List<String> params = new ArrayList<String>();
      for (Type type : parameterTypes)
      {
         params.add(type.getFullyQualifiedName());
      }
      return params;
   }

   /**
    * @param fields
    * @return
    */
   private static List<FieldInfo> toFieldInfo(JavaClass clazz)
   {
      JavaField[] fields = clazz.getFields();
      List<FieldInfo> fi = new ArrayList<FieldInfo>();
      boolean isGeneric = false;
      Set<String> parameters = null;
      if (clazz.getTypeParameters().length != 0)
      {
         isGeneric = true;
         parameters = new HashSet<String>(clazz.getTypeParameters().length);
         for (TypeVariable v : clazz.getTypeParameters())
            parameters.add(v.getName());
      }
      for (int i = 0; i < fields.length; i++)
      {
         FieldInfo info = new FieldInfoBean();
         JavaField f = fields[i];
         info.setDeclaringClass(f.getParentClass().getFullyQualifiedName());
         info.setType(f.getType().getValue());
         info.setName(f.getName());
         info.setModifiers(modifiersToInteger(f.getModifiers()));
         info.setDescriptor(SignatureCreator.createTypeSignature(f).replaceAll("\\.", "/"));

         if (isGeneric && parameters.contains(f.getType().getFullyQualifiedName()))
         {
            StringBuilder signature = new StringBuilder();
            if (f.getType().isArray())
               signature.append('[');
            signature.append('T').append(f.getType().getFullyQualifiedName()).append(';');
            info.setSignature(signature.toString());
         }
         else if (f.getType().getActualTypeArguments() != null)
         {
            info.setSignature(createSignatureForType(f.getType()));
         }
         fi.add(info);
      }

      return fi;
   }

   /**
    * @param modifiers
    * @return
    */
   private static Integer modifiersToInteger(String[] modifiers)
   {
      int i = 0;

      for (String s : modifiers)
      {
         i = i | Modifier.valueOf(s.toUpperCase()).value();
      }

      return i;
   }

   /**
    * 
    * 
    * @param types
    * @return
    */
   private static List<String> toListFqn(Type[] types)
   {
      List<String> arr = new ArrayList<String>();
      for (int i = 0; i < types.length; i++)
      {
         arr.add(types[i].getFullyQualifiedName());
      }
      return arr;
   }

   /**
    * @param clazz
    * @return
    */
   public ShortTypeInfo toShortTypeInfo(JavaClass clazz)
   {
      ShortTypeInfo info = new ShortTypeInfoBean();
      info.setModifiers(modifiersToInteger(clazz.getModifiers()));
      info.setName(clazz.getFullyQualifiedName());
      info.setType(getType(clazz).name());
      info.setSignature(createTypeSignature(clazz));
      return info;
   }

   /**
    * @param tags
    * @return
    */
   public static String tagsToString(DocletTag[] tags)
   {
      if (tags == null)
         return "";
      StringBuilder b = new StringBuilder();
      for (DocletTag t : tags)
      {
         b.append("<p>").append("<b>").append(t.getName()).append("</b>").append("<br/>").append(t.getValue())
            .append("</p>");
      }
      return b.toString();
   }
}
