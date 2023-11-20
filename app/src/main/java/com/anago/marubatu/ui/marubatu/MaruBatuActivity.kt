package com.anago.marubatu.ui.marubatu

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEachIndexed
import com.anago.marubatu.R
import com.anago.marubatu.models.FieldSymbol.CPU
import com.anago.marubatu.models.FieldSymbol.ME
import com.anago.marubatu.models.FieldSymbol.NONE
import com.anago.marubatu.ui.views.MaruBatuFieldView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class MaruBatuActivity : AppCompatActivity() {
    private val viewModel: MaruBatuViewModel by viewModels()
    private lateinit var tvText: MaterialTextView
    private lateinit var btnReset: MaterialButton
    private var viewFields: ArrayList<MaruBatuFieldView> = ArrayList(9)
    private var objectAnimators: ArrayList<ObjectAnimator> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marubatu)

        tvText = findViewById(R.id.tvText)
        btnReset = findViewById(R.id.btnReset)
        for (pos in 0 until 9) {
            val fieldView = findViewById<MaruBatuFieldView>(
                resources.getIdentifier("marubatu_field_$pos", "id", packageName)
            )
            viewFields.add(pos, fieldView)
        }

        btnReset.setOnClickListener {
            viewModel.resetGame()
        }

        viewModel.isFinished.observe(this) {
            btnReset.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        findViewById<ViewGroup>(R.id.marubatu_fields_container).forEachIndexed { index, view ->
            view.setOnClickListener { fieldView ->
                (fieldView as MaruBatuFieldView)
                viewModel.handleFieldClick(index)
            }
        }

        viewModel.turn.observe(this) { turn ->
            val turnText = when (turn) {
                ME -> "Your Turn!"
                CPU -> "CPU Turn!"
                else -> "unknown"
            }
            tvText.text = turnText
        }

        viewModel.connectedFieldPos.observe(this) {
            objectAnimators.forEach { animator ->
                animator.end()
            }
            objectAnimators.clear()
            if (it.isNotEmpty()) {
                it.forEach { pos ->
                    val objectAnimator =
                        ObjectAnimator.ofFloat(viewFields[pos].getImageView(), "alpha", 0.2f, 1f)
                    objectAnimator?.duration = 600
                    objectAnimator?.repeatMode = ObjectAnimator.REVERSE
                    objectAnimator?.repeatCount = ObjectAnimator.INFINITE
                    objectAnimator?.start()
                    objectAnimators.add(objectAnimator)
                }
            }
        }

        viewModel.winnerSymbol.observe(this) { symbol ->
            val resultText = when (symbol) {
                ME -> "You Won!"
                CPU -> "CPU Won!"
                NONE -> "Draw!"
                else -> null
            }
            resultText?.let {
                tvText.text = it
            }
        }

        viewModel.fields.observe(this) { symbols ->
            symbols.forEachIndexed { index, symbol ->
                viewFields[index].setSymbol(symbol)
            }
        }
    }
}