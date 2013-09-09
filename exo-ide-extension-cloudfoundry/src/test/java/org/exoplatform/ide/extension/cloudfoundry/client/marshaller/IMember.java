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
package org.exoplatform.ide.extension.cloudfoundry.client.marshaller;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public interface IMember {

    /** @return the modifiers */
    Integer getModifiers();

    /** @return the name */
    String getName();

    /**
     * @param modifiers
     *         the modifiers to set
     */
    void setModifiers(Integer modifiers);

    /**
     * @param name
     *         the name to set
     */
    void setName(String name);

    /**
     * Return a string describing the access modifier flags in the specified
     * modifier. For example: <blockquote>
     * <p/>
     * <pre>
     *    public final synchronized strictfp
     * </pre>
     * <p/>
     * </blockquote> The modifier names are returned in an order consistent with
     * the suggested modifier orderings given in <a href=
     * "http://java.sun.com/docs/books/jls/second_edition/html/j.title.doc.html">
     * <em>The
     * Java Language Specification, Second Edition</em></a> sections <a href=
     * "http://java.sun.com/docs/books/jls/second_edition/html/classes.doc.html#21613"
     * >&sect;8.1.1</a>, <a href=
     * "http://java.sun.com/docs/books/jls/second_edition/html/classes.doc.html#78091"
     * >&sect;8.3.1</a>, <a href=
     * "http://java.sun.com/docs/books/jls/second_edition/html/classes.doc.html#78188"
     * >&sect;8.4.3</a>, <a href=
     * "http://java.sun.com/docs/books/jls/second_edition/html/classes.doc.html#42018"
     * >&sect;8.8.3</a>, and <a href=
     * "http://java.sun.com/docs/books/jls/second_edition/html/interfaces.doc.html#235947"
     * >&sect;9.1.1</a>. The full modifier ordering used by this method is:
     * <blockquote> <code>
     * public protected private abstract static final transient
     * volatile synchronized native strictfp
     * interface </code> </blockquote> The <code>interface</code> modifier
     * discussed in this class is not a true modifier in the Java language and it
     * appears after all other modifiers listed by this method. This method may
     * return a string of modifiers that are not valid modifiers of a Java
     * entity; in other words, no checking is done on the possible validity of
     * the combination of modifiers represented by the input.
     *
     * @return a string representation of the set of modifiers represented by
     *         <code>modifiers</code>
     */
    String modifierToString();


}