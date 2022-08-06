package com.example.troidocrypto.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.example.troidocrypto.R
import com.example.troidocrypto.domain.data.CoinBaseUpdate
import com.example.troidocrypto.domain.data.CoinPrice

class CryptoCardAdapter(private val dataSet: ObservableField<MutableList<CoinBaseUpdate>>)
    : RecyclerView.Adapter<CryptoCardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.crypto_data_card, viewGroup, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cryptoName: TextView = view.findViewById(R.id.tv_crypto_name)
        val updateTime: TextView = view.findViewById(R.id.tv_update_time)
        val rateEur: TextView = view.findViewById(R.id.tv_eur_rate)
        val rateUsd: TextView = view.findViewById(R.id.tv_usd_rate)
        val rateGbp: TextView = view.findViewById(R.id.tv_gbp_rate)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        dataSet.get()?.get(position).let { cryptoData ->
            with(viewHolder) {
                cryptoName.text = "${cryptoData?.chartName} - #${position+1} Update"
                updateTime.text = cryptoData?.time?.updated
                rateEur.text = buildRate(cryptoData?.bpi?.EUR)
                rateUsd.text = buildRate(cryptoData?.bpi?.USD)
                rateGbp.text = buildRate(cryptoData?.bpi?.GBP)
            }
        }
    }

    private fun buildRate(currency: CoinPrice?): String? {
        val symbol = HtmlCompat.fromHtml(currency?.symbol?:"", HtmlCompat.FROM_HTML_MODE_LEGACY)
        return "$symbol ${currency?.rate_float?:0F}"
    }

    override fun getItemCount() = dataSet.get()?.size?:0
}
