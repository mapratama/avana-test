package avana.mvvm.product.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import avana.mvvm.product.R
import avana.mvvm.product.data.AdapterCallback
import avana.mvvm.product.di.Injection
import avana.mvvm.product.model.Product
import avana.mvvm.product.viewmodel.ProductViewModel
import avana.mvvm.product.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_error.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ProductViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewModel()
        setupUI()
    }

    private fun setupUI(){
        adapter = ProductAdapter(adapterCallback)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener {
                view, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == view.getChildAt(0).measuredHeight - view.measuredHeight) {
                viewModel.loadNextProducts()
            }
        })
    }

    private fun setupViewModel(){
        viewModel = ViewModelProviders.of(this, ViewModelFactory(Injection.providerRepository())).get(ProductViewModel::class.java)

        viewModel.products.observe(this, renderProducts)
        viewModel.isViewLoading.observe(this, isViewLoadingObserver)
        viewModel.isRecyclerViewLoading.observe(this, isRecylerViewLoadingObserver)
        viewModel.onMessageError.observe(this, onMessageErrorObserver)
        viewModel.isEmptyList.observe(this, emptyListObserver)
        viewModel.loadProducts()
    }

    private val renderProducts = Observer<List<Product>> {
        layoutError.visibility= View.GONE
        layoutEmpty.visibility= View.GONE
        adapter.update(it)
    }

    private val isViewLoadingObserver = Observer<Boolean> {
        val visibility= if(it) View.VISIBLE else View.GONE
        progressBar.visibility = visibility
    }

    private val isRecylerViewLoadingObserver = Observer<Boolean> {
        val visibility= if(it) View.VISIBLE else View.GONE
        recyclerViewProgressBar.visibility = visibility
    }

    private val onMessageErrorObserver= Observer<Any> {
        layoutError.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE
        textViewError.text = it.toString()
    }

    private val emptyListObserver= Observer<Boolean> {
        layoutEmpty.visibility = View.VISIBLE
        layoutError.visibility = View.GONE
    }

    private val adapterCallback = object: AdapterCallback {

        override fun onClick(obj: Any?) {
            val bundle = Bundle()
            bundle.putSerializable("product", obj as Product)

            val intent = Intent(this@MainActivity, ProductDetailActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }
}
