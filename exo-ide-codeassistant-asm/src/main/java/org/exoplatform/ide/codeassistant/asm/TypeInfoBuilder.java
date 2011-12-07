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
package org.exoplatform.ide.codeassistant.asm;

import org.exoplatform.ide.codeassistant.jvm.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.RoutineInfo;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

/**
 * <p>
 * This class used for building {@link TypeInfo} objects using data from
 * <b>asm</b> library. When {@link TypeInfoClassVisitor} visits methods,
 * constructors and fields, it's add they in builder using methods
 * {@link #addField(int, String, String)},
 * {@link #addMethod(int, String, String[], String),
 * 
 * @link #addConstructor(int, String, String[], String)}.<br>
 *       NOTE: This methods are private for this package and shouldn't be
 *       invoked by user code.
 *       </p>
 *       <p>
 *       Instance of {@link TypeInfoBuilder} creates by
 *       {@link TypeInfoClassVisitor} when it visits class definition. You can
 *       get {@link TypeInfoBuilder} objects by method
 *       {@link ClassParser#getClasses()}. You may be sure that when you get
 *       {@link TypeInfoBuilder} object, all fields, methods and constructors
 *       was added.
 *       </p>
 */
public class TypeInfoBuilder
{

   /**
    * There are constants for <b>modifier</b> flag.<br>
    * There are no (yet) <b>public</b> constants for this flags in {@list
    * Modifier}, because <b>they have different meanings for fields and
    * methods</b>. More details, see in {@link Modifier} source;
    * 
    */
   public static final int MODIFIER_SYNTHETIC = 0x00001000;

   public static final int MODIFIER_ANNOTATION = 0x00002000;

   public static final int MODIFIER_ENUM = 0x00004000;

   private final String qualifiedName;

   private final String name;

   private final String superName;

   private final String[] interfaces;

   private final String type;

   private final int modifiers;

   private final List<FieldInfo> fields;

   private final List<MethodInfo> methods;

   private final List<RoutineInfo> constructors;

   /*
    * manifest field is not final,
    * because this info stores in MANIFEST.MF, but not in byte-code.
    * So, ClassParser couldn't set this value.
    */
   private Manifest manifest;

   public TypeInfoBuilder(int access, String name, String superName, String[] interfaces)
   {
      this.qualifiedName = name.replace("/", ".");
      this.name = name.substring(name.lastIndexOf('/') + 1);
      this.superName = superName.replace("/", ".");

      this.interfaces = interfaces;
      if (interfaces != null)
      {
         for (int i = 0; i < interfaces.length; i++)
         {
            this.interfaces[i] = interfaces[i].replace('/', '.');
         }
      }

      this.modifiers = access;
      if ((modifiers & MODIFIER_ENUM) != 0)
      {
         type = "ENUM";
      }
      else if ((modifiers & MODIFIER_ANNOTATION) != 0)
      {
         type = "ANNOTATION";
      }
      else if ((modifiers & Modifier.INTERFACE) != 0)
      {
         type = "INTERFACE";
      }
      else
      {
         type = "CLASS";
      }

      this.fields = new ArrayList<FieldInfo>();
      this.methods = new ArrayList<MethodInfo>();
      this.constructors = new ArrayList<RoutineInfo>();
   }

   public ShortTypeInfo buildShortTypeInfo()
   {
      return new ShortTypeInfo(modifiers, name, qualifiedName, type);
   }

   public TypeInfo buildTypeInfo()
   {
      TypeInfo typeInfo = new TypeInfo();
      typeInfo.setModifiers(modifiers);
      typeInfo.setName(name);
      typeInfo.setQualifiedName(qualifiedName);
      typeInfo.setFields(fields.toArray(new FieldInfo[0]));
      typeInfo.setMethods(methods.toArray(new MethodInfo[0]));
      typeInfo.setConstructors(constructors.toArray(new RoutineInfo[0]));

      /*
       * There are no way to fill declared fields, methods and constructors, because there are no class hierarchy,
       * each class parses separately.
       */
      typeInfo.setDeclaredFields(null);
      typeInfo.setDeclaredMethods(null);
      typeInfo.setDeclaredConstructors(null);

      typeInfo.setInterfaces(interfaces);
      typeInfo.setSuperClass(superName);
      typeInfo.setType(type);
      return typeInfo;
   }

   public String getName()
   {
      return qualifiedName;
   }

   void addField(int access, String name, String desc)
   {
      fields.add(new FieldInfo(transformTypeFormat(desc), access, name, qualifiedName));
   }

   void addMethod(int access, String methodName, String[] exceptions, String desc)
   {

      MethodInfo methodInfo = new MethodInfo();

      fillRoutine(methodInfo, access, methodName, exceptions, desc);

      String genericReturnType = transformTypeFormat(desc.substring(desc.lastIndexOf(')') + 1));
      methodInfo.setGenericReturnType(genericReturnType);
      methodInfo.setReturnType(toShortName(genericReturnType));

      StringBuilder genericBuilder = new StringBuilder();
      String stringModifier = methodInfo.modifierToString();
      if (!stringModifier.isEmpty())
      {
         genericBuilder.append(stringModifier);
         genericBuilder.append(" ");
      }
      genericBuilder.append(methodInfo.getGenericReturnType());
      genericBuilder.append(" ");
      genericBuilder.append(methodInfo.getDeclaringClass());
      genericBuilder.append(".");
      genericBuilder.append(methodInfo.getName());
      genericBuilder.append(methodInfo.getGenericParameterTypes());
      methodInfo.setGeneric(genericBuilder.toString());

      methods.add(methodInfo);
   }

   void addConstructor(int access, String[] exceptions, String desc)
   {
      RoutineInfo constructorInfo = new RoutineInfo();

      fillRoutine(constructorInfo, access, name, exceptions, desc);

      StringBuilder genericBuilder = new StringBuilder();
      String stringModifier = constructorInfo.modifierToString();
      if (!stringModifier.isEmpty())
      {
         genericBuilder.append(stringModifier);
         genericBuilder.append(" ");
      }
      genericBuilder.append(constructorInfo.getDeclaringClass());
      genericBuilder.append(constructorInfo.getGenericParameterTypes());
      constructorInfo.setGeneric(genericBuilder.toString());

      constructors.add(constructorInfo);
   }

   private void fillRoutine(RoutineInfo routineInfo, int access, String name, String[] exceptions, String desc)
   {

      routineInfo.setModifiers(access);
      routineInfo.setName(name);
      routineInfo.setDeclaringClass(qualifiedName);

      if (exceptions != null)
      {
         for (int i = 0; i < exceptions.length; i++)
         {
            exceptions[i] = exceptions[i].replace('/', '.');
         }
      }
      if (exceptions == null)
      {
         routineInfo.setGenericExceptionTypes(new String[0]);
      }
      else
      {
         routineInfo.setGenericExceptionTypes(exceptions);
      }

      String[] variables = splitMethodDesc(desc);
      StringBuilder genericParameterTypesBuilder = new StringBuilder();
      StringBuilder parameterTypesBuilder = new StringBuilder();
      genericParameterTypesBuilder.append("(");
      parameterTypesBuilder.append("(");
      for (int i = 0; i < variables.length; i++)
      {
         if (i > 0)
         {
            genericParameterTypesBuilder.append(", ");
            parameterTypesBuilder.append(", ");
         }
         genericParameterTypesBuilder.append(variables[i]);
         parameterTypesBuilder.append(toShortName(variables[i]));
      }
      genericParameterTypesBuilder.append(")");
      parameterTypesBuilder.append(")");
      routineInfo.setGenericParameterTypes(genericParameterTypesBuilder.toString());
      routineInfo.setParameterTypes(parameterTypesBuilder.toString());
   }

   void addManifest(Manifest manifest)
   {
      this.manifest = manifest;
   }

   /**
    * Method transforms variable types<br>
    * from: [Ljava/io/ObjectStreamField;<br>
    * to: java.io.ObjectStreamField[]
    * 
    * @param type
    * @return
    */
   private String transformTypeFormat(String type)
   {
      int index = 0;
      while (type.charAt(index) == '[')
      {
         index++;
      }
      return transformTypeFormat(type.substring(index), index);
   }

   /**
    * Method transforms variable types without arrays<br>
    * from: Ljava/io/ObjectStreamField;<br>
    * to: java.io.ObjectStreamField<br>
    * 
    * Mainly, this method will be used only from
    * {@link #transformTypeFormat(String)} method.<br>
    * 
    * @param type
    * @param arrayLevel
    *           level of arrays which will add after type
    * @return
    */
   private String transformTypeFormat(String type, int arrayLevel)
   {
      StringBuilder transformedTypeBuilder = new StringBuilder();
      char variableType;
      String ext = null;

      int index = 0;
      variableType = type.charAt(index);

      switch (variableType)
      {
         case 'Z' :
            transformedTypeBuilder.append("boolean");
            break;
         case 'B' :
            transformedTypeBuilder.append("byte");
            break;
         case 'C' :
            transformedTypeBuilder.append("char");
            break;
         case 'S' :
            transformedTypeBuilder.append("short");
            break;
         case 'I' :
            transformedTypeBuilder.append("int");
            break;
         case 'J' :
            transformedTypeBuilder.append("long");
            break;
         case 'F' :
            transformedTypeBuilder.append("float");
            break;
         case 'D' :
            transformedTypeBuilder.append("double");
            break;
         case 'V' :
            transformedTypeBuilder.append("void");
            break;
         case 'L' :
            // type.length() - 1 for removing ';' after class name
            ext = type.substring(index + 1, type.length() - 1);
            transformedTypeBuilder.append(ext.replace('/', '.'));
            break;
      }

      for (int i = 0; i < arrayLevel; i++)
      {
         transformedTypeBuilder.append("[]");
      }

      return transformedTypeBuilder.toString();
   }

   private String toShortName(String name)
   {
      return name.substring(name.lastIndexOf('.') + 1);
   }

   /**
    * Method get method description and return genericParameterTypes. Return
    * type ignored<br>
    * For example:<br>
    * input: (I,[D,Ljava.lang.String)V output: ["int", "double[]",
    * "java.lang.String"]
    * 
    * @param desc
    * @return
    */
   private String[] splitMethodDesc(String desc)
   {
      desc = desc.substring(0, desc.lastIndexOf(')'));
      ArrayList<String> result = new ArrayList<String>();
      int index = 0;
      int arrayLevel = 0;
      while (index < desc.length())
      {
         char c = desc.charAt(index);
         // skip separator characters
         if (c == '[')
         {
            arrayLevel++;
         }
         else if (!(c == '(' || c == ')' || c == ' '))
         {
            if (c == 'L')
            {
               // object
               int start = index;
               index++;
               while (desc.charAt(index) != ';')
               {
                  index++;
               }
               result.add(transformTypeFormat(desc.substring(start, index + 1), arrayLevel));
            }
            else
            {
               // primitive
               result.add(transformTypeFormat(desc.substring(index, index + 1), arrayLevel));
            }
            arrayLevel = 0;
         }
         index++;
      }
      return result.toArray(new String[0]);
   }

   public static String toString(TypeInfo typeInfo)
   {

      StringBuilder builder = new StringBuilder();

      builder.append(typeInfo.modifierToString());
      builder.append(" ");
      builder.append(typeInfo.getType().toLowerCase());
      builder.append(" ");
      builder.append(typeInfo.getName());
      if (!typeInfo.getSuperClass().equals("java.lang.Object"))
      {
         builder.append(" extends ");
         builder.append(typeInfo.getSuperClass());
      }
      builder.append(" {\n");

      if (typeInfo.getFields().length > 0)
      {
         for (FieldInfo field : typeInfo.getFields())
         {
            builder.append("   ");
            String stringModifier = field.modifierToString();
            if (!stringModifier.isEmpty())
            {
               builder.append(stringModifier);
               builder.append(" ");
            }
            builder.append(field.getType());
            builder.append(" ");
            builder.append(field.getName());
            builder.append(";\n");
         }
         builder.append("\n");
      }

      if (typeInfo.getConstructors().length > 0)
      {
         for (RoutineInfo constructor : typeInfo.getConstructors())
         {
            builder.append("   ");
            builder.append(constructor.getGeneric());
            if (constructor.getGenericExceptionTypes().length > 0)
            {
               builder.append(" throws ");
               boolean z = false;
               for (String exception : constructor.getGenericExceptionTypes())
               {
                  if (z)
                  {
                     builder.append(", ");
                  }
                  builder.append(exception);
                  z = true;
               }
            }
            builder.append(";\n");
         }
         builder.append("\n");
      }

      for (MethodInfo method : typeInfo.getMethods())
      {
         builder.append("   ");
         builder.append(method.getGeneric());
         if (method.getGenericExceptionTypes().length > 0)
         {
            builder.append(" throws ");
            boolean z = false;
            for (String exception : method.getGenericExceptionTypes())
            {
               if (z)
               {
                  builder.append(", ");
               }
               builder.append(exception);
               z = true;
            }
         }
         builder.append(";\n");
      }

      builder.append("}\n");

      return builder.toString();
   }

}
