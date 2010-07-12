package org.exoplatform.ideall.server;

import java.io.PrintWriter;

import org.exoplatform.ideall.client.module.IDEModuleInitializer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class G extends Generator {
   
   private SourceWriter sourceWriter;   

	@Override
	public String generate(TreeLogger logger, GeneratorContext context,
			String typeName) throws UnableToCompleteException {
	   
	   //return getStatic();
	   
	   return getGenerated(logger, context, typeName);
	}
	
	private String getStatic() {
    String className = "org.exoplatform.ideall.client.module.IDEMainModuleInitializer";
    
    System.out.println("----------------------------------");
    System.out.println("class name " + className);
      System.out.println("----------------------------------");
    
    
    return className;
	}
	
	private String getGenerated(TreeLogger logger, GeneratorContext context,
      String typeName) throws UnableToCompleteException {
	
	   try {
	      System.out.println("G.generate()");
	      
	      String generatedClassQualifiedName = createClass( logger, context, typeName );
	      
	      System.out.println("returning [" + generatedClassQualifiedName + "]");
	      
	      //System.out.println("sourceWriter ................ " + sourceWriter);
	      //sourceWriter.commit(logger);
	      
	      try {
	         Thread.sleep(2000);
	      } catch (Throwable e) {
	         e.printStackTrace();
	      }
	      
	      return generatedClassQualifiedName;    	      
	   } catch (Exception exc) {
	      System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA UNABLE TO COMPLETE!!!!!!!!!!!!!!");
	      exc.printStackTrace();
	      
	      return getStatic();	      
	   }
	}
	
   private String createClass( TreeLogger logger, GeneratorContext context, String typeName ) throws UnableToCompleteException {
      sourceWriter = getSourceWriter( logger, context, typeName );
      TypeOracle typeOracle = context.getTypeOracle();
      JClassType originalType = typeOracle.findType( typeName );
      
      //System.out.println("sourceWriter " + sourceWriter);
      
      if ( sourceWriter != null ) {
         //System.out.println("sw " + sourceWriter);
         
         writeClass( originalType, logger, context );
         sourceWriter.commit( logger );
         
      }
      
      System.out.println("create class " + originalType.getParameterizedQualifiedSourceName() + "Impl");
      return originalType.getParameterizedQualifiedSourceName() + "Impl";
   }	
   
   private SourceWriter getSourceWriter( TreeLogger logger, GeneratorContext context, String typeName ) throws UnableToCompleteException {
      logger.log( TreeLogger.INFO, "Generating source for " + typeName, null );

      System.out.println("type name > " + typeName);
      
      TypeOracle typeOracle = context.getTypeOracle();
      JClassType originalType = typeOracle.findType( typeName );
      if ( originalType == null ) {
         logger.log( TreeLogger.ERROR, "Unable to find metadata for type '" + typeName + "'", null );
         throw new UnableToCompleteException();
      }

      logger.log( TreeLogger.INFO, "Generating source for " + originalType.getQualifiedSourceName(), null );

      String packageName = originalType.getPackage().getName();
      String originalClassName = originalType.getSimpleSourceName();
      
      System.out.println("originalClassName " + originalClassName);
      
      String generatedClassName = originalClassName + "Impl";

      ClassSourceFileComposerFactory classFactory = new ClassSourceFileComposerFactory( packageName, generatedClassName );
      //classFactory.addImplementedInterface( originalType.getName() );
      classFactory.addImport( GWT.class.getName() );
      classFactory.addImport(IDEModuleInitializer.class.getName());

      PrintWriter printWriter = context.tryCreate( logger, packageName, generatedClassName );
      if ( printWriter == null ) {
         return null;
      }
      
      PWriter w = new PWriter(printWriter);
      
      return classFactory.createSourceWriter(context, w);
   }
   
   @SuppressWarnings( "unchecked" )
   private void writeClass( JClassType module, TreeLogger logger, GeneratorContext context ) throws UnableToCompleteException {
//      sourceWriter.println("public void initializeModules() {");
//         sourceWriter.indent();      
//         sourceWriter.println("Window.alert(\"Ya generated class!\");");
//         sourceWriter.outdent();
//      sourceWriter.println("}");
   }   

}
