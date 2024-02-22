package com.best.zoom.dou.wb.fourthqrcode.base

import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity<VM : ViewModel, DB : ViewDataBinding> : AppCompatActivity() {

    protected lateinit var viewModel: VM
    protected lateinit var binding: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, getLayoutRes())
        viewModel = ViewModelProvider(this).get(getViewModelClass())
        binding.lifecycleOwner = this
        initViews()
    }

    @LayoutRes
    abstract fun getLayoutRes(): Int

    abstract fun getViewModelClass(): Class<VM>

    abstract fun initViews()
    fun navigateTo(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
    }

    fun navigateToWithParams(destination: Class<*>, bundle: Bundle) {
        val intent = Intent(this, destination)
        intent.putExtras(bundle)
        startActivity(intent)
    }

}
