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

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

import java.io.PrintWriter;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 21, 2010 $
 */
public abstract class BaseGenerator extends Generator {
    protected static final String IMPORT = "import %1$s;";

    protected static final String PACKAGE = "package %s;";

    /**
     * @see com.google.gwt.core.ext.Generator#generate(com.google.gwt.core.ext.TreeLogger,
     *      com.google.gwt.core.ext.GeneratorContext, java.lang.String)
     */
    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName)
            throws UnableToCompleteException {
        JClassType interfaceType = getInterfaceType(context.getTypeOracle(), typeName, logger);

        String packageName = interfaceType.getPackage().getName();

        // Create implementation class name
        String implName = interfaceType.getName().replace(".", "_") + "Impl";
        PrintWriter printWriter = context.tryCreate(logger, packageName, implName);
        if (printWriter != null) {
            ConsolePrintWriter writer = new ConsolePrintWriter(printWriter);
            // Write package information to generating class
            writer.write(PACKAGE, packageName);
            writer.println();
            // Generate source of the class
            doGenerate(interfaceType, implName, writer, context);
            context.commit(logger, printWriter);
        }
        return packageName + "." + implName;
    }

    protected JClassType getInterfaceType(TypeOracle oracle, String s, TreeLogger treeLogger)
            throws UnableToCompleteException {
        JClassType interfaceType;
        try {
            interfaceType = oracle.getType(s);
        } catch (NotFoundException e) {
            treeLogger.log(TreeLogger.ERROR,
                           String.format("%s: Could not find the interface [%s]. %s", e.getClass().getName(), s, e.getMessage()));
            throw new UnableToCompleteException();
        }
        return interfaceType;
    }

    /**
     * @param interfaceType
     *         interface for generating class
     * @param implName
     *         name of the generating class
     * @param writer
     *         source writer
     * @param context
     *         generator context
     */
    abstract protected void doGenerate(JClassType interfaceType, String implName, ConsolePrintWriter writer,
                                       GeneratorContext context);
}
