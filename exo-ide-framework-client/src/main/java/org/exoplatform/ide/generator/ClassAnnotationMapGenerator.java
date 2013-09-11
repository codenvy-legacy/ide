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

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 21, 2010 $
 */
public abstract class ClassAnnotationMapGenerator extends BaseGenerator {
    /**
     * @see org.exoplatform.ide.generator.BaseGenerator#doGenerate(com.google.gwt.core.ext.typeinfo.JClassType, java.lang.String,
     *      com.google.gwt.uibinder.rebind.IndentedWriter, com.google.gwt.core.ext.GeneratorContext)
     */
    @Override
    protected void doGenerate(JClassType interfaceType, String implName, ConsolePrintWriter writer,
                              GeneratorContext context) {
        writeImports(writer);
        writeClassIntro(interfaceType, implName, writer);
        writeConstructor(writer, interfaceType, implName, context);
        writeOutro(writer);
    }

    /**
     * Writes imports source of the class.
     *
     * @param writer
     *         source writer
     */
    private void writeImports(ConsolePrintWriter writer) {
        writer.write(IMPORT, java.util.HashMap.class.getName());
        writer.println();
        writer.write(IMPORT, java.util.List.class.getName());
        writer.println();
        writer.write(IMPORT, java.util.ArrayList.class.getName());
        writer.println();
    }

    /**
     * Writes the source of class introduction
     *
     * @param interfaceType
     *         implemented interface type
     * @param implName
     *         class name
     * @param writer
     *         source write
     */
    protected void writeClassIntro(JClassType interfaceType, String implName, ConsolePrintWriter writer) {
        writer.write("public class %1$s extends %2$s {", implName, interfaceType.getName());
        writer.println();
    }

    protected void writeOutro(ConsolePrintWriter writer) {
        writer.write("}");
    }

    /**
     * @param writer
     *         source write
     * @param interfaceType
     *         implemented interface type
     * @param implName
     *         class name
     * @param context
     *         generator context
     */
    abstract protected void writeConstructor(ConsolePrintWriter writer, JClassType interfaceType, String implName,
                                             GeneratorContext context);

}
