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
package com.codenvy.ide.ext.java.jdi.server.model;


import com.codenvy.ide.ext.java.jdi.shared.Location;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class LocationImpl implements Location {
    private String className;
    private int    lineNumber;

    public LocationImpl(String className, int lineNumber) {
        this.className = className;
        this.lineNumber = lineNumber;
    }

    public LocationImpl() {
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (className == null ? 0 : className.hashCode());
        hash = 31 * hash + lineNumber;
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LocationImpl other = (LocationImpl)o;
        return lineNumber == other.lineNumber
               && (className == null ? other.className == null : className.equals(other.className));
    }

    @Override
    public String toString() {
        return "LocationImpl{" +
               "className='" + className + '\'' +
               ", lineNumber=" + lineNumber +
               '}';
    }
}
