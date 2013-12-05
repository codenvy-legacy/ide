/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.java.jdt.templates;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class TemplatesJs {

    protected TemplatesJs(){

    }

    public static String templates() {
        return new StringBuilder().append("{\"templates\" :\n").append("                [\n").append("                    {\n")
                                  .append("                        \"autoinsert\": true,\n")
                                  .append("                        \"context\": \"gettercomment_context\",\n")
                                  .append("                        \"deleted\": false,\n")
                                  .append("                        \"description\": \"Comment for getter method\",\n")
                                  .append("                        \"enabled\": true,\n")
                                  .append("                        \"id\": \"org.eclipse.jdt.ui.text.codetemplates.gettercomment\",\n")
                                  .append("                        \"name\": \"gettercomment\",\n")
                                  .append("                        \"text\": \"/**\\n * @return the ${bare_field_name}\\n *\\/\"\n")
                                  .append("                    },\n").append("                    {\n")
                                  .append("                        \"autoinsert\": true,\n")
                                  .append("                        \"context\": \"settercomment_context\",\n")
                                  .append("                        \"deleted\": false,\n")
                                  .append("                        \"description\": \"Comment for setter method\",\n")
                                  .append("                        \"enabled\": true,\n")
                                  .append("                        \"id\": \"org.eclipse.jdt.ui.text.codetemplates.settercomment\",\n")
                                  .append("                        \"name\": \"settercomment\",\n")
                                  .append("                        \"text\": \"/**\\n * @param ${param} the ${bare_field_name} to set\\n " +
                                          "*\\/\"\n")
                                  .append("                    },\n").append("                    {\n")
                                  .append("                        \"autoinsert\": true,\n")
                                  .append("                        \"context\": \"constructorcomment_context\",\n")
                                  .append("                        \"deleted\": false,\n")
                                  .append("                        \"description\": \"Comment for created constructors\",\n")
                                  .append("                        \"enabled\": true,\n")
                                  .append("                        \"id\": \"org.eclipse.jdt.ui.text.codetemplates.constructorcomment\",\n")
                                  .append("                        \"name\": \"constructorcomment\",\n")
                                  .append("                        \"text\": \"/**\\b * ${tags}\\n *\\/\"\n")
                                  .append("                    },\n").append("                    {\n")
                                  .append("                        \"autoinsert\": true,\n")
                                  .append("                        \"context\": \"fieldcomment_context\",\n")
                                  .append("                        \"deleted\": false,\n")
                                  .append("                        \"description\": \"Comment for fields\",\n")
                                  .append("                        \"enabled\": true,\n")
                                  .append("                        \"id\": \"org.eclipse.jdt.ui.text.codetemplates.fieldcomment\",\n")
                                  .append("                        \"name\": \"fieldcomment\",\n")
                                  .append("                        \"text\": \"/**\\n *\\n *\\/\"\n").append("                    },\n")
                                  .append("                    {\n").append("                        \"autoinsert\": true,\n")
                                  .append("                        \"context\": \"methodcomment_context\",\n")
                                  .append("                        \"deleted\": false,\n")
                                  .append("                        \"description\": \"Comment for non-overriding methods\",\n")
                                  .append("                        \"enabled\": true,\n")
                                  .append("                        \"id\": \"org.eclipse.jdt.ui.text.codetemplates.methodcomment\",\n")
                                  .append("                        \"name\": \"methodcomment\",\n")
                                  .append("                        \"text\": \"/**\\n * ${tags}\\n *\\/\"\n")
                                  .append("                    },\n").append("                    {\n")
                                  .append("                        \"autoinsert\": false,\n")
                                  .append("                        \"context\": \"overridecomment_context\",\n")
                                  .append("                        \"deleted\": false,\n")
                                  .append("                        \"description\": \"Comment for overriding methods\",\n")
                                  .append("                        \"enabled\": true,\n")
                                  .append("                        \"id\": \"org.eclipse.jdt.ui.text.codetemplates.overridecomment\",\n")
                                  .append("                        \"name\": \"overridecomment\",\n")
                                  .append("                        \"text\": \"/**\\n * ${see_to_overridden}\\n *\\/\"\n")
                                  .append("                    },\n").append("                    {\n")
                                  .append("                        \"autoinsert\": true,\n")
                                  .append("                        \"context\": \"delegatecomment_context\",\n")
                                  .append("                        \"deleted\": false,\n")
                                  .append("                        \"description\": \"Comment for delegate methods\",\n")
                                  .append("                        \"enabled\": true,\n")
                                  .append("                        \"id\": \"org.eclipse.jdt.ui.text.codetemplates.delegatecomment\",\n")
                                  .append("                        \"name\": \"delegatecomment\",\n")
                                  .append("                        \"text\": \"/**\\n * ${tags}\\n * ${see_to_target}\\n *\\/\"\n")
                                  .append("                    },\n").append("                    {\n")
                                  .append("                        \"autoinsert\": true,\n")
                                  .append("                        \"context\": \"newtype_context\",\n")
                                  .append("                        \"deleted\": false,\n")
                                  .append("                        \"description\": \"Newly created files\",\n")
                                  .append("                        \"enabled\": true,\n")
                                  .append("                        \"id\": \"org.eclipse.jdt.ui.text.codetemplates.newtype\",\n")
                                  .append("                        \"name\": \"newtype\",\n")
                                  .append("                        \"text\": " +
                                          "\"${filecomment}\\n${package_declaration}\\n\\n${typecomment}\\n${type_declaration}\"\n")
                                  .append("                    },\n").append("                    {\n")
                                  .append("                        \"autoinsert\": true,\n")
                                  .append("                        \"context\": \"catchblock_context\",\n")
                                  .append("                        \"deleted\": false,\n")
                                  .append("                        \"description\": \"Code in new catch blocks\",\n")
                                  .append("                        \"enabled\": true,\n")
                                  .append("                        \"id\": \"org.eclipse.jdt.ui.text.codetemplates.catchblock\",\n")
                                  .append("                        \"name\": \"catchblock\",\n")
                                  .append("                        \"text\": \"// ${todo} Auto-generated catch block\\n${exception_var}" +
                                          ".printStackTrace();\"\n")
                                  .append("                    },\n").append("                    {\n")
                                  .append("                        \"autoinsert\": true,\n")
                                  .append("                        \"context\": \"methodbody_context\",\n")
                                  .append("                        \"deleted\": false,\n")
                                  .append("                        \"description\": \"Code in created method stubs\",\n")
                                  .append("                        \"enabled\": true,\n")
                                  .append("                        \"id\": \"org.eclipse.jdt.ui.text.codetemplates.methodbody\",\n")
                                  .append("                        \"name\": \"methodbody\",\n")
                                  .append("                        \"text\": \"// ${todo} Auto-generated method " +
                                          "stub\\n${body_statement}\"\n")
                                  .append("                    },\n").append("                    {\n")
                                  .append("                        \"autoinsert\": true,\n")
                                  .append("                        \"context\": \"constructorbody_context\",\n")
                                  .append("                        \"deleted\": false,\n")
                                  .append("                        \"description\": \"Code in created constructor stubs\",\n")
                                  .append("                        \"enabled\": true,\n")
                                  .append("                        \"id\": \"org.eclipse.jdt.ui.text.codetemplates.constructorbody\",\n")
                                  .append("                        \"name\": \"constructorbody\",\n")
                                  .append("                        \"text\": \"${body_statement}\\n// ${todo} Auto-generated constructor " +
                                          "stub\"\n")
                                  .append("                    },\n").append("                    {\n")
                                  .append("                        \"autoinsert\": true,\n")
                                  .append("                        \"context\": \"getterbody_context\",\n")
                                  .append("                        \"deleted\": false,\n")
                                  .append("                        \"description\": \"Code in created getters\",\n")
                                  .append("                        \"enabled\": true,\n")
                                  .append("                        \"id\": \"org.eclipse.jdt.ui.text.codetemplates.getterbody\",\n")
                                  .append("                        \"name\": \"getterbody\",\n")
                                  .append("                        \"text\": \"return ${field};\"\n").append("                    },\n")
                                  .append("                    {\n").append("                        \"autoinsert\": true,\n")
                                  .append("                        \"context\": \"setterbody_context\",\n")
                                  .append("                        \"deleted\": false,\n")
                                  .append("                        \"description\": \"Code in created setters\",\n")
                                  .append("                        \"enabled\": true,\n")
                                  .append("                        \"id\": \"org.eclipse.jdt.ui.text.codetemplates.setterbody\",\n")
                                  .append("                        \"name\": \"setterbody\",\n")
                                  .append("                        \"text\": \"${field} = ${param};\"\n").append("                    }\n")
                                  .append("                ]}").toString();
    };

    public  static String codeTemplate() {
        return "{\n" + "            \"codeTemplates\"\n" + "                :\n" + "                [\n" + "                    {\n" +
               "                        \"name\": \"for\",\n" + "                        \"description\": \"iterate over array\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.for_array\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"for (int ${index} = 0; ${index} < ${array}.length; " +
               "${index}++) {\\n    " + "${line_selection}${cursor}\\n}\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"for\",\n" +
               "                        \"description\": \"iterate over array with temporary variable\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.for_temp\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"for (int ${index} = 0; ${index} < ${array}.length; " +
               "${index}++) {\\n    ${array_type} " + "${array_element} = ${array}[${index}];\\n  ${cursor}\\n}\"\n" +
               "                    },\n" + "                    {\n" + "                        \"name\": \"for\",\n" +
               "                        \"description\": \"iterate over collection\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.for_collection\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"for (${iteratorType:newType(java.util.Iterator)} " +
               "${iterator} = ${collection}.iterator" +
               "(); ${iterator}.hasNext(); ) {\\n ${type:elemType(collection)} ${name:newName(type)} = " +
               "(${type}) ${iterator}.next();\\n  " + "  ${cursor} \\n}\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"foreach\",\n" +
               "                        \"description\": \"iterate over an array or Iterable\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.for_iterable\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"for (${iterable_type} ${iterable_element} : ${iterable}) " +
               "{\\n  ${cursor}\\n}\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"while\",\n" +
               "                        \"description\": \"iterate with enumeration\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.while_enumeration\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"while (${en:var(java.util.Enumeration)}.hasMoreElements())" +
               " {\\n    ${type:argType(en)" + "} ${elem:newName(type)} = (${type}) ${en}.nextElement();\\n    ${cursor}\\n}\"\n" +
               "                    },\n" + "                    {\n" + "                        \"name\": \"while\",\n" +
               "                        \"description\": \"iterate with iterator\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.while_iterator\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"while (${it:var(java.util.Iterator)}.hasNext()) {\\n    " +
               "${type:argType(it)} " + "${elem:newName(type)} = (${type}) ${it}.next();\\n    ${cursor}\\n}\"\n" +
               "                    },\n" + "                    {\n" + "                        \"name\": \"do\",\n" +
               "                        \"description\": \"do while statement\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.do\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"do {\\n    ${line_selection}${cursor}\\n} while " +
               "(${condition:var(boolean)});\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"switch\",\n" + "                        \"description\": \"switch case statement\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.switch\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"switch (${key}) {\\n    case ${value}:\\n        " +
               "${cursor}\\n        break;\\n    " + "default:\\n        break;\\n}\"\n" + "                    },\n" +
               "                    {\n" + "                        \"name\": \"if\",\n" +
               "                        \"description\": \"if statement\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.if\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"if (${condition:var(boolean)}) {\\n    " +
               "${line_selection}${cursor}\\n}\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"ifelse\",\n" + "                        \"description\": \"if else statement\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.ifelse\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"if (${condition:var(boolean)}) {\\n    ${cursor}\\n} else " +
               "{\\n\\n}\"\n" + "                    },\n" + "                    {\n" + "                        \"name\": \"elseif\",\n" +
               "                        \"description\": \"else if block\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.elseif\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"else if (${condition:var(boolean)}) {\\n    " +
               "${cursor}\\n}\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"else\",\n" + "                        \"description\": \"else block\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.else\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" + "                        \"text\": \"else {\\n    ${cursor}\\n}\"\n" +
               "                    },\n" + "                    {\n" + "                        \"name\": \"try\",\n" +
               "                        \"description\": \"try catch block\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.try\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"try {\\n    ${line_selection}${cursor}\\n} catch " +
               "(${Exception} " + "${exception_variable_name}) {\\n    // ${todo}: handle exception\\n}\"\n" + "                    },\n" +
               "                    {\n" + "                        \"name\": \"catch\",\n" +
               "                        \"description\": \"catch block\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.catch\",\n" +
               "                        \"context\": \"java\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"catch (${Exception} ${exception_variable_name}) {\\n    " +
               "${cursor}// ${todo}: handle " + "exception\\n}\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"main\",\n" + "                        \"description\": \"main method\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.main\",\n" +
               "                        \"context\": \"java-members\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"public static void main(String[] args) {\\n    " +
               "${cursor}\\n}\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"public_method\",\n" + "                        \"description\": \"public method\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.public_method\",\n" +
               "                        \"context\": \"java-members\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"public ${return_type} ${name}(${}) {\\n    " +
               "${cursor}\\n}\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"protected_method\",\n" +
               "                        \"description\": \"protected method\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.protected_method\",\n" +
               "                        \"context\": \"java-members\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"protected ${return_type} ${name}(${}) {\\n    " +
               "${cursor}\\n}\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"private_method\",\n" +
               "                        \"description\": \"private method\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.private_method\",\n" +
               "                        \"context\": \"java-members\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"private ${return_type} ${name}(${}) {\\n    " +
               "${cursor}\\n}\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"private_static_method\",\n" +
               "                        \"description\": \"private static method\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.private_static_method\",\n" +
               "                        \"context\": \"java-members\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"private static ${return_type} ${name}(${}) {\\n    " +
               "${cursor}\\n}\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"instanceof\",\n" +
               "                        \"description\": \"dynamic type test and cast\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.instanceof\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"if (${name:var} instanceof ${type}) {\\n    ${type} " +
               "${new_name} = (${type})${name};" + "\\n    ${cursor}\\n}\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"cast\",\n" + "                        \"description\": \"dynamic cast\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.cast\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"${type} ${new_name} = (${type}) ${name};\"\n" + "                    },\n" +
               "                    {\n" + "                        \"name\": \"new\",\n" +
               "                        \"description\": \"create new object\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.new\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"${type} ${name} = new ${type}(${arguments});\"\n" + "                    },\n" +
               "                    {\n" + "                        \"name\": \"lazy\",\n" +
               "                        \"description\": \"lazy creation\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.lazy\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"if (${name:var} == null) {\\n    ${name} = new ${type}" +
               "(${arguments});\\n    " + "${cursor}\\n}\\n\\nreturn ${name};\"\n" + "                    },\n" +
               "                    {\n" + "                        \"name\": \"toarray\",\n" +
               "                        \"description\": \"convert collection to array\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.toarray\",\n" +
               "                        \"context\": \"java\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"(${type:elemType(collection)}[]) ${collection}.toArray(new" +
               " ${type}[${collection}.size" + "()])\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"sysout\",\n" + "                        \"description\": \"print to standard out\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.sysout\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": true,\n" +
               "                        \"text\": \"System.out.println(${word_selection}${});${cursor}\"\n" + "                    },\n" +
               "                    {\n" + "                        \"name\": \"syserr\",\n" +
               "                        \"description\": \"print to standard error\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.syserr\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": true,\n" +
               "                        \"text\": \"System.err.println(${word_selection}${});${cursor}\"\n" + "                    },\n" +
               "                    {\n" + "                        \"name\": \"systrace\",\n" +
               "                        \"description\": \"print current method to standard out\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.systrace\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": true,\n" +
               "                        \"text\": \"System.out.println(\\\"${enclosing_type}" +
               ".${enclosing_method}()\\\");\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"<code>\",\n" + "                        \"description\": \"<code></code>\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.code_tag\",\n" +
               "                        \"context\": \"javadoc\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": true,\n" +
               "                        \"text\": \"<code>${word_selection}${}</code>${cursor}\"\n" + "                    },\n" +
               "                    {\n" + "                        \"name\": \"null\",\n" +
               "                        \"description\": \"<code>null</code>\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.code_tag_null\",\n" +
               "                        \"context\": \"javadoc\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": true,\n" + "                        \"text\": \"<code>null</code>\"\n" +
               "                    },\n" + "                    {\n" + "                        \"name\": \"true\",\n" +
               "                        \"description\": \"<code>true</code>\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.code_tag_true\",\n" +
               "                        \"context\": \"javadoc\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": true,\n" + "                        \"text\": \"<code>true</code>\"\n" +
               "                    },\n" + "                    {\n" + "                        \"name\": \"false\",\n" +
               "                        \"description\": \"<code>false</code>\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.code_tag_false\",\n" +
               "                        \"context\": \"javadoc\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": true,\n" + "                        \"text\": \"<code>false</code>\"\n" +
               "                    },\n" + "                    {\n" + "                        \"name\": \"<pre>\",\n" +
               "                        \"description\": \"<pre></pre>\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.pre_tag\",\n" +
               "                        \"context\": \"javadoc\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": true,\n" +
               "                        \"text\": \"<pre>${word_selection}${}</pre>${cursor}\"\n" + "                    },\n" +
               "                    {\n" + "                        \"name\": \"<b>\",\n" +
               "                        \"description\": \"<b></b>\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.b_tag\",\n" +
               "                        \"context\": \"javadoc\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": true,\n" +
               "                        \"text\": \"<b>${word_selection}${}</b>${cursor}\"\n" + "                    },\n" +
               "                    {\n" + "                        \"name\": \"<i>\",\n" +
               "                        \"description\": \"<i></i>\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.i_tag\",\n" +
               "                        \"context\": \"javadoc\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": true,\n" +
               "                        \"text\": \"<i>${word_selection}${}</i>${cursor}\"\n" + "                    },\n" +
               "                    {\n" + "                        \"name\": \"@author\",\n" +
               "                        \"description\": \"author name\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.author\",\n" +
               "                        \"context\": \"javadoc\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": true,\n" + "                        \"text\": \"@author ${user}\"\n" +
               "                    },\n" + "                    {\n" + "                        \"name\": \"while\",\n" +
               "                        \"description\": \"while loop with condition\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.while_condition\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"while (${condition:var(boolean)}) {\\n    " +
               "${line_selection}${cursor}\\n}\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"test\",\n" + "                        \"description\": \"test method\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.test\",\n" +
               "                        \"context\": \"java-members\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"public void test${name}() throws Exception {\\n    " +
               "${cursor}\\n}\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"Test\",\n" + "                        \"description\": \"test method (JUnit 4)\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.test_junit4\",\n" +
               "                        \"context\": \"java-members\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"@${testType:newType(org.junit.Test)}\\npublic void " +
               "${testName}() throws Exception " + "{\\n    ${staticImport:importStatic('org.junit.Assert.*')}${cursor}\\n}\"\n" +
               "                    },\n" + "                    {\n" + "                        \"name\": \"nls\",\n" +
               "                        \"description\": \"non-externalized string marker\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.non-nls\",\n" +
               "                        \"context\": \"java\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" + "                        \"text\": \"//$$NON-NLS-${N}$$\"\n" +
               "                    },\n" + "                    {\n" + "                        \"name\": \"fall-through\",\n" +
               "                        \"description\": \"$FALL-THROUGH$ marker\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.fall-through\",\n" +
               "                        \"context\": \"java\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" + "                        \"text\": \"//$$FALL-THROUGH$$\"\n" +
               "                    },\n" + "                    {\n" + "                        \"name\": \"synchronized\",\n" +
               "                        \"description\": \"synchronized block\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.synchronized\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"synchronized (${mutex:var}) {\\n    " +
               "${line_selection}\\n}\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"runnable\",\n" + "                        \"description\": \"runnable\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.runnable\",\n" +
               "                        \"context\": \"java\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"new Runnable() {\\n    public void run() {\\n        " +
               "${line_selection}\\n    " + "}\\n}\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"arraymerge\",\n" +
               "                        \"description\": \"merge two arrays into one\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.arraymerge\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"${array_type}[] ${result:newName(array1)} = new " +
               "${array_type}[${array1:array}.length " + "+ ${array}.length];\\nSystem.arraycopy(${array1}, 0, ${result}, 0, " +
               "${array1}.length);\\nSystem.arraycopy(${array}, 0, " + "${result}, ${array1}.length, ${array}.length);\"\n" +
               "                    },\n" + "                    {\n" + "                        \"name\": \"arrayadd\",\n" +
               "                        \"description\": \"add an element to an array\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.arrayadd\",\n" +
               "                        \"context\": \"java-statements\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"${array_type}[] ${result:newName(array)} = new " +
               "${array_type}[${array}.length + 1];" +
               "\\nSystem.arraycopy(${array}, 0, ${result}, 0, ${array}.length);\\n${result}[${array}.length]=" +
               " ${var};\"\n" + "                    },\n" + "                    {\n" +
               "                        \"name\": \"static_final\",\n" +
               "                        \"description\": \"static final field\",\n" +
               "                        \"id\": \"org.eclipse.jdt.ui.templates.static_final\",\n" +
               "                        \"context\": \"java-members\",\n" + "                        \"enabled\": true,\n" +
               "                        \"autoinsert\": false,\n" +
               "                        \"text\": \"${visibility:link(public,protected," +
               "private)} static final ${type:link(String," + "int)} ${NAME};\"\n" + "                    }\n" + "                ]}";
    }



}
