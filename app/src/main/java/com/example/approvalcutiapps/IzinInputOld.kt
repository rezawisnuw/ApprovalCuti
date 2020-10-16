package com.example.approvalcutiapps

import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import kotlinx.android.synthetic.main.layout_izin_input_old.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class IzinInputOld : AppCompatActivity(), OnSelectDateListener {
    var cal = Calendar.getInstance()
    var selected: Any? = null
    var oneDay: Any? = null
    var timeIn: Any? = 1
    var timeOut: Any? = 1
    var NikAtasan: Any? = ""
    var IDIzin: Any? = null
    var NamaBawahan: Any? = ""

    override fun onSelect(calendar: List<Calendar>) {
        var StringDate : String? = ""
//        var x = SimpleDateFormat("dd-MM-yyyy")
        println(calendar[0].timeInMillis)
        val myFormat = "dd/MM/yyyy"
        for(i in calendar.indices){
            var sdf = SimpleDateFormat(myFormat, Locale.US)
            var formatted = sdf.format(calendar[i].time).toString()
            if(i == calendar.lastIndex){
                StringDate += formatted
            } else {
                StringDate += "$formatted,"
            }
        }
        println(StringDate)
        textview_date.text = StringDate
        if(textview_date.text != "--/--/----"){
            //button_date.isEnabled = false
            if(selected == "Dinas Luar Kantor Pagi" || selected == "Dinas Luar Kantor Siang" || selected == "Dinas Luar Kantor" || selected == "Ijin Pulang Karena Sakit" || selected=="Ijin Datang Terlambat" || selected=="Ijin Pulang"){
                textJamIn.isEnabled = true
            } else {
                editTextKetarangan.isEnabled = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_cuti_input)
        //supportActionBar?.title = "Input Izin"
        setTitle("Input Cuti")
        val nik = intent.getStringExtra("nik")

        textview_date.text = "--/--/----"
        editTextKetarangan.hint = "Input Keterangan"

        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage("Input Izin Sukses")
            .setCancelable(false)
            .setPositiveButton("OK", DialogInterface.OnClickListener {
                    dialog, id -> finish()
            })

        val alert = dialogBuilder.create()

        val dialogBuildergagal = AlertDialog.Builder(this)

        dialogBuildergagal.setMessage("Input Izin Tidak Berhasil, silahkan dicoba kembali setelah beberapa saat")
            .setCancelable(false)
            .setPositiveButton("OK", DialogInterface.OnClickListener {
                    dialog, id -> finish()
            })

        val alertgagal = dialogBuildergagal.create()

        //getRiwayatIzin(nik)
        //getAtasanIzin(nik)

        btnPickDate()

        textJamIn.isEnabled = false
        textJamOut.isEnabled = false
        editTextKetarangan.isEnabled = false
        buttonSubmit.isEnabled = false

        textJamIn.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                val myFormat = "HH:mm" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val rightNow = sdf.format(cal.time)

                textJamIn.text = rightNow
                if(textJamIn.text !== "Input Jam In"){
                    textJamOut.isEnabled = true
                    timeIn = textJamIn.text
                }
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        textJamOut.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                val myFormat = "HH:mm" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val rightNow = sdf.format(cal.time)

                textJamOut.text = rightNow
                if(selected=="Ijin Datang Terlambat" || selected=="Ijin Pulang"){
                    var jamIn = SimpleDateFormat("HH:mm",Locale.US).parse(textJamIn.text.toString())
                    println(jamIn)
                    var jamOut = SimpleDateFormat("HH:mm",Locale.US).parse(textJamOut.text.toString())
//                    println(TimeUnit.MILLISECONDS.toMinutes(jamOut.time -  jamIn.time))
                    if(TimeUnit.MILLISECONDS.toMinutes(jamOut.time -  jamIn.time) > 240){
//                        textJamOut.text = SimpleDateFormat("HH:mm",Locale.US).parse(jamIn.time+TimeUnit.MINUTES.toHours(240)).toString()
                        var time = jamIn.time
                        time += (4*60*60*1000)
                        var date = Date(time)
                        var test = SimpleDateFormat("HH:mm",Locale.US).format(date)
                        textJamOut.text = test
                        textJamIn.isEnabled = false
                        timeOut = textJamOut.text
                        editTextKetarangan.isEnabled = true
                    } else {
                        textJamIn.isEnabled = false
                        timeOut = textJamOut.text
                        editTextKetarangan.isEnabled = true
                    }
                } else {
                    textJamOut.text = rightNow
                    textJamIn.isEnabled = false
                    timeOut = textJamOut.text
                    editTextKetarangan.isEnabled = true
                }
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        editTextKetarangan.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buttonSubmit.isEnabled = editTextKetarangan.text.toString().trim().length !== 0
            }
        })

        buttonSubmit.setOnClickListener {

            runOnUiThread {
                progressBar.visibility = View.VISIBLE
                buttonSubmit.isEnabled = false
            }

            val url = "https://hrindomaret.com/api/submitIzinKotlin"

            val nik = nik


            val param = JSONObject()
            param.put("EMPLOYEEID", nik)
            param.put("APPROVEDBY", NikAtasan)
            param.put("ID", IDIzin)
            param.put("FROMD", textview_date.text)
            param.put("TIMEIN", timeIn)
            param.put("TIMEOUT", timeOut)
            param.put("KETERANGAN", editTextKetarangan.text)
            param.put("EMPLOYEENAME", NamaBawahan)


            val formbody = param.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val post = Request.Builder()
                .url(url)
                .post(formbody)
                .build()

            val client = OkHttpClient()

            client.newCall(post).enqueue(object: Callback {
                override fun onResponse(call: Call, response: Response) {
                    val resp = response.body?.string()
                    println("SUKSES")
                    println(resp)
                    if(resp!!.contains("sendingEmailResult")){
                        runOnUiThread {
//                            alert.show()
                            Toast.makeText(this@IzinInputOld, "Cuti Berhasil Di Input", Toast.LENGTH_LONG).show()
                            finish()
                            startActivity(getIntent())
//                            val dialog = BottomSheetDialog(this@CutiInput)
//                            val view = layoutInflater.inflate(R.layout.dialog_layout, null)
//
//                            val close = view.findViewById<Button>(R.id.textButton)
//                            close.setOnClickListener {
//                                dialog.dismiss()
//                                val intent = Intent(this@CutiInput, MainActivity::class.java)
//                                intent.putExtra("nik",nik)
//                                startActivity(intent)
//                                finish()
//                            }
//                            dialog.setCancelable(false)
//                            dialog.setContentView(view)
//                            dialog.show()
                        }
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    println("GAGAL")
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        buttonSubmit.isEnabled = true
                        alertgagal.show()
                    }
                }
            })
        }
    }

    fun getRiwayatIzin(nik:String){
        val url = "https://hrindomaret.com/api/getTipeIzinKotlin"

        val nik = nik

        val param = JSONObject()
        param.put("nik", nik)

        val formbody = param.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val post = Request.Builder()
            .url(url)
            .post(formbody)
            .build()

        val client = OkHttpClient()

        client.newCall(post).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val resp = response.body?.string()
                println("SUKSES")
                println(resp)
                runOnUiThread {
                    SpinnerIzin(resp)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("GAGAL")
            }
        })
    }

    fun SpinnerIzin(list:String?){
        val spinnerIzin = findViewById<Spinner>(R.id.sp_jeniscuti)

        val listJenisIzinArray = arrayListOf<String?>()

        val oneDayOnlyArray = arrayListOf<String?>()

        val jenisIzinArray = arrayListOf<String?>()

        val jsonobject = JSONObject(list)
        println("LIST " + list)
        var listIzin = jsonobject.getJSONArray("data")

        for(i in 0 until listIzin.length()){
            val jsonobjectlistizin = listIzin.getJSONObject(i)
            val listJenisIzin = jsonobjectlistizin.getString("DESCRIPTION")
            var oneDayOnly = jsonobjectlistizin.getString("ONEDAYONLY")
            var IDIzinList = jsonobjectlistizin.getString("ABSENCEID")
            listJenisIzinArray.add(listJenisIzin)
            oneDayOnlyArray.add(oneDayOnly)
            jenisIzinArray.add(IDIzinList)
        }

        val adapterIzin = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listJenisIzinArray
        )

        adapterIzin.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        runOnUiThread {
            spinnerIzin.adapter = adapterIzin
        }

        spinnerIzin?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("NOTHING")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selecteditem = listJenisIzinArray[position].toString()
                selected = selecteditem
                println("SELECTED " + selected)
                oneDay = oneDayOnlyArray[position]
                IDIzin = jenisIzinArray[position]
                println(oneDayOnlyArray[position])
                if(selected=="Ijin Datang Terlambat" || selected=="Ijin Pulang"){
                    textJamIn.visibility = View.VISIBLE
                    textJamOut.visibility = View.VISIBLE
                    textView3.visibility = View.VISIBLE
                } else if(selected == "Dinas Luar Kantor Pagi" || selected == "Dinas Luar Kantor Siang" || selected == "Dinas Luar Kantor" || selected == "Ijin Pulang Karena Sakit"){
                    textJamIn.visibility = View.VISIBLE
                    textJamOut.visibility = View.VISIBLE
                    textView3.visibility = View.VISIBLE
                } else {
                    textJamIn.visibility = View.GONE
                    textJamOut.visibility = View.GONE
                    textView3.visibility = View.GONE
                }
//                btnPickDate()
            }
        }
    }

    interface OnSelectDateListener {
        fun onSelect(calendar: List<Calendar>)
    }

    fun btnPickDate(){
        println("ONEDAY " + oneDay)
//        if(oneDay == "0"){
        val oneDayBuilder = DatePickerBuilder(this, this)
            .pickerType(CalendarView.MANY_DAYS_PICKER)
            .headerColor(android.R.color.holo_green_dark)
            .selectionColor(android.R.color.holo_green_dark)
            .todayLabelColor(android.R.color.holo_green_dark)
            .dialogButtonsColor(android.R.color.holo_green_dark)
            .navigationVisibility(View.VISIBLE);

        val oneDayPicker = oneDayBuilder.build()
        button_date!!.setOnClickListener {
            sp_jeniscuti.isEnabled = false
            oneDayPicker.show()
        }
//        } else {
//            val dateSetListener =
//                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                    cal.set(Calendar.YEAR, year)
//                    cal.set(Calendar.MONTH, monthOfYear)
//                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//                    updateDateInView()
//                }
//
//            button_date!!.setOnClickListener {
//                spinnerJenisIzin.isEnabled = false
//                DatePickerDialog(this@CutiInput,
//                    dateSetListener,
//                    // set DatePickerDialog to point to today's date when it loads up
//                    cal.get(Calendar.YEAR),
//                    cal.get(Calendar.MONTH),
//                    cal.get(Calendar.DAY_OF_MONTH)).show()
//            }
//        }
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textview_date!!.text = sdf.format(cal.time)
        println("CAL TIME" + cal.time)
        if(textview_date.text !== "--/--/----"){
//            button_date.isEnabled = true
            //button_date.isEnabled = false
            if(selected == "Dinas Luar Kantor Pagi" || selected == "Dinas Luar Kantor Siang" || selected == "Dinas Luar Kantor" || selected == "Ijin Pulang Karena Sakit" || selected=="Ijin Datang Terlambat" || selected=="Ijin Pulang"){
                textJamIn.isEnabled = true
            } else {
                editTextKetarangan.isEnabled = true
            }

        }
    }

    fun getAtasanIzin(nik:String){
        val url = "https://hrindomaret.com/api/getAtasanKotlin"

        val nik = nik

        val param = JSONObject()
        param.put("nik", nik)

        val formbody = param.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val post = Request.Builder()
            .url(url)
            .post(formbody)
            .build()

        val client = OkHttpClient()

        client.newCall(post).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                var resp = response.body?.string()
                println("SUKSES")
                println(resp)
                var Atasan = JSONArray(resp)
                var NamaAtasan:String? = ""
                for(i in 0 until Atasan.length()){
                    val listAtasan = Atasan.getJSONObject(i)
                    NamaAtasan = listAtasan.getString("NAMA_ATASAN")
                    NikAtasan = listAtasan.getString("NIK_ATASAN")
                    NamaBawahan = listAtasan.getString("NAMA_BAWAHAN")
                }
                runOnUiThread {
                    textAtasan.text = "NAMA ATASAN : $NamaAtasan"
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("GAGAL")
            }
        })
    }
}