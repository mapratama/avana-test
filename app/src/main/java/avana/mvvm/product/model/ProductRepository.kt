package avana.mvvm.product.model

import avana.mvvm.product.data.ApiClient
import avana.mvvm.product.data.HomeResponse
import avana.mvvm.product.data.OperationCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProductRepository: ProductDataSource {

    private var call: Call<List<HomeResponse>>? = null

    override fun retrieveProducts(callback: OperationCallback) {
        call = ApiClient.build()?.home()
        call?.enqueue(object: Callback<List<HomeResponse>>{
            override fun onFailure(call: Call<List<HomeResponse>>, t: Throwable) {
                callback.onError(t.message)
            }

            override fun onResponse(call: Call<List<HomeResponse>>, response: Response<List<HomeResponse>>) {
                response.body()?.let {
                    if (response.isSuccessful) {
                        callback.onSuccess(it.first().data.productPromo)
                    } else {
                        callback.onError("Tidak bisa mengambil data dari server")
                    }
                }
            }
        })
    }

    override fun cancel() {
        call?.cancel()
    }
}