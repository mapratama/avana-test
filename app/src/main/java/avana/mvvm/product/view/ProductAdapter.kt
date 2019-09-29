package avana.mvvm.product.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import avana.mvvm.product.R
import avana.mvvm.product.data.AdapterCallback
import avana.mvvm.product.model.Product
import com.bumptech.glide.Glide

class ProductAdapter(var adapterCallback: AdapterCallback): RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private var products: ArrayList<Product> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val product= products[position]

        viewHolder.product = product
        viewHolder.textViewName.text= product.title
        Glide.with(viewHolder.imageView.context).load(product.imageUrl).into(viewHolder.imageView)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun update(data: List<Product>) {
        products.addAll(data)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        lateinit var product: Product
        val textViewName: TextView = view.findViewById(R.id.textViewName)
        val imageView: ImageView = view.findViewById(R.id.imageView)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            adapterCallback.onClick(product)
        }
    }
}