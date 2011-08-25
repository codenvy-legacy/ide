package dependencies;

import javax.ws.rs.Path
import javax.ws.rs.POST

public class TestJSON {
   public java.lang.A a1, a2, a3;
   java.lang.B b1, b2; String b3;

   public java.lang.String getValue1() {
      String c1;

      if (true)
      {
         c1 = getValue2("test");

         if (curentState != null)
         {
           Identity identity = curentState.getIdentity();
           3.times
           {
             println "Hello " + identity.getUserId();
           }
         }
      }

      String c2 = "test";
      return c3
   }
   
   private java.lang.String d

   public void setValue2(@Path("Inner/{pathParam}") String f1) {
      this.f2 = f1;
      def printClosureInner = {name1 -> println "Hello, ${name1}" }
      printClosure("Chris")
      printClosure("Joseph")
   }

   def printClosureOuter = {name2 -> println "Hello, ${name2}" }

   def hello(name3) {
     "Hello, ${name3}"
     String name4 = "test";
   }
   
   String g = "test";

   // test parsing parameterized types
   @POST
   @Path("products")
   @Produces("application/json")
   public Collection<HashMap<String,String>> get(@Path("Inner/{pathParam}") java.lang.List<? extends Tree> pathParam) {
      def col = []
      List<String> var1;
      var1 <=> col;
      col < var1;
      col >> var1;
      def products = getProducts();
      products.each { product ->
        def row = [:]
        row["price"] = "" + product.price;
        row["name"] = product.getName();
        row["description"] = product.getDescription();
        row["id"] = product.getId();
        List<String> var2;
        col << row;
      }
      return col;
   }

   @POST
   @Path("add-product")
   @Produces("application/json")
   public HashMap<String,String> add(HashMap<String,String> map) {
      addProduct(map)
      
      List<Tree> addVar1;  
      String[] addVar2;  // issue IDE-1045
      
      return map;
   }
}