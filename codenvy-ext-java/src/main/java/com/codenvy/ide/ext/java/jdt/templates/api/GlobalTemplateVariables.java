/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sebastian Davids: sdavids@gmx.de - see bug 25376
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.templates.api;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

/**
 * Global variables which are available in any context.
 * <p>
 * Clients may instantiate the classes contained within this class.
 * </p>
 *
 * @since 3.0
 */
public class GlobalTemplateVariables {

    /** The type of the selection variables. */
    public static final String SELECTION = "selection"; //$NON-NLS-1$

    /** The cursor variable determines the cursor placement after template edition. */
    public static class Cursor extends SimpleTemplateVariableResolver {

        /** Name of the cursor variable, value= {@value} */
        public static final String NAME = "cursor"; //$NON-NLS-1$

        /** Creates a new cursor variable */
        public Cursor() {
            super(NAME, TextTemplateMessages.MESSAGES.variableDescriptionCursor()); //$NON-NLS-1$
            setEvaluationString(""); //$NON-NLS-1$
        }
    }

    /** The word selection variable determines templates that work on a full lines selection. */
    public static class WordSelection extends SimpleTemplateVariableResolver {

        /** Name of the word selection variable, value= {@value} */
        public static final String NAME = "word_selection"; //$NON-NLS-1$

        /** Creates a new word selection variable */
        public WordSelection() {
            super(NAME, TextTemplateMessages.MESSAGES.variableDescriptionSelectedWord()); //$NON-NLS-1$
        }

        protected String resolve(TemplateContext context) {
            String selection = context.getVariable(SELECTION);
            if (selection == null)
                return ""; //$NON-NLS-1$
            return selection;
        }
    }

    /** The line selection variable determines templates that work on selected lines. */
    public static class LineSelection extends SimpleTemplateVariableResolver {

        /** Name of the line selection variable, value= {@value} */
        public static final String NAME = "line_selection"; //$NON-NLS-1$

        /** Creates a new line selection variable */
        public LineSelection() {
            super(NAME, TextTemplateMessages.MESSAGES.variableDescriptionSelectedLines()); //$NON-NLS-1$
        }

        protected String resolve(TemplateContext context) {
            String selection = context.getVariable(SELECTION);
            if (selection == null)
                return ""; //$NON-NLS-1$
            return selection;
        }
    }

    /** The dollar variable inserts an escaped dollar symbol. */
    public static class Dollar extends SimpleTemplateVariableResolver {
        /** Creates a new dollar variable */
        public Dollar() {
            super("dollar", TextTemplateMessages.MESSAGES.variableDescriptionDollar()); //$NON-NLS-1$ //$NON-NLS-2$
            setEvaluationString("$"); //$NON-NLS-1$
        }
    }

    /** The date variable evaluates to the current date. */
    public static class Date extends SimpleTemplateVariableResolver {
        /** Creates a new date variable */
        public Date() {
            super("date", TextTemplateMessages.MESSAGES.variableDescriptionDate()); //$NON-NLS-1$ //$NON-NLS-2$
        }

        protected String resolve(TemplateContext context) {
            return DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM).format(new java.util.Date());
        }
    }

    /** The year variable evaluates to the current year. */
    public static class Year extends SimpleTemplateVariableResolver {
        /** Creates a new year variable */
        public Year() {
            super("year", TextTemplateMessages.MESSAGES.variableDescriptionYear()); //$NON-NLS-1$ //$NON-NLS-2$
        }

        protected String resolve(TemplateContext context) {
            return DateTimeFormat.getFormat(PredefinedFormat.YEAR).format(new java.util.Date());
        }
    }

    /** The time variable evaluates to the current time. */
    public static class Time extends SimpleTemplateVariableResolver {
        /** Creates a new time variable */
        public Time() {
            super("time", TextTemplateMessages.MESSAGES.variableDescriptionTime()); //$NON-NLS-1$ //$NON-NLS-2$
        }

        /** {@inheritDoc} */
        protected String resolve(TemplateContext context) {
            return DateTimeFormat.getFormat(PredefinedFormat.TIME_MEDIUM).format(new java.util.Date());
        }
    }

    /** The user variable evaluates to the current user. */
    public static class User extends SimpleTemplateVariableResolver {
        /** Creates a new user name variable */
        public User() {
            super("user", TextTemplateMessages.MESSAGES.variableDescriptionUser()); //$NON-NLS-1$ //$NON-NLS-2$
        }

        /** {@inheritDoc} */
        protected String resolve(TemplateContext context) {
            // TODO read user name
            return "user.name"; //$NON-NLS-1$
        }
    }
}
