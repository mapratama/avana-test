package avana.mvvm.product.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import avana.mvvm.product.R
import avana.mvvm.product.model.Product
import avana.mvvm.product.viewmodel.AuthViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.FilePickerConst.REQUEST_CODE_DOC
import droidninja.filepicker.FilePickerConst.REQUEST_CODE_PHOTO
import kotlinx.android.synthetic.main.activity_checkout.*
import java.io.File


class CheckoutActivity : AppCompatActivity() {

    private lateinit var product: Product
    private lateinit var viewModel: AuthViewModel
    private var callbackManager: CallbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        setupUI()
        setupViewModel()
        setupCustomerData()
    }

    private fun setupUI() {
        supportActionBar!!.title = "Checkout"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        product = intent.getSerializableExtra("product") as Product
        titleTextView.text = product.title
        priceTextView.text = product.price
        Glide.with(this).load(product.imageUrl).into(photo)
    }

    private fun setupCustomerData() {
        val user = FirebaseAuth.getInstance().currentUser
        nameEditText.setText(if (user == null) "" else user.displayName)
        emailEditText.setText(if (user == null) "" else user.email)
    }

    private fun setupViewModel() {
        viewModel = AuthViewModel(callbackManager)
        viewModel.taskAuth.observe(this, getFacebookName)
        viewModel.onMessageError.observe(this, onMessageErrorObserver)
    }

    private val getFacebookName = Observer<Task<AuthResult>> {
        it.addOnCompleteListener(this) { task ->
            if (task.isSuccessful)
                setupCustomerData()
            else
                showToast(resources.getString(R.string.fb_error))
        }
    }

    private val onMessageErrorObserver= Observer<Any> {
        showToast(it.toString())
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun buttonFacebookLoginOnClick(view: View) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            buttonFacebookLogin.setReadPermissions("email", "public_profile")
            viewModel.facebookAuthenticate()
        }
        else {
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()
            setupCustomerData()
        }
    }

    fun uploadButtonOnClick(view: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        else
            openFilePicker()
    }

    private fun openFilePicker() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pilih tipe file")
        builder.setItems(arrayOf("Image", "PDF File")) { _, which ->
            val filePickerBuilder = FilePickerBuilder.instance.setMaxCount(1)
                    .setActivityTheme(R.style.LibAppTheme)

            if (which == 0) filePickerBuilder.pickPhoto(this, REQUEST_CODE_PHOTO)
            else {
                val zipTypes = Array(1) {".pdf"}
                filePickerBuilder.addFileSupport("PDF", zipTypes)
                        .pickFile(this, REQUEST_CODE_DOC)
            }

        }
        builder.show()
    }

    fun openImage(path: String) {
        Glide.with(this)
                .load(File(path))
                .apply(RequestOptions.centerCropTransform()
                        .dontAnimate()
                        .placeholder(droidninja.filepicker.R.drawable.image_placeholder))
                .thumbnail(0.5f)
                .into(paymentPhoto)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_PHOTO -> if (resultCode == Activity.RESULT_OK && data != null) {
                val path = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)[0].toString()
                openImage(path)

                paymentUrlTextView.visibility = View.GONE
                paymentPhoto.visibility = View.VISIBLE
            }
            REQUEST_CODE_DOC -> if (resultCode == Activity.RESULT_OK && data != null) {
                val path = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)[0].toString()
                paymentUrlTextView.text = path

                paymentUrlTextView.visibility = View.VISIBLE
                paymentPhoto.visibility = View.GONE

            }
            else -> callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            openFilePicker()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home)
            finish()

        return super.onOptionsItemSelected(item)
    }
}
