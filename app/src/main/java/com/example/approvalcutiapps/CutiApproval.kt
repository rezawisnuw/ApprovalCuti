package com.example.approvalcutiapps

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.layout_cuti_approval.*
import kotlinx.android.synthetic.main.layout_rv_cuti_approval.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class classContainer_spinnerKaryawanCutiApproval{
    var globalContainer_spinnerKaryawanCutiApproval: String? = ""
}

object objContainer_spinnerKaryawanCutiApproval {
    private var globalObject: String? = ""

    fun getObjContainer_spinnerKaryawanCutiApproval(): String?{
        return globalObject
    }

    fun setObjContainer_spinnerKaryawanCutiApproval(funContainer_spinnerKaryawanCutiApproval : classContainer_spinnerKaryawanCutiApproval){
        globalObject = funContainer_spinnerKaryawanCutiApproval.globalContainer_spinnerKaryawanCutiApproval
    }
}


var KaryawanCutiParam : String? = ""

class CutiApproval : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_cuti_approval)
        setTitle("Approval Cuti")

        rv_cutiapproval.setHasFixedSize(true)
        rv_cutiapproval.layoutManager = LinearLayoutManager(this)

        pb_cutiapproval.visibility = View.VISIBLE
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );

        getListKaryawanCuti()

        btn_approve.setOnClickListener{
            if(ck_cutiapproval.isChecked){
                approveCuti()
            }else{
                Toast.makeText(this@CutiApproval, "Pilih karyawan terlebih dahulu", Toast.LENGTH_LONG).show()
            }

        }

        btn_reject.setOnClickListener {
            if(ck_cutiapproval.isChecked){
                rejectCuti()
            }else{
                Toast.makeText(this@CutiApproval, "Pilih karyawan terlebih dahulu", Toast.LENGTH_LONG).show()
            }
        }

    }

    fun getListKaryawanCuti(){
        val url = "https://hrindomaret.com/api/getKaryawanCutiApproval"
        val nik = intent.getStringExtra("nik")

        val param = JSONObject()
//        param.put("nik",  nik)
        param.put("nik",  "2013077872")

        val formbody = param.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val post = Request.Builder()
            .url(url)
            .post(formbody)
            .build()

        val client = OkHttpClient()

        client.newCall(post).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                println("bodykaryawancutiapproval" + body)

                runOnUiThread {
                    pb_cutiapproval.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

                val ret_spinnerKaryawanCutiApproval = classContainer_spinnerKaryawanCutiApproval()
                //listApprovalType = body

                ret_spinnerKaryawanCutiApproval.globalContainer_spinnerKaryawanCutiApproval = body
                objContainer_spinnerKaryawanCutiApproval.setObjContainer_spinnerKaryawanCutiApproval(ret_spinnerKaryawanCutiApproval)
                //setApprovalList(resApprovalType)

                setListKaryawanCuti()


            }

            override fun onFailure(call: Call, e: IOException) {
                println("Data Karyawan Cuti Tidak Tampil")
            }
        })

    }

    fun setListKaryawanCuti(){
        val setKaryawanCuti  = arrayListOf<String>()

        val setKaryawanCutiJSON = JSONObject(objContainer_spinnerKaryawanCutiApproval.getObjContainer_spinnerKaryawanCutiApproval())
        val jsonArray = JSONArray(setKaryawanCutiJSON.get("data").toString())
        for(i in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(i)

            val listKaryawanCutiApproval = jsonObject.getString("KARYAWAN")

            setKaryawanCuti.add(listKaryawanCutiApproval)

        }

        val spinnerKaryawanCutiApproval = findViewById<Spinner>(R.id.sp_karyawan)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, setKaryawanCuti)

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        println("setKaryawanCutiJSON"+setKaryawanCutiJSON)

        this.runOnUiThread {
            spinnerKaryawanCutiApproval.adapter = adapter
        }

        spinnerKaryawanCutiApproval?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("Tidak Ada Yang Terpilih")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedKaryawanCuti = setKaryawanCuti[position]

                KaryawanCutiParam = selectedKaryawanCuti

                pb_cutiapproval.visibility = View.VISIBLE
                getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );

                getListCuti()

            }
        }
    }

    fun getListCuti(){
        val url = "https://hrindomaret.com/api/getCutiApproval"
        val param = JSONObject()
        param.put("nik",  KaryawanCutiParam)
        println("KaryawanCutiParam"+KaryawanCutiParam)

        val formbody = param.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val post = Request.Builder()
            .url(url)
            .post(formbody)
            .build()

        val client = OkHttpClient()

        client.newCall(post).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                println("bodycutiapproval" + body)

                val gson = GsonBuilder().create()
                val listcuti = gson.fromJson(body, Feed::class.java)

                println("listcuti"+listcuti)

                runOnUiThread {
                    pb_cutiapproval.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    rv_cutiapproval.adapter = RecyclerCutiApproval(listcuti)
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                println("Hasil Error")
            }
        })
    }

    fun approveCuti(){
        pb_cutiapproval.visibility = View.VISIBLE
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );

        val url = "https://hrindomaret.com/api/submitCutiApproved"

//        val nik = getDataKaryawanCutiApproval
        val idcuti = getIdCutiApproval
        val status = "Approve"

        val param = JSONObject()

//        param.put("nik", nik)
        param.put("idcuti", idcuti)
        param.put("status", status)

        val formbody = param.toString().replace("\"[","[").replace("]\"","]").replace("\\","").toRequestBody()

        val postReq = Request.Builder()
            .url(url)
            .post(formbody)
            .build()

        val client = OkHttpClient()

        client.newCall(postReq).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val resp = response.body?.string()
                println("responseapprove "+ resp)
                if(resp!!.toString().contains("Success") && ck_cutiapproval.isChecked){
                    runOnUiThread {
                        pb_cutiapproval.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(this@CutiApproval, "Cuti Berhasil Di Approve", Toast.LENGTH_LONG).show()
                        finish()
                        startActivity(getIntent())

                    }
                } else {
                    runOnUiThread {
                        pb_cutiapproval.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(this@CutiApproval, "Cuti Gagal Di Approve", Toast.LENGTH_LONG).show()
                        finish()
                        startActivity(getIntent())
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Hasil Error")
            }
        })
    }

    fun rejectCuti(){
        pb_cutiapproval.visibility = View.VISIBLE
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );


        val url = "https://hrindomaret.com/api/submitCutiApproved"

//        val nik = getDataKaryawanCutiApproval
        val idcuti = getIdCutiApproval
        val status = "reject"

        val param = JSONObject()

//        param.put("nik", nik)
        param.put("idcuti", idcuti)
        param.put("status", status)

        val formbody = param.toString().replace("\"[","[").replace("]\"","]").replace("\\","").toRequestBody()
        val post2 = Request.Builder()
            .url(url)
            .post(formbody)
            .build()

        val client = OkHttpClient()

        client.newCall(post2).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val resp = response.body?.string()
                println("responsereject"+ resp)
                if(resp!!.toString()!!.contains("Sukses")){
                    runOnUiThread {
                        pb_cutiapproval.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(this@CutiApproval, "Cuti Berhasil Di Reject", Toast.LENGTH_LONG).show()
                        finish()
                        startActivity(getIntent())
                    }
                } else {
                    runOnUiThread {
                        pb_cutiapproval.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(this@CutiApproval, "Cuti Gagal Di Reject", Toast.LENGTH_LONG).show()
                        finish()
                        startActivity(getIntent())
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Hasil Error")
            }
        })
    }

    data class Feed(
        @SerializedName("data") val data: List<ModelListCuti>
    )

    data class ModelListCuti(
        @SerializedName("KARYAWAN") val KARYAWAN : String?,
        @SerializedName("JENISCUTI") val JENISCUTI : String?,
        @SerializedName("TANGGALCUTI") val TANGGALCUTI : String?,
        @SerializedName("PENGAJUANCUTI") val PENGAJUANCUTI : String?,
        @SerializedName("IDCUTI") val IDCUTI : String?,
        @SerializedName("STATUS") val STATUS : String?
    )
}