package avana.mvvm.product.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import avana.mvvm.product.data.OperationCallback
import avana.mvvm.product.model.Product
import avana.mvvm.product.model.ProductDataSource

class ProductViewModel(private val repository: ProductDataSource): ViewModel() {

    private val _products = MutableLiveData<List<Product>>().apply { value = emptyList() }
    val products: LiveData<List<Product>> = _products

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> = _isViewLoading

    private val _isRecyclerViewLoading = MutableLiveData<Boolean>()
    val isRecyclerViewLoading: LiveData<Boolean> = _isRecyclerViewLoading

    private val _onMessageError = MutableLiveData<Any>()
    val onMessageError: LiveData<Any> = _onMessageError

    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean> = _isEmptyList

    fun loadProducts() {
        _isViewLoading.postValue(true)
        repository.retrieveProducts(object: OperationCallback{
            override fun onError(obj: Any?) {
                _isViewLoading.postValue(false)
                _onMessageError.postValue(obj)
            }

            override fun onSuccess(obj: Any?) {
                _isViewLoading.postValue(false)
                if (obj != null && obj is List<*>){
                    if (obj.isEmpty()) _isEmptyList.postValue(true)
                    else _products.value = obj as List<Product>
                }
            }
        })
    }

    fun loadNextProducts() {
        _isRecyclerViewLoading.postValue(true)
        repository.retrieveProducts(object: OperationCallback{
            override fun onError(obj: Any?) {
                _isRecyclerViewLoading.postValue(false)
                _onMessageError.postValue(obj)
            }

            override fun onSuccess(obj: Any?) {
                _isRecyclerViewLoading.postValue(false)
                if (obj != null && obj is List<*>){
                    if (obj.isEmpty()) _isEmptyList.postValue(true)
                    else _products.value = obj as List<Product>
                }
            }
        })
    }

}