package com.eduardo.ticketbluetoothprint

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.StringBuilderPrinter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.eduardo.ticketbluetoothprint.databinding.ActivityMainBinding
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback

class MainActivity : AppCompatActivity(), PrintingCallback {

    override fun connectingWithPrinter() {
        Toast.makeText(this,"Conectandose a la impresora",Toast.LENGTH_SHORT).show()
    }

    override fun connectionFailed(error: String) {
        Toast.makeText(this,"Fallido $error",Toast.LENGTH_SHORT).show()
    }

    override fun disconnected() {
        Toast.makeText(this,"Impresora Desconectada",Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: String) {
        Toast.makeText(this,"Error $error",Toast.LENGTH_SHORT).show()
    }

    override fun onMessage(message: String) {
        Toast.makeText(this,"Mensaje $message",Toast.LENGTH_SHORT).show()
    }

    override fun printingOrderSentSuccessfully() {
        Toast.makeText(this,"Ticket Enviado a la impresora",Toast.LENGTH_SHORT).show()
    }

    private var printing: Printing? = null;
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Printooth.init(this)
        checaBluetooth()
        vincularPrinter()
        initView()
    }

    private fun checaBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT))
        }
        else{
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
        }
    }

    private var requestBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            //granted
            Toast.makeText(this, "Bluetooth is enable",Toast.LENGTH_SHORT).show()
        }else{
            //deny
            Toast.makeText(this, "Bluetooth is enable",Toast.LENGTH_SHORT).show()
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.d("test006", "${it.key} = ${it.value}")
            }
        }

    private fun vincularPrinter(){
        Printooth.setPrinter("MP210","DC:0D:51:58:BF:CB")
        if (Printooth.hasPairedPrinter()){
            Toast.makeText(this, "Printer is enable",Toast.LENGTH_SHORT).show()
            Log.e("Printer", " is enable")
        }else{
            Toast.makeText(this, "Printer is enable",Toast.LENGTH_SHORT).show()
            Log.e("printer", " is not enable")
        }
        changePairAndUnpair()
    }
    private fun initView() {

        if (printing != null){
            printing!!.printingCallback = this
        }

        binding.btnPiarUnpair.setOnClickListener{
            Printooth.setPrinter("MP210","DC:0D:51:58:BF:CB")
            if (Printooth.hasPairedPrinter()){
                Toast.makeText(this, "Printer is enable",Toast.LENGTH_SHORT).show()
                Log.e("Printer", " is enable")
            }else{
                Toast.makeText(this, "Printer is enable",Toast.LENGTH_SHORT).show()
                Log.e("printer", " is not enable")
            }
            changePairAndUnpair()
            /*if (Printooth.hasPairedPrinter())
                Printooth.removeCurrentPrinter()
            else{
                startActivityForResult(Intent(this@MainActivity,ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
                changePairAndUnpair()
            }*/
        }

        binding.btnPrintImages.setOnClickListener{
            if (!Printooth.hasPairedPrinter())
                startActivityForResult(Intent(this@MainActivity,ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
            else
                printImage()
        }

        binding.btnPrint.setOnClickListener{
            if (!Printooth.hasPairedPrinter())
                startActivityForResult(Intent(this@MainActivity,ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
            else
                printText()
        }
    }

    private fun printText() {
        var printables = ArrayList<Printable>()
        //printables.add(RawPrintable.Builder(byteArrayOf(27,100,4)).build())
        /*var printable = TextPrintable.Builder()
            .setText("Punto de Venta")
            .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
            .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
            .setNewLinesAfter(1)
            .build()
        printables.add(printable)

        var direccion = TextPrintable.Builder()
            .setText("Av Francisco I.Madero")
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
            .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
            .setNewLinesAfter(1)
            .build()
        printables.add(direccion)

        var telefono = TextPrintable.Builder()
            .setText("Cel. 6632273472")
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
            .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
            .setNewLinesAfter(1)
            .build()
        printables.add(telefono)*/

        val str = StringBuilder()
        str?.append("Hamburguesa")
        str?.append(rightSpace(17,"Hamburguesa",2,"$185.00"))
        str?.append(2)
        str?.append(rightSpace2(9,"1$85.00", 2))
        str?.append("$185.00")

        val str2 = StringBuilder()
        str2?.append("Hotdog")
        str2?.append(rightSpace(17,"Hotdog",10,"$25.00"))
        str2?.append(10)
        str2?.append(rightSpace2(9,"$25.00",10))
        str2?.append("$25.00")

        Log.e("Resultado", str.toString())
        Log.e("Resultado", str2.toString())

        var titulo = TextPrintable.Builder()
            .setText(
                "Productos        Cant      Total"
            )
            .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
            .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
            .setCharacterCode(DefaultPrinter.CHARCODE_PC852)
            .setNewLinesAfter(1)
            .build()
        printables.add(titulo)

        var productos = TextPrintable.Builder()
            .setText(
                "$str"
            )
            .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
            .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
            .setCharacterCode(DefaultPrinter.CHARCODE_PC852)
            .setNewLinesAfter(1)
            .build()
        printables.add(productos)

        var productos2 = TextPrintable.Builder()
            .setText(
                "$str2"
            )
            .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
            .setLineSpacing(DefaultPrinter.LINE_SPACING_30)
            .setCharacterCode(DefaultPrinter.CHARCODE_PC852)
            .setNewLinesAfter(1)
            .build()
        printables.add(productos2)

        Printooth.printer().print(printables)
    }

    private fun acomodoTotales(max: Int): StringBuilder{
        val strS = StringBuilder()
        val strS2 = StringBuilder()
        val strC = StringBuilder()



        return strC
    }

    private fun rightSpace(max: Int, name: String, cantidad: Int, precio: String):  StringBuilder{
        val strS = StringBuilder()
        val totalS = name.length+cantidad+precio.length
        for (i in 0 .. max - name.length ){
           strS.append(".")
        }
        return strS
    }

    private fun rightSpace2(max: Int, precio: String, cantidad: Int):  StringBuilder{
        var c: Int = 0
        c = if (cantidad.toString().length == 1)
            precio.length-3
        else
            precio.length-2

        val strS = StringBuilder()
        for (i in 0 .. max -c){
            strS.append(".")
        }
        return strS
    }

    private fun printImage() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK) {
            initPrinting()
        }
        changePairAndUnpair()
    }

    private fun changePairAndUnpair() {
        if (Printooth.hasPairedPrinter()){
            binding.btnPiarUnpair.text = "Desvincular ${Printooth.getPairedPrinter()?.name}"
        }
        else {
            binding.btnPiarUnpair.text = "Vincula con la impresora"
        }
    }

    private fun initPrinting() {
        if (Printooth.hasPairedPrinter()){
            printing = Printooth.printer()
        }
        if (printing != null){
            printing!!.printingCallback = this
        }
    }


}