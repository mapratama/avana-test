package avana.mvvm.product.model

import java.io.Serializable

data class Product(
        val id: String,
        val title: String,
        val description: String,
        val price: String,
        val imageUrl: String
): Serializable