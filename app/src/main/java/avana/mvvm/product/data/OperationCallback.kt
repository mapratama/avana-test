package avana.mvvm.product.data

interface OperationCallback {
    fun onSuccess(obj:Any?)
    fun onError(obj:Any?)
}

interface AdapterCallback {
    fun onClick(obj:Any?)
}