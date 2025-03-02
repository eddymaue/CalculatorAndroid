package com.example.calcbygrok3v2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.calcbygrok3v2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Log.d(TAG, "Démarrage de l'application")

            try {
                binding = ActivityMainBinding.inflate(layoutInflater)
                Log.d(TAG, "Binding initialisé")
            } catch (e: Exception) {
                Log.e(TAG, "Erreur dans l'inflation du layout: ${e.message}", e)
                Toast.makeText(this, "Erreur layout: ${e.message}", Toast.LENGTH_LONG).show()
                throw e
            }

            try {
                setContentView(binding.root)
                Log.d(TAG, "setContentView réussi")
            } catch (e: Exception) {
                Log.e(TAG, "Erreur dans setContentView: ${e.message}", e)
                Toast.makeText(this, "Erreur view: ${e.message}", Toast.LENGTH_LONG).show()
                throw e
            }

            try {
                // Observer l'affichage
                viewModel.display.observe(this) { display ->
                    try {
                        binding.display.text = display
                    } catch (e: Exception) {
                        Log.e(TAG, "Erreur dans la mise à jour de l'affichage: ${e.message}", e)
                    }
                }
                Log.d(TAG, "Observer configuré")
            } catch (e: Exception) {
                Log.e(TAG, "Erreur dans la configuration de l'observer: ${e.message}", e)
                Toast.makeText(this, "Erreur observer: ${e.message}", Toast.LENGTH_LONG).show()
                throw e
            }

            try {
                // Associer les clics des boutons
                setupButtonListeners()
                Log.d(TAG, "Listeners de boutons configurés")
            } catch (e: Exception) {
                Log.e(TAG, "Erreur dans la configuration des listeners: ${e.message}", e)
                Toast.makeText(this, "Erreur boutons: ${e.message}", Toast.LENGTH_LONG).show()
                throw e
            }

        } catch (e: Exception) {
            Log.e(TAG, "Erreur critique lors de l'initialisation: ${e.message}", e)
            Toast.makeText(this, "Erreur critique: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupButtonListeners() {
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