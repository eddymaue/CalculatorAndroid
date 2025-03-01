package com.example.calcbygrok3v2

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.calcbygrok3v2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observer l'affichage
        viewModel.display.observe(this) { display ->
            binding.display.text = display
        }

        // Associer les clics des boutons
        with(binding) {
            btn0.setOnClickListener { viewModel.onButtonClick("0") }
            btn1.setOnClickListener { viewModel.onButtonClick("1") }
            btn2.setOnClickListener { viewModel.onButtonClick("2") }
            btn3.setOnClickListener { viewModel.onButtonClick("3") }
            btn4.setOnClickListener { viewModel.onButtonClick("4") }
            btn5.setOnClickListener { viewModel.onButtonClick("5") }
            btn6.setOnClickListener { viewModel.onButtonClick("6") }
            btn7.setOnClickListener { viewModel.onButtonClick("7") }
            btn8.setOnClickListener { viewModel.onButtonClick("8") }
            btn9.setOnClickListener { viewModel.onButtonClick("9") }
            btnDot.setOnClickListener { viewModel.onButtonClick(".") }
            btnAdd.setOnClickListener { viewModel.onButtonClick("+") }
            btnSub.setOnClickListener { viewModel.onButtonClick("-") }
            btnMul.setOnClickListener { viewModel.onButtonClick("×") }
            btnDiv.setOnClickListener { viewModel.onButtonClick("/") }
            btnParOpen.setOnClickListener { viewModel.onButtonClick("(") }
            btnParClose.setOnClickListener { viewModel.onButtonClick(")") }
            btnEquals.setOnClickListener { viewModel.onButtonClick("=") }
            btnCa.setOnClickListener { viewModel.onButtonClick("CA") }
            btnC.setOnClickListener { viewModel.onButtonClick("C") }
            btnBack.setOnClickListener { viewModel.onButtonClick("<--") }
            btnPercent.setOnClickListener { viewModel.onButtonClick("%") }
            btnInv.setOnClickListener { viewModel.onButtonClick("1/x") }
        }
    }
}