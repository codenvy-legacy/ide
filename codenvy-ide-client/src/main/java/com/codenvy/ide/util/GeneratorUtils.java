/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.util;

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
