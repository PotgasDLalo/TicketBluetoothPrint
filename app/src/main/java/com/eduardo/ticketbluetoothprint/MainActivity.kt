package com.eduardo.ticketbluetoothprint

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

    private var printing: Printing? = null
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        if (printing != null){
            printing!!.printingCallback = this
        }

        binding.btnPiarUnpair.setOnClickListener{
            if (Printooth.hasPairedPrinter())
                Printooth.removeCurrentPrinter()
            else{
                startActivityForResult(Intent(this@MainActivity,ScanningActivity::class.java),
                    ScanningActivity.SCANNING_FOR_PRINTER)
                changePairAndUnpair()
            }
        }

        binding.btnPrintImages.setOnClickListener{
            if (!Printooth.hasPairedPrinter())
                startActivityForResult(Intent(this@MainActivity,ScanningActivity::class.java),
                ScanningActivity.SCANNING_FOR_PRINTER)
            else
                printImage()
        }

        binding.btnPrint.setOnClickListener{
            if (!Printooth.hasPairedPrinter())
                startActivityForResult(Intent(this@MainActivity,ScanningActivity::class.java),
                    ScanningActivity.SCANNING_FOR_PRINTER)
            else
                printText()
        }
    }

    private fun printText() {
        val printables = ArrayList<Printable>()
        printables.add(RawPrintable.Builder(byteArrayOf(27,100,4)).build())

        // Agregar texto
        printables.add(TextPrintable.Builder()
            .setText("Hello World")
            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
            .setNewLinesAfter(1)
            .build())

        // texto personalizado
        printables.add(TextPrintable.Builder()
            .setText("Hola mundo")
            .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
            .setNewLinesAfter(1)
            .build())

        printing!!.print(printables)
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