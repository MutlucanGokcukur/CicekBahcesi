package com.example.cicekbahcesi.Animasyon

import android.app.Activity
import android.app.AlertDialog
import com.example.cicekbahcesi.R

class GirisAnimasyon(val mActivity:Activity) {
    private lateinit var dialog:AlertDialog
    fun YüklemeEkranıBaslangıc()
    {
        val inflater=mActivity.layoutInflater
        val dialogview=inflater.inflate(R.layout.yukleniyor_animasyon,null)
        val builder=AlertDialog.Builder(mActivity)
        builder.setView(dialogview)
        builder.setCancelable(false)
        dialog=builder.create()
        dialog.show()
    }
    fun Kapat()
    {
        dialog.dismiss()
    }
}