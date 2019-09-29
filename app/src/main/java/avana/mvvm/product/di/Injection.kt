package avana.mvvm.product.di

import avana.mvvm.product.model.ProductDataSource
import avana.mvvm.product.model.ProductRepository

object Injection {

    fun providerRepository(): ProductDataSource{
        return ProductRepository()
    }
}