package avana.mvvm.product.model

import avana.mvvm.product.data.OperationCallback

interface ProductDataSource {
    fun retrieveProducts(callback: OperationCallback)
    fun cancel()
}