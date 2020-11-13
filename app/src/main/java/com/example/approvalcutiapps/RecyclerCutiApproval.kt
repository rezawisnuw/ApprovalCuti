package com.example.approvalcutiapps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.layout_cuti_approval.view.*
import kotlinx.android.synthetic.main.layout_rv_cuti_approval.view.*

//var getDataKaryawanCutiApproval:MutableList<String?> = ArrayList()
var getJenisCutiApproval:MutableList<String?> = ArrayList()
var getTanggalCutiApproval:MutableList<String?> = ArrayList()
var getKeteranganCutiApproval:MutableList<String?> = ArrayList()
var getIdCutiApproval:MutableList<String?> = ArrayList()
var getTipeCutiApproval:MutableList<String?> = ArrayList()
var getPengajuanCutiApproval:MutableList<String?> = ArrayList()

var ttlDataCutiApproval: Int = 0

var getCheckedCutiApproval:MutableList<String?> = ArrayList()
var setCheckedCutiApproval:Boolean = false
var getJsonCutiApproval:MutableList<String?> = ArrayList()

class RecyclerCutiApproval(val feed: CutiApproval.Feed): RecyclerView.Adapter<CustomViewHolder>(){

    override fun getItemCount(): Int {
        ttlDataCutiApproval = feed.data.count()
        return feed.data.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val row = layoutInflater.inflate(R.layout.layout_rv_cuti_approval, parent, false)
        return CustomViewHolder(row)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val res = feed.data.get(position)

        holder.view.tv_inpkaryawancuti.text = res.KARYAWAN
        holder.view.tv_inpposisi.text = res.POSISI
        holder.view.tv_inpdepartement.text = res.DEPARTEMENT
        holder.view.tv_inpsisasaldo.text = res.SISASALDOCUTI
        holder.view.tv_inpjeniscuti.text = res.JENISCUTI
        holder.view.tv_inptglcuti.text = res.TANGGALCUTI
        holder.view.tv_inpketerangancuti.text = res.KETERANGAN
        holder.view.tv_inppengganticuti.text = res.PENGGANTI

        holder.view.tv_inpidcuti.text = res.IDCUTI
        holder.view.tv_inpidcuti.setVisibility(View.GONE);
        holder.view.tv_idcuti.setVisibility(View.GONE);
        holder.view.tv_inptipecuti.text = res.TIPECUTI
        holder.view.tv_inptipecuti.setVisibility(View.GONE);
        holder.view.tv_tipecuti.setVisibility(View.GONE);
        holder.view.tv_inppengajuancuti.text = res.PENGAJUANCUTI
        holder.view.tv_inppengajuancuti.setVisibility(View.GONE);
        holder.view.tv_pengajuancuti.setVisibility(View.GONE);

        holder.view.ck_cutiapproval.setOnClickListener {
            var gson = GsonBuilder().create().toJson(res)
//            var gsonDataKaryawan = GsonBuilder().create().toJson(res.KARYAWAN)
            var gsonJenisCuti = GsonBuilder().create().toJson(res.JENISCUTI)
            var gsonTanggalCuti = GsonBuilder().create().toJson(res.TANGGALCUTI)
            var gsonKeteranganCuti = GsonBuilder().create().toJson(res.KETERANGAN)
            var gsonIdCuti = GsonBuilder().create().toJson(res.IDCUTI)
            var gsonTipeCuti = GsonBuilder().create().toJson(res.TIPECUTI)
            var gsonPengajuanCuti = GsonBuilder().create().toJson(res.PENGAJUANCUTI)


            if(holder.view.ck_cutiapproval.isChecked) {

                getCheckedCutiApproval.add(res.toString())
                getJsonCutiApproval.add(gson)

//                getDataKaryawanCutiApproval.add(gsonDataKaryawan)
                getJenisCutiApproval.add(gsonJenisCuti)
                getTanggalCutiApproval.add(gsonTanggalCuti)
                getKeteranganCutiApproval.add(gsonKeteranganCuti)
                getIdCutiApproval.add(gsonIdCuti)
                getTipeCutiApproval.add(gsonTipeCuti)
                getPengajuanCutiApproval.add(gsonPengajuanCuti)

            } else {

                if(getCheckedCutiApproval.contains(res.toString())){
                    getCheckedCutiApproval.remove(res.toString())
                    getJsonCutiApproval.remove(gson)

//                    getDataKaryawanCutiApproval.remove(gsonDataKaryawan)
                    getJenisCutiApproval.remove(gsonJenisCuti)
                    getTanggalCutiApproval.remove(gsonTanggalCuti)
                    getKeteranganCutiApproval.remove(gsonKeteranganCuti)
                    getIdCutiApproval.remove(gsonIdCuti)
                    getTipeCutiApproval.remove(gsonTipeCuti)
                    getPengajuanCutiApproval.add(gsonPengajuanCuti)

                }
            }
            println("GsonBuilder "+ GsonBuilder().create().toJson(res))
            println("getJsonCutiApproval" + getJsonCutiApproval)
            println("position "+position)
            setCheckedCutiApproval = holder.view.ck_cutiapproval.isChecked
        }

    }

}

class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view){

}