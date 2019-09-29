package avana.mvvm.product.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import avana.mvvm.product.R

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import avana.mvvm.product.model.Product
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_product_detail.*

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        setupUI()
    }

    fun setupUI() {
        supportActionBar!!.title = "Detail Produk"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        product = intent.getSerializableExtra("product") as Product
        titleTextView.text = product.title
        descriptionTextView.text = product.description
        priceTextView.text = product.price
        Glide.with(this).load(product.imageUrl).into(photo)
    }

    fun buyButtonOnClick(view: View) {
        val bundle = Bundle()
        bundle.putSerializable("product", product)

        val intent = Intent(this, CheckoutActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home)
            finish()

        return super.onOptionsItemSelected(item)
    }
}
