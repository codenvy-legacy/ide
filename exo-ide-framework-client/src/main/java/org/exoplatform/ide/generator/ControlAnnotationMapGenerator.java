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
package org.exoplatform.ide.generator;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JWildcardType.BoundType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;

import org.exoplatform.ide.client.framework.annotation.DisableInTempWorkspace;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;

import java.util.ArrayList;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 21, 2010 $
 */
public class ControlAnnotationMapGenerator extends ClassAnnotationMapGenerator {
    public static final String CLASS_NAME = org.exoplatform.gwtframework.ui.client.command.Control.class.getName();

    /**
     * @see org.exoplatform.ide.generator.ClassAnnotationMapGenerator#writeConstructor(com.google.gwt.uibinder.rebind.IndentedWriter,
     *      com.google.gwt.core.ext.typeinfo.JClassType, java.lang.String, com.google.gwt.core.ext.GeneratorContext)
     */
    @Override
    protected void writeConstructor(ConsolePrintWriter writer, JClassType interfaceType, String implName,
                                    GeneratorContext context) {
        writer.write("public %s()", implName);
        writer.println();
        writer.write("{");
        writer.println();
        JClassType[] subTypes = getSubTypes(CLASS_NAME, context);
        if (subTypes != null) {
            writer.write("List<String> values;");
            writer.println();
            for (JClassType type : subTypes) {
                writer.write("values = new ArrayList<String>();");
                writer.println();
                ArrayList<String> list = new ArrayList<>();
                if (type.isAnnotationPresent(RolesAllowed.class)) {
                    for (String value : type.getAnnotation(RolesAllowed.class).value()) {
                        writer.write("values.add(\"" + value + "\");");
                        list.add(value);
                        writer.println();
                    }
                }
                if (type.isAnnotationPresent(DisableInTempWorkspace.class)) {
                    writer.write("values.add(\"" + DisableInTempWorkspace.class.getName() + "\");");
                    list.add(DisableInTempWorkspace.class.getName());
                    writer.println();
                }
                writer.write("classAnnotations.put(\"%s\", values);", type.getQualifiedSourceName());
                writer.println();

            }
        }
        writer.write("}");
        writer.println();
    }

    /**
     * @param className
     *         name of the super class
     * @param context
     *         generator context
     * @return {@link JClassType[]} sub types of the pointed super class
     */
    private JClassType[] getSubTypes(String className, GeneratorContext context) {
        try {
            JClassType superClass = context.getTypeOracle().getType(className);
            JClassType[] subTypes = context.getTypeOracle().getWildcardType(BoundType.EXTENDS, superClass).getSubtypes();
            return subTypes;
        } catch (NotFoundException e) {
            return null;
        }
    }

}
