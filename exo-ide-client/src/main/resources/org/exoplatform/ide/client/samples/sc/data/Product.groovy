 package data

import org.chromattic.api.annotations.Property
import org.chromattic.api.annotations.PrimaryType
import org.chromattic.api.annotations.Mandatory
import org.chromattic.api.annotations.Name
import org.chromattic.api.annotations.Id

@PrimaryType(name = "exo:product")
class Product {
 @Id String id
 @Name String name
 @Property(name = "price") @Mandatory Integer price
 @Property(name = "description") String description
}    