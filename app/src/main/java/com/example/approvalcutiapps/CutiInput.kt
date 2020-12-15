package com.example.approvalcutiapps

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.getDatesRange
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import kotlinx.android.synthetic.main.layout_cuti_input.*
import kotlinx.android.synthetic.main.layout_cuti_input.sp_jeniscuti
import kotlinx.android.synthetic.main.layout_izin_input_old.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class GlobalHelperCutiInput {
    companion object {
        var dataJenisCutiInput: String? = ""
        var dataPenggantiCutiInput: String? = ""
        var selectedJenisCutiInput = ""
        var selectedidJenisCutiInput = ""
        var selectedPenggantiCutiInput = ""
        var getTanggalHariIni = ""
        var getTanggalAkhir = ""
        var getTotalHari = 0
    }

}

class CutiInput : AppCompatActivity(), OnSelectDateListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_cuti_input)
        //supportActionBar?.title = "Input Cuti"
        setTitle("Input Cuti")

        pb_cutiinput.visibility = View.VISIBLE
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );

        tv_inptglawalcuti.text = "--/--/----"
        tv_inptglakhircuti.text = "--/--/----"
        et_tanggalcuti.text.clear()
        tv_inptglawalcuti.setFocusable(false)
        tv_inptglakhircuti.setFocusable(false)
        et_tanggalcuti.setFocusable(false)

        getJenisCuti()
        getPenggantiCuti()
        getAtasanCuti()

        btn_tanggalcuti!!.setOnClickListener {
            if(et_jmlhari.text.toString() == ""){
                Toast.makeText(this@CutiInput, "Silahkan isi jumlah hari terlebih dahulu", Toast.LENGTH_LONG).show()
            }else{
                if(et_jmlhari.text.toString().toInt() <= tv_inpsisa.text.toString().toInt()){
                    getTglCuti()
                }else if(et_jmlhari.text.toString().toInt() > tv_inpsisa.text.toString().toInt()){
                    Toast.makeText(this@CutiInput, "Jumlah hari melebihi batas sisa cuti", Toast.LENGTH_LONG).show()
                }
            }

        }

        if (GlobalHelperCutiInput.selectedidJenisCutiInput != "1" && GlobalHelperCutiInput.selectedidJenisCutiInput != "2") {
            tv_inptglawalcuti.visibility = View.VISIBLE
            tv_sampaidengan.visibility = View.VISIBLE
            tv_inptglakhircuti.visibility = View.VISIBLE
            et_tanggalcuti.visibility = View.GONE
        }else{
            tv_inptglawalcuti.visibility = View.GONE
            tv_sampaidengan.visibility = View.GONE
            tv_inptglakhircuti.visibility = View.GONE
            et_tanggalcuti.visibility = View.VISIBLE
        }

        btn_simpan!!.setOnClickListener {
            saveCuti()
        }

    }

    override fun onSelect(calendar: List<Calendar>) {
        val myFormat = "dd/MM/yyyy"
        var sdf = SimpleDateFormat(myFormat, Locale.US)
        println("www"+calendar)
        if (GlobalHelperCutiInput.selectedidJenisCutiInput != "1" && GlobalHelperCutiInput.selectedidJenisCutiInput != "2") {
            tv_inptglawalcuti.text =  sdf.format(calendar[0].time).toString()
            calendar[0].add(Calendar.DATE, if(et_jmlhari.text.toString() == "") GlobalHelperCutiInput.getTotalHari else et_jmlhari.text.toString().toInt())
            tv_inptglakhircuti.text = sdf.format(calendar[0].timeInMillis).toString()
        }else{
            var StringDate : String = ""

            for(i in calendar.indices){
                var formatted = sdf.format(calendar[i].time).toString()
                if(i == calendar.lastIndex){
                    StringDate += formatted
                } else {
                    StringDate += "$formatted,"
                }
            }
            et_tanggalcuti.setText(StringDate)

            var ArrayDate =
                et_tanggalcuti.text
                    .split(",")
                    .toTypedArray()
                    .take(if(et_jmlhari.text.toString() == "") GlobalHelperCutiInput.getTotalHari else et_jmlhari.text.toString().toInt())

            et_tanggalcuti.setText(
                ArrayDate.toString().substring(1, ArrayDate.toString().length - 1)
            )

            println("et_tanggalcuti"+et_tanggalcuti.text)

        }


    }

    fun getJenisCuti() {
        val url = "https://hrindomaret.com/api/getJenisCutiInput"
        val nik = intent.getStringExtra("nik")
        val param = JSONObject()
        //param.put("nik",  nik)
        param.put("nik",  "2007004013")

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

                if (GlobalHelperCutiInput.selectedidJenisCutiInput != "1" && GlobalHelperCutiInput.selectedidJenisCutiInput != "2") {
                    tv_inptglawalcuti.visibility = View.VISIBLE
                    tv_sampaidengan.visibility = View.VISIBLE
                    tv_inptglakhircuti.visibility = View.VISIBLE
                    et_tanggalcuti.visibility = View.GONE
                }else{
                    tv_inptglawalcuti.visibility = View.GONE
                    tv_sampaidengan.visibility = View.GONE
                    tv_inptglakhircuti.visibility = View.GONE
                    et_tanggalcuti.visibility = View.VISIBLE
                }
                tv_inptglawalcuti.text = "--/--/----"
                tv_inptglakhircuti.text = "--/--/----"
                et_tanggalcuti.text.clear()
                //println("selectedidJenisCutiInput"+GlobalHelperCutiInput.selectedidJenisCutiInput)
                getSaldoCuti()
            }
        }
    }

    fun getSaldoCuti(){
        val url = "https://hrindomaret.com/api/getSaldoCutiInput"
        val nik = intent.getStringExtra("nik")
        val param = JSONObject()
        //param.put("nik",  nik)
        param.put("nik",  "2007004013")
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
        param.put("nik",  "2007004013")

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
        param.put("nik",  "2007004013")

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

    fun getTglCuti(){

        if (GlobalHelperCutiInput.selectedidJenisCutiInput != "1" && GlobalHelperCutiInput.selectedidJenisCutiInput != "2"){

            val calendar = Calendar.getInstance()
            val myFormat = "dd/MM/yyyy"
            var sdf = SimpleDateFormat(myFormat, Locale.US)

            val multiDayBuilder = DatePickerBuilder(this, this)
                .date(calendar)
                .pickerType(CalendarView.ONE_DAY_PICKER)
                .headerColor(android.R.color.holo_green_dark)
                .selectionColor(android.R.color.holo_green_dark)
                .todayLabelColor(android.R.color.holo_blue_dark)
                .dialogButtonsColor(android.R.color.holo_green_dark)
                .navigationVisibility(View.VISIBLE);

            multiDayBuilder.build().show()

        } else {
            val calendar = Calendar.getInstance()
            val myFormat = "dd/MM/yyyy"
            var sdf = SimpleDateFormat(myFormat, Locale.US)
//            GlobalHelperCutiInput.getTanggalHariIni = sdf.format(calendar.time)
//            calendar.time = sdf.parse(GlobalHelperCutiInput.getTanggalHariIni)
//            calendar.add(Calendar.DATE, if(et_jmlhari.text.toString() == "") GlobalHelperCutiInput.getTotalHari else et_jmlhari.text.toString().toInt())
//            val resultdate = Date(calendar.timeInMillis)
//            GlobalHelperCutiInput.getTanggalAkhir = sdf.format(resultdate)
//            println("getTanggalHariIni"+GlobalHelperCutiInput.getTanggalHariIni)
//            println("getTanggalAkhir"+GlobalHelperCutiInput.getTanggalAkhir)
//            println("jmlhari"+if(et_jmlhari.text.toString() == "") GlobalHelperCutiInput.getTotalHari else et_jmlhari.text.toString().toInt())
//            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            val multiDayBuilder = DatePickerBuilder(this, this)
                .date(calendar)
                .pickerType(CalendarView.MANY_DAYS_PICKER)
                .headerColor(android.R.color.holo_green_dark)
                .selectionColor(android.R.color.holo_green_dark)
                .todayLabelColor(android.R.color.holo_blue_dark)
                .dialogButtonsColor(android.R.color.holo_green_dark)
                .navigationVisibility(View.VISIBLE);

            multiDayBuilder.build().show()

            //        val dateSetListener =
            //            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            //                calendar.set(Calendar.YEAR, year)
            //                calendar.set(Calendar.MONTH, monthOfYear)
            //                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            //            }
            //        DatePickerDialog(this@CutiInput,
            //            dateSetListener,
            //            calendar.get(Calendar.YEAR),
            //            calendar.get(Calendar.MONTH),
            //            calendar.get(Calendar.DAY_OF_MONTH)).show()
       }
    }

    fun saveCuti(){
        pb_cutiinput.visibility = View.VISIBLE
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );
        val url = "https://hrindomaret.com/api/submitCutiInput"
        val param = JSONObject()
        val nik = intent.getStringExtra("nik")
        //param.put("nik",  nik)
        param.put("nik",  "2007004013")
        param.put("jeniscuti",  GlobalHelperCutiInput.selectedJenisCutiInput)
        param.put("idcuti",  GlobalHelperCutiInput.selectedidJenisCutiInput)
        param.put("pengganticuti",  GlobalHelperCutiInput.selectedPenggantiCutiInput)
        param.put("atasancuti", tv_inpatasan.text)
        param.put("jmlharicuti", if(et_jmlhari.text.toString() == "") "0" else et_jmlhari.text.toString())
        param.put("keterangancuti",if(et_keterangan.text.toString() == "") "" else et_keterangan.text.toString())
        if (GlobalHelperCutiInput.selectedidJenisCutiInput != "1" && GlobalHelperCutiInput.selectedidJenisCutiInput != "2") {
            param.put("tglinputcuti", if(et_tanggalcuti.text.toString() == "") "" else et_tanggalcuti.text.split(",").toTypedArray())
        }else{
            param.put("tglawalcuti", tv_inptglawalcuti.text)
            param.put("tglakhircuti", tv_inptglakhircuti.text)
        }

        val formbody = param.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val post = Request.Builder()
            .url(url)
            .post(formbody)
            .build()

        val client = OkHttpClient()

        client.newCall(post).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Data Tidak Masuk")
                Toast.makeText(this@CutiInput, "Cuti Gagal Di Simpan", Toast.LENGTH_LONG).show()
                finish()
                startActivity(getIntent())
            }

            override fun onResponse(call: Call, response: Response) {
                val resp = response.body?.string()
                println("respnsimpan"+resp)
                runOnUiThread {
                    pb_cutiinput.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(this@CutiInput, "Cuti Berhasil Di Simpan", Toast.LENGTH_LONG).show()
                    finish()
                    startActivity(getIntent())
                }
            }

        })
    }
}