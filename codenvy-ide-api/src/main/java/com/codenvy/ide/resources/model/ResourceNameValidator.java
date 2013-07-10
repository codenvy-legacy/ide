/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.resources.model;

import com.google.gwt.regexp.shared.RegExp;

/**
 * Used for validating new resource names.
 * <b>Note, methods in this class check names only of limitation provided by Resource API</b>
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *          //TODO check how to use GWT Validation API https://developers.google.com/web-toolkit/doc/latest/DevGuideValidation
 */
public class ResourceNameValidator {

    private static final RegExp PROJECT_VALIDATOR = RegExp.compile("^[A-Za-z0-9_]*$");

    private static final RegExp FOLDER_VALIDATOR = RegExp.compile("^[A-Za-z0-9_/.]*$");

    private static final RegExp FILE_VALIDATOR = RegExp.compile("^[A-Za-z0-9_.]*$");

    public static boolean isFileNameValid(String name) {
        return FILE_VALIDATOR.test(name);
    }

    public static boolean isFolderNameValid(String name) {
        return FOLDER_VALIDATOR.test(name);
    }

    public static boolean isProjectNameValid(String name) {
        return PROJECT_VALIDATOR.test(name);
    }


}
