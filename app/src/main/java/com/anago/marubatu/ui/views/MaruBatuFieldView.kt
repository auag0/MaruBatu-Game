package com.anago.marubatu.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.anago.marubatu.R
import com.anago.marubatu.models.FieldSymbol

class MaruBatuFieldView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_marubatu_field, this)

        init()
    }

    private var symbol: FieldSymbol = FieldSymbol.NONE
    private lateinit var image: ImageView

    private fun init() {
        image = findViewById(R.id.marubatu_image)
    }

    fun getSymbol(): FieldSymbol {
        return this.symbol
    }

    fun setSymbol(symbol: FieldSymbol) {
        this.symbol = symbol

        val imageRes = when (symbol) {
            FieldSymbol.NONE -> 0
            FieldSymbol.ME -> R.drawable.ic_maru
            FieldSymbol.CPU -> R.drawable.ic_batu
        }
        image.setImageResource(imageRes)
    }

    fun resetSymbol() {
        setSymbol(FieldSymbol.NONE)
    }

    fun getImageView(): ImageView {
        return image
    }
}