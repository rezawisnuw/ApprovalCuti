package com.example.approvalcutiapps

import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import kotlinx.android.synthetic.main.layout_cuti_approval.*
import kotlinx.android.synthetic.main.layout_cuti_input.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class GlobalHelperCutiInput {
    companion object {
        var dataJenisCutiInput: String? = ""
        var dataPenggantiCutiInput: String? = ""
        var selectedJenisCutiInput = ""
        var selectedidJenisCutiInput = ""
        var selectedPenggantiCutiInput = ""
    }

}

class CutiInput : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_cuti_input)
        //supportActionBar?.title = "Input Izin"
        setTitle("Input Cuti")

        pb_cutiinput.visibility = View.VISIBLE
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );

        getJenisCutiInput()
        getPenggantiCuti()
        getAtasanCuti()

    }

    fun getJenisCutiInput() {
        val url = "https://hrindomaret.com/api/getJenisCutiInput"
        val nik = intent.getStringExtra("nik")
        val param = JSONObject()
        //param.put("nik",  nik)
        param.put("nik",  "2015191455")

        val formbody = param.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val post = Request.Builder()
            .url(url)
            .post(formbody)
            .build()

        val client = OkHttpClient()

        client.newCall(post).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val respnObject = JSONObject(body)
                //respnObject.getJSONArray("data").getJSONObject(0)
                println("bodyjeniscuti"+respnObject)

                runOnUiThread {
                    pb_cutiinput.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    GlobalHelperCutiInput.dataJenisCutiInput = body

                    setJenisCutiInput()
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                println("Data Tidak Masuk")
            }

        })
    }

    fun setJenisCutiInput(){
        val listketeranganJenisCutiInput = arrayListOf<String>()
        val listidJenisCutiInput = arrayListOf<String>()

        val jsonJenisCutiInput = JSONObject(GlobalHelperCutiInput.dataJenisCutiInput)
        val arrayJenisCutiInput = JSONArray(jsonJenisCutiInput.get("data").toString())

        for (i in 0 until arrayJenisCutiInput.length()){
            val getjsonJenisCutiInput = arrayJenisCutiInput.getJSONObject(i)

            val keteranganJenisCutiInput = getjsonJenisCutiInput.getString("LEAVEDESCRIPTION")
            val idJenisCutiInput = getjsonJenisCutiInput.getString("LEAVEID")

            listketeranganJenisCutiInput.add(keteranganJenisCutiInput)
            listidJenisCutiInput.add(idJenisCutiInput)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listketeranganJenisCutiInput)

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        sp_jeniscuti?.adapter = adapter

        sp_jeniscuti?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                print("Not There")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                GlobalHelperCutiInput.selectedJenisCutiInput = listketeranganJenisCutiInput[position]
                GlobalHelperCutiInput.selectedidJenisCutiInput = listidJenisCutiInput[position]

                getSaldoCuti()
            }
        }
    }

    fun getSaldoCuti(){
        val url = "https://hrindomaret.com/api/getSaldoCutiInput"
        val nik = intent.getStringExtra("nik")
        val param = JSONObject()
        //param.put("nik",  nik)
        param.put("nik",  "2015191455")
        param.put("idcuti",  GlobalHelperCutiInput.selectedidJenisCutiInput )

        val formbody = param.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val post = Request.Builder()
            .url(url)
            .post(formbody)
            .build()

        val client = OkHttpClient()

        client.newCall(post).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Hasil Error")
            }

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                val respnObject = JSONObject(body)
                println("bodysaldocuti" + respnObject)

                val hakcuti = respnObject.getJSONArray("data").getJSONObject(0).getString("LEAVERIGHTS").toString()
                val digunakan = respnObject.getJSONArray("data").getJSONObject(0).getString("LEAVEOCCUPIED").toString()
                val sisa = respnObject.getJSONArray("data").getJSONObject(0).getString("LEAVEREMAINDER").toString()

                println("sisa"+sisa)

                runOnUiThread {
                    tv_inphakcuti.text = hakcuti
                    tv_inpdigunakan.text = digunakan
                    tv_inpsisa.text = sisa
                }

            }
        })
    }

    fun getPenggantiCuti(){
        val url = "https://hrindomaret.com/api/getPenggantiCutiInput"
        val nik = intent.getStringExtra("nik")
        val param = JSONObject()
        //param.put("nik",  nik)
        param.put("nik",  "2015191455")

        val formbody = param.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val post = Request.Builder()
            .url(url)
            .post(formbody)
            .build()

        val client = OkHttpClient()

        client.newCall(post).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val respnObject = JSONObject(body)
                //respnObject.getJSONArray("data").getJSONObject(0)
                println("bodypengganticuti"+respnObject)

                runOnUiThread {
                    pb_cutiinput.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    GlobalHelperCutiInput.dataPenggantiCutiInput = body

                    setPenggantiCutiInput()
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                println("Data Tidak Masuk")
            }

        })
    }

    fun setPenggantiCutiInput(){
        val listPenggantiCutiInput = arrayListOf<String>()

        val jsonPenggantiCutiInput = JSONObject(GlobalHelperCutiInput.dataPenggantiCutiInput)

        val arrayPenggantiCutiInput = JSONArray(jsonPenggantiCutiInput.get("data").toString())

        for (i in 0 until arrayPenggantiCutiInput.length()){
            val getjsonPenggantiCutiInput = arrayPenggantiCutiInput.getJSONObject(i)

            val PenggantiCutiInput = getjsonPenggantiCutiInput.getString("PENGGANTI")

            listPenggantiCutiInput.add(PenggantiCutiInput)

        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listPenggantiCutiInput)

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        sp_pengganticuti?.adapter = adapter

        sp_pengganticuti?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                print("Not There")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                GlobalHelperCutiInput.selectedPenggantiCutiInput = listPenggantiCutiInput[position]
            }
        }
    }

    fun getAtasanCuti(){
        val url = "https://hrindomaret.com/api/getAtasanCutiInput"
        val nik = intent.getStringExtra("nik")
        val param = JSONObject()
        //param.put("nik",  nik)
        param.put("nik",  "2015191455")

        val formbody = param.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val post = Request.Builder()
            .url(url)
            .post(formbody)
            .build()

        val client = OkHttpClient()

        client.newCall(post).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val respnObject = JSONObject(body)

                println("bodyatasancuti"+respnObject)

                val atasan = respnObject.getJSONArray("data").getJSONObject(0).getString("ATASAN").toString()

                runOnUiThread {
                    tv_inpatasan.text = atasan
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                println("Data Tidak Masuk")
            }

        })
    }

}