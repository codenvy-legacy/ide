/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.exoplatform.ide.extension.chromattic.server;

import org.chromattic.api.annotations.MixinType;
import org.chromattic.api.annotations.PrimaryType;
import org.chromattic.dataobject.CompilationSource;
import org.chromattic.dataobject.DataObjectException;
import org.chromattic.dataobject.NodeTypeFormat;
import org.chromattic.metamodel.typegen.CNDNodeTypeSerializer;
import org.chromattic.metamodel.typegen.NodeType;
import org.chromattic.metamodel.typegen.NodeTypeSerializer;
import org.chromattic.metamodel.typegen.SchemaBuilder;
import org.chromattic.metamodel.typegen.XMLNodeTypeSerializer;
import org.everrest.groovy.SourceFolder;
import org.exoplatform.ide.groovy.JcrGroovyCompiler;
import org.exoplatform.services.jcr.ext.resource.UnifiedNodeReference;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.TypeResolver;
import org.reflext.core.TypeResolverImpl;
import org.reflext.jlr.JavaLangReflectReflectionModel;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * The data object compiler.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class DataObjectCompiler {

  /** . */
  private final JcrGroovyCompiler compiler;

  /** . */
  private final CompilationSource source;

  /** . */
  private final String[] doPaths;

  /** . */
  private Class[] classes;

  /**
   * Create a new do compiler with the provided JCR compiler.
   *
   * @param compiler the compiler to use
   * @param source the compilation source
   * @param doPaths the data object paths
   * @throws DataObjectException anything that would prevent the compilation of data object
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if any data object path is null
   */
  public DataObjectCompiler(
    JcrGroovyCompiler compiler,
    CompilationSource source,
    String... doPaths) throws NullPointerException, IllegalArgumentException, DataObjectException {
    if (compiler == null) {
      throw new NullPointerException();
    }
    if (source == null) {
      throw new NullPointerException("No null source accepted");
    }
    doPaths = doPaths.clone();
    for (String doPath : doPaths) {
      if (doPath == null) {
        throw new IllegalArgumentException("Data object paths must not contain a null value");
      }
    }

    //
    this.compiler = compiler;
    this.source = source;
    this.doPaths = doPaths;
    this.classes = null;
  }

  /**
   * Create a new do compiler.
   *
   * @param source the compilation source
   * @param doPaths the data object paths
   * @throws DataObjectException anything that would prevent the compilation of data object
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if any data object path is null
   */
  public DataObjectCompiler(CompilationSource source, String... doPaths) throws DataObjectException {
    this(new JcrGroovyCompiler(), source, doPaths);
  }

  /**
   * Generates the node types for the specified data object paths. This operation returns the schema source
   * in the specified format.
   *
   * @param format the schema output format
   * @return the data object paths
   * @throws org.chromattic.dataobject.DataObjectException anything that would prevent data object compilation
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if any data object path is null
   */
  public String generateSchema(NodeTypeFormat format) throws DataObjectException, NullPointerException, IllegalArgumentException {

    Map<String,  NodeType> doNodeTypes = generateSchema();

    //
    NodeTypeSerializer serializer;
    switch (format) {
      case EXO:
        serializer = new XMLNodeTypeSerializer();
        break;
      case CND:
        serializer = new CNDNodeTypeSerializer();
        break;
      default:
        throw new AssertionError();
    }

    //
    for (NodeType nodeType : doNodeTypes.values()) {
      serializer.addNodeType(nodeType);
    }

    //
    try {
      StringWriter writer = new StringWriter();
      serializer.writeTo(writer);
      return writer.toString();
    }
    catch (Exception e) {
      throw new DataObjectException("Unexpected io exception", e);
    }
  }

  /**
   * Generates the node types for the specified data object paths. This operations returns a map
   * with the data object path as keys and the related node type as values.
   *
   * @return the data object paths
   * @throws org.chromattic.dataobject.DataObjectException anything that would prevent data object compilation
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if any data object path is null
   */
  public Map<String, NodeType> generateSchema() throws DataObjectException, NullPointerException, IllegalArgumentException {

    // Generate classes
    Map<String, Class<?>> classes = generateClasses();

    // Generate class types
    TypeResolver<Type> domain = TypeResolverImpl.create(JavaLangReflectReflectionModel.getInstance());
    Map<ClassTypeInfo, String> doClassTypes = new HashMap<ClassTypeInfo, String>();
    for (Map.Entry<String, Class<?>> entry : classes.entrySet()) {
      doClassTypes.put((ClassTypeInfo)domain.resolve(entry.getValue()), entry.getKey());
    }

    // Generate bean mappings
    Map<String, NodeType> doNodeTypes = new HashMap<String, NodeType>();
    for (Map.Entry<ClassTypeInfo,  NodeType> entry : new SchemaBuilder().build(doClassTypes.keySet()).entrySet()) {
      ClassTypeInfo doClassType = entry.getKey();
      NodeType doNodeType = entry.getValue();
      String doPath = doClassTypes.get(doClassType);
      doNodeTypes.put(doPath, doNodeType);
    }

    //
    return doNodeTypes;
  }

  /**
   * Compiles the specified classes and returns a map with a data object path as key and
   * the corresponding compiled data object class.
   *
   * @return the compiled data object classes
   * @throws org.chromattic.dataobject.DataObjectException anything that would prevent data object compilation
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if any data object path is null
   */
  public Map<String, Class<?>> generateClasses() throws DataObjectException, NullPointerException, IllegalArgumentException {

    //
    Class<?>[] classes = generateAllClasses();

    //
    int i = 0;
    Map<String, Class<?>> doClasses = new HashMap<String, Class<?>>();
    for (Class<?> clazz : classes) {
      if (clazz.isAnnotationPresent(PrimaryType.class) || clazz.isAnnotationPresent(MixinType.class)) {
        doClasses.put(doPaths[i++], clazz);
      }
    }

    //
    return doClasses;
  }

  /**
   * Compiles the specified classes and returns an array containing all the classes generated during
   * the compilation. Note that the number of returned class can be greater than the number of provided
   * paths (classes can be generated for specific groovy needs, such as closure).
   *
   * @return the compiled data object classes
   * @throws org.chromattic.dataobject.DataObjectException anything that would prevent data object compilation
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if any data object path is null
   */
  public Class[] generateAllClasses() throws DataObjectException, NullPointerException, IllegalArgumentException {

    //
    if (classes == null) {
      // Build the classloader url
      try {

        //
        UnifiedNodeReference[] doRefs = new UnifiedNodeReference[doPaths.length];
        for  (int i = 0;i < doPaths.length;i++) {
          doRefs[i] = new UnifiedNodeReference(source.getRepositoryRef(), source.getWorkspaceRef(), doPaths[i]);
        }

        // see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4648098
        URL url = new UnifiedNodeReference(source.getRepositoryRef(), source.getWorkspaceRef(), source.getPath()).getURL();
        // Compile to classes and return
        return compiler.compile(new SourceFolder[]{new SourceFolder(url)}, doRefs);
      }
      catch (IOException e) {
        throw new DataObjectException("Could not generate data object classes", e);
      }
    }

    //
    return classes;
  }
}
