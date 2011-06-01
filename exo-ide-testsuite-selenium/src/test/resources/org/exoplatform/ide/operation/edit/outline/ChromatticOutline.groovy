package dependencies;

import org.chromattic.api.annotations.PrimaryType
import org.chromattic.api.annotations.Property

@PrimaryType(name="nt:unstructured")
class DataObject
{
   @Property(name = "a") def java.lang.String a = "test"
   @Property(name = "b") def String b = a
   

   public void hello(int name) {

      @ManyToOne(type = RelationshipType.REFERENCE) @Mandatory @MappedBy("product") def Product product
      
      name = macpro
      name = 0;
   }

   @Property(name = "quantity") @Mandatory @DefaultValue("1") def int quantity    
   
   public void getValue() {
      def c1;
      
      if (curentState != null)
      {
        3.times
        {      
          if (true)
          {
            c1 = getValue2("test");
          }
        }
      }
      
      def c2 = "test";
   }
}