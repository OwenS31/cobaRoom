package com.example.cobaroom

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cobaroom.database.daftarBelanja
import com.example.cobaroom.database.daftarBelanjaDB
import com.example.cobaroom.database.historyBarang
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class adapterDaftar ( private val daftarBelanja: MutableList<daftarBelanja>, private var DB: daftarBelanjaDB): RecyclerView.Adapter<adapterDaftar.ListViewHolder>() {
    private lateinit var onItemClickCallback : OnItemClickCallback

    interface OnItemClickCallback {
        fun delData(dtBelanja: daftarBelanja)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        var _tvItemBarang = itemView.findViewById<TextView>(R.id.tvItemBarang)
        var _tvjumlahBarang = itemView.findViewById<TextView>(R.id.tvjumlahBarang)
        var _tvTanggal = itemView.findViewById<TextView>(R.id.tvTanggal)

        var _btnEdit = itemView.findViewById<ImageView>(R.id.btnEdit)
        var _btnDelete = itemView.findViewById<ImageView>(R.id.btnDelete)
        val _btnSelesai = itemView.findViewById<ImageView>(R.id.btnSelesai)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var daftar = daftarBelanja[position]

        holder._tvTanggal.setText(daftar.tanggal)
        holder._tvItemBarang.setText(daftar.item)
        holder._tvjumlahBarang.setText(daftar.jumlah)

        holder._btnEdit.setOnClickListener {
            val intent = Intent(it.context, TambahDaftar::class.java)
            intent.putExtra("id", daftar.id)
            intent.putExtra("addEdit", 1)
            it.context.startActivity(intent)
        }

        holder._btnDelete.setOnClickListener {
            onItemClickCallback.delData(daftar)
        }

        holder._btnSelesai.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                DB.fundaftarBelanjaDAO().delete(daftar)
                DB.funHistoryBarangDAO().insert(
                    historyBarang(
                        tanggal = daftar.tanggal,
                        item = daftar.item,
                        jumlah = daftar.jumlah)
                )
                val daftarBaru = DB.fundaftarBelanjaDAO().selectAll()
                withContext(Dispatchers.Main) {
                    isiData(daftarBaru)
                }
            }
            onItemClickCallback.delData(daftar)
        }

    }

    override fun getItemCount(): Int {
        return daftarBelanja.size
    }

    fun isiData(daftar: List<daftarBelanja>) {
        daftarBelanja.clear()
        daftarBelanja.addAll(daftar)
        notifyDataSetChanged()
    }


}