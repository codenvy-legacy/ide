package data

import org.chromattic.api.annotations.PrimaryType
import org.chromattic.api.annotations.OneToMany
import java.util.Map

@PrimaryType(name="exo:shop")
class Shop {
  @OneToMany def Map<String, Product> products
}