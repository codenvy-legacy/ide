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


import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
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
     *         matching first letter of type name if it set to null service MUST return all founded annotations
     * @return {@link List<ShortTypeInfo>}
     * @throws CodeAssistantException
     */
    @NotNull
    List<ShortTypeInfo> getAnnotations(@Nullable String prefix, @NotNull Set<String> dependencies) throws CodeAssistantException;

    /**
     * Find all classes by prefix.
     *
     * @param prefix
     *         matching first letter of type name if it set to null service MUST return all founded annotations
     * @return {@link List<ShortTypeInfo>}
     * @throws CodeAssistantException
     */
    @NotNull
    List<ShortTypeInfo> getClasses(@Nullable String prefix, @NotNull Set<String> dependencies) throws CodeAssistantException;

    /**
     * Find JavaDoc for FQN
     *
     * @param fqn
     *         of type
     * @return string Java doc
     * @throws CodeAssistantException
     *         if Java doc not found
     */
    @NotNull
    String getClassJavaDoc(@NotNull String fqn, @NotNull Set<String> dependencies) throws CodeAssistantException;

    /**
     * Find all interfaces by prefix.
     *
     * @param prefix
     *         matching first letter of type name if it set to null service MUST return all founded annotations
     * @return {@link List<ShortTypeInfo>}
     * @throws CodeAssistantException
     */
    @NotNull
    List<ShortTypeInfo> getInterfaces(@Nullable String prefix, @NotNull Set<String> dependencies) throws CodeAssistantException;

    /**
     * Find JavaDoc for Java Class member FQN
     *
     * @param fqn
     *         of type
     * @return string Java doc
     * @throws CodeAssistantException
     *         if Java doc not found
     */
    @NotNull
    String getMemberJavaDoc(@NotNull String fqn, @NotNull Set<String> dependencies) throws CodeAssistantException;

    /**
     * Returns the Class object associated with the class or interface with the given string Full Qualified Name.
     *
     * @param fqn
     *         the Full Qualified Name
     * @return {@link TypeInfo} or null if Class object not found.
     * @throws CodeAssistantException
     */
    @NotNull
    TypeInfo getTypeByFqn(@NotNull String fqn, @NotNull Set<String> dependencies) throws CodeAssistantException;

    /**
     * sets of {@link ShortTypeInfo} matched to prefix (means FQN begin on {fqnPrefix} ) Example : if prefix = "java.util.c" set must
     * content: {java.util.Comparator<T> java.util.Calendar java.util.Collection<E> java.util.Collections
     * java.util.ConcurrentModificationException java.util.Currency java.util.concurrent java.util.concurrent.atomic
     * java.util.concurrent.locks }
     *
     * @param fqnPrefix
     *         the string for matching FQNs
     * @return {@link List<ShortTypeInfo>}
     * @throws CodeAssistantException
     */
    @NotNull
    List<ShortTypeInfo> getTypesByFqnPrefix(@NotNull String fqnPrefix, @NotNull Set<String> dependencies) throws CodeAssistantException;

    /**
     * Return sets of {@link ShortTypeInfo} object associated with the class or interface matched to name. (means Class simple name begin
     * on {namePrefix})
     * Example: if name == "Node" result can content information about: - javax.xml.soap.Node - com.google.gwt.xml.client.Node -
     * org.w3c.dom.Node - org.w3c.dom.traversal.NodeFilter - org.w3c.dom.traversal.NodeIterator
     *
     * @param namePrefix
     *         the string for matching FQNs
     * @return {@link List<ShortTypeInfo>}
     * @throws CodeAssistantException
     */
    @NotNull
    List<ShortTypeInfo> getTypesByNamePrefix(@NotNull String namePrefix, @NotNull Set<String> dependencies) throws CodeAssistantException;

    /**
     * Return sets of {@link TypeInfo} object associated with the class or interface matched to name. (means Class simple name begin on
     * {namePrefix})
     * Example: if name == "Node" result can content information about: - javax.xml.soap.Node - com.google.gwt.xml.client.Node -
     * org.w3c.dom.Node - org.w3c.dom.traversal.NodeFilter - org.w3c.dom.traversal.NodeIterator
     *
     * @param namePrefix
     *         the string for matching FQNs
     * @return {@link List<ShortTypeInfo>}
     * @throws CodeAssistantException
     */
    @NotNull
    List<TypeInfo> getTypesInfoByNamePrefix(@NotNull String namePrefix, @NotNull Set<String> dependencies) throws CodeAssistantException;

    /**
     * Return sets of Strings, associated with the package names
     *
     * @param packagePrefix
     *         the string for matching package name
     * @return List of strings
     * @throws CodeAssistantException
     */
    @NotNull
    List<String> getPackages(@NotNull String packagePrefix, @NotNull Set<String> dependencies) throws CodeAssistantException;

    /**
     * Return sets of Strings, associated with the package names
     *
     * @param dependencies
     *         of the project
     * @return List of strings
     * @throws CodeAssistantException
     */
    @NotNull
    List<String> getAllPackages(@NotNull Set<String> dependencies) throws CodeAssistantException;
}
