package com.example.troidocrypto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.troidocrypto.presentation.CryptoCardAdapter
import com.example.troidocrypto.presentation.PresenterViewModel

class MainActivity : AppCompatActivity(), PresenterViewModel.View {

    private val presenter by lazy { ViewModelProvider(this)[PresenterViewModel::class.java] }
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter.setView(this)
        recycler = this.findViewById(R.id.main_recycler)
        recycler.adapter = CryptoCardAdapter(presenter.cryptoData)
        recycler.adapter?.notifyDataSetChanged()
        recycler.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL,
            false
        )
    }

    override fun notifyListUpdated() {
        recycler.adapter?.notifyDataSetChanged()
    }
}