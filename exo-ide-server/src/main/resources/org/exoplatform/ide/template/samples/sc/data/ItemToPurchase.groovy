 package data

import org.chromattic.api.annotations.Property
import org.chromattic.api.annotations.ManyToOne
import org.chromattic.api.RelationshipType
import org.chromattic.api.annotations.PrimaryType
import org.chromattic.api.annotations.MappedBy
import org.chromattic.api.annotations.Mandatory
import org.chromattic.api.annotations.Id
import org.chromattic.api.annotations.Name
import org.chromattic.api.annotations.DefaultValue

@PrimaryType(name = "exo:itemtopurchase")
class ItemToPurchase {
  @Id def String id
  @Name def String name
  @ManyToOne def ShoppingCart cart
  @ManyToOne(type = RelationshipType.REFERENCE) @Mandatory @MappedBy("product") def Product product
  @Property(name = "quantity") @Mandatory @DefaultValue("1") def int quantity
  
  public int getValue() {
    return quantity * product.price
  }  
}  