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
package com.codenvy.ide.ext.java.server;



import com.codenvy.ide.ext.java.shared.ShortTypeInfo;
import com.codenvy.ide.ext.java.shared.TypeInfo;

import java.util.List;
import java.util.Set;

/**
 *
 */
public interface CodeAssistantStorage {
    /**
     * Find all annotations by prefix.
     *
     * @param prefix
     *         matching first letter of type name if it set to null service
     *         MUST return all founded annotations
     * @return {@link List<ShortTypeInfo>}
     * @throws CodeAssistantException
     */
    List<ShortTypeInfo> getAnnotations(String prefix, Set<String> dependencys) throws CodeAssistantException;

    /**
     * Find all classes by prefix.
     *
     * @param prefix
     *         matching first letter of type name if it set to null service
     *         MUST return all founded annotations
     * @return {@link List<ShortTypeInfo>}
     * @throws CodeAssistantException
     */
    List<ShortTypeInfo> getClasses(String prefix, Set<String> dependencys) throws CodeAssistantException;

    /**
     * Find JavaDoc for FQN
     *
     * @param fqn
     *         of type
     * @return string Java doc
     * @throws CodeAssistantException
     *         if Java doc not found
     */
    String getClassJavaDoc(String fqn, Set<String> dependencys) throws CodeAssistantException;

    /**
     * Find all interfaces by prefix.
     *
     * @param prefix
     *         matching first letter of type name if it set to null service
     *         MUST return all founded annotations
     * @return {@link List<ShortTypeInfo>}
     * @throws CodeAssistantException
     */
    List<ShortTypeInfo> getInterfaces(String prefix, Set<String> dependencys) throws CodeAssistantException;

    /**
     * Find JavaDoc for Java Class member FQN
     *
     * @param fqn
     *         of type
     * @return string Java doc
     * @throws CodeAssistantException
     *         if Java doc not found
     */
    String getMemberJavaDoc(String fqn, Set<String> dependencys) throws CodeAssistantException;

    /**
     * Returns the Class object associated with the class or interface with the
     * given string Full Qualified Name.
     *
     * @param fqn
     *         the Full Qualified Name
     * @return {@link TypeInfo} or null if Class object not found.
     * @throws CodeAssistantException
     */
    TypeInfo getTypeByFqn(String fqn, Set<String> dependencys) throws CodeAssistantException;

    /**
     * sets of {@link ShortTypeInfo} matched to prefix (means FQN begin on
     * {fqnPrefix} ) Example : if prefix = "java.util.c" set must content: {
     * java.util.Comparator<T> java.util.Calendar java.util.Collection<E>
     * java.util.Collections java.util.ConcurrentModificationException
     * java.util.Currency java.util.concurrent java.util.concurrent.atomic
     * java.util.concurrent.locks }
     *
     * @param fqnPrefix
     *         the string for matching FQNs
     * @return {@link List<ShortTypeInfo>}
     * @throws CodeAssistantException
     */
    List<ShortTypeInfo> getTypesByFqnPrefix(String fqnPrefix, Set<String> dependencys) throws CodeAssistantException;

    /**
     * Return sets of {@link ShortTypeInfo} object associated with the class or
     * interface matched to name. (means Class simple name begin on {namePrefix})
     * Example: if name == "Node" result can content information about: -
     * javax.xml.soap.Node - com.google.gwt.xml.client.Node - org.w3c.dom.Node -
     * org.w3c.dom.traversal.NodeFilter - org.w3c.dom.traversal.NodeIterator
     *
     * @param namePrefix
     *         the string for matching FQNs
     * @return {@link List<ShortTypeInfo>}
     * @throws CodeAssistantException
     */
    List<ShortTypeInfo> getTypesByNamePrefix(String namePrefix, Set<String> dependencys) throws CodeAssistantException;

    /**
     * Return sets of {@link TypeInfo} object associated with the class or
     * interface matched to name. (means Class simple name begin on {namePrefix})
     * Example: if name == "Node" result can content information about: -
     * javax.xml.soap.Node - com.google.gwt.xml.client.Node - org.w3c.dom.Node -
     * org.w3c.dom.traversal.NodeFilter - org.w3c.dom.traversal.NodeIterator
     *
     * @param namePrefix
     *         the string for matching FQNs
     * @return {@link List<ShortTypeInfo>}
     * @throws CodeAssistantException
     */
    List<TypeInfo> getTypesInfoByNamePrefix(String namePrefix, Set<String> dependencys) throws CodeAssistantException;

    /**
     * Return sets of Strings, associated with the package names
     *
     * @param packagePrefix
     *         the string for matching package name
     * @return List of strings
     * @throws CodeAssistantException
     */
    List<String> getPackages(String packagePrefix, Set<String> dependencys) throws CodeAssistantException;

    /**
     * Return sets of Strings, associated with the package names
     *
     * @param dependencys
     *         of the project
     * @return List of strings
     * @throws CodeAssistantException
     */
    List<String> getAllPackages(Set<String> dependencys) throws CodeAssistantException;
}
