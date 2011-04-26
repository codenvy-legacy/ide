 package data

import org.chromattic.api.annotations.OneToMany
import org.chromattic.api.annotations.PrimaryType
import org.chromattic.api.annotations.Name
import org.chromattic.api.annotations.Id

@PrimaryType(name = "exo:shoppingcart")
class ShoppingCart {
  @Id def String id
  @Name def String name
  @OneToMany def Collection<ItemToPurchase> items
  
  int getTotalMoney(){
    def money = 0
    items.each { ItemToPurchase item -> money += item.value }
    return money
  }
}
