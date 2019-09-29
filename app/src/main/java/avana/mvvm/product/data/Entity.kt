package avana.mvvm.product.data

import avana.mvvm.product.model.Product

data class HomeResponse(val data: ProductResponse)

data class ProductResponse(val productPromo: List<Product>?)