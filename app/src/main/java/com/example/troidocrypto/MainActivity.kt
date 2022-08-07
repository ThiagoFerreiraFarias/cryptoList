package com.example.troidocrypto

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.troidocrypto.presentation.CryptoCardAdapter
import com.example.troidocrypto.presentation.PresenterViewModel

class MainActivity : AppCompatActivity(), PresenterViewModel.View {

    private val presenter by lazy { ViewModelProvider(this)[PresenterViewModel::class.java] }
    private lateinit var recycler: RecyclerView
    private lateinit var warningText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUI()
        presenter.initSetup(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.verifyRequestStatus()
    }

    private fun setupUI() {
        warningText = this.findViewById(R.id.tv_error_message)
        recycler = this.findViewById(R.id.main_recycler)

        recycler.adapter = CryptoCardAdapter(presenter.ratesUpdate)
        recycler.adapter?.notifyDataSetChanged()
        recycler.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL,
            false
        ).apply {
            reverseLayout = true
            stackFromEnd = true
        }
    }

    override fun notifyListUpdated() {
        if (recycler.visibility == View.GONE) {
            warningText.visibility = View.GONE
            recycler.visibility = View.VISIBLE
        }
        recycler.adapter?.notifyDataSetChanged()
    }

    override fun displayErrorMessage() {
        warningText.visibility = View.VISIBLE
        recycler.visibility = View.GONE
    }
}