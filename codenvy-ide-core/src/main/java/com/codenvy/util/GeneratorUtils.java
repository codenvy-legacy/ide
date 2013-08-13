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
package com.codenvy.util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> */
public class GeneratorUtils {

    /** CLI Argument */
    public static final String ROOT_DIR_PARAMETER = "--rootDir=";

    /**
     * Extracts Package declaration from file
     *
     * @param fileName
     * @param content
     * @return
     * @throws IOException
     */
    public static String getClassFQN(String fileName, String content) throws IOException {
        Matcher matcher = GeneratorUtils.PACKAGE_PATTERN.matcher(content);
        if (!matcher.matches()) {
            throw new IOException(String.format("Class %s doesn't seem to be valid. Package declaration is missing.",
                                                fileName));
        }
        if (matcher.groupCount() != 1) {
            throw new IOException(String.format("Class %s doesn't seem to be valid. Package declaration is missing.",
                                                fileName));
        }
        return matcher.group(1);
    }

    /** Reg Exp that matches the package declaration */
    public static final Pattern PACKAGE_PATTERN      = Pattern
            .compile(".*package\\s+([a-zA_Z_][\\.\\w]*);.*", Pattern.DOTALL);
    /** Current Package name, used to avoid miss-hits of Extension's lookup */
    static final        String  COM_CODENVY_IDE_UTIL = "com.codenvy.ide.util";
    public static final String  TAB                  = "   ";
    public static final String  TAB2                 = TAB + TAB;

}
