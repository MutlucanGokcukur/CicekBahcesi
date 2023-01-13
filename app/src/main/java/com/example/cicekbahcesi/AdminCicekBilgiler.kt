package com.example.cicekbahcesi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_admin_cicek_bilgiler.*
import kotlinx.android.synthetic.main.activity_musteri_cicek_bilgiler.*

class AdminCicekBilgiler : AppCompatActivity() {
    private lateinit var secilencicek:String
    private lateinit var db:FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_cicek_bilgiler)
        title="Çiçek Bilgileri"
        db=Firebase.firestore
        secilencicek=intent.getStringExtra("secilencicek").toString()
        db.collection("Çiçekler").whereEqualTo("Çiçek Adı",secilencicek).get().addOnSuccessListener { Bilgiler->
            for (Bilgi in Bilgiler)
            {
                PlnAdminCicekBilgilerCicekAdı.setText(secilencicek)
                PlnAdminCicekBilgilerCicekFıyat.setText(Bilgi.get("Fiyat")as String)
                val mekan=Bilgi.get("Mekan")as String
                if (mekan=="Dış Mekan")
                {
                    ChcAdminCicekBilgilerDışMekan.isChecked=true
                }
                else if(mekan=="İç Mekan")
                {
                    ChcCicekBilgilerCicekIcMekan.isChecked=true
                }
                else if(mekan=="Fark Etmez")
                {
                    ChcCicekBilgilerCicekIcMekan.isChecked=true
                    ChcAdminCicekBilgilerDışMekan.isChecked=true
                }
                else
                {

                }
            }

        }
    }

    fun BilgileriGuncelle(view: View)
    {
        val YeniBilgiler = hashMapOf<String, Any>()
        YeniBilgiler.put("Fiyat",PlnAdminCicekBilgilerCicekFıyat.text.toString())
        YeniBilgiler.put("Çiçek Adı",PlnAdminCicekBilgilerCicekAdı.text.toString())


        if (ChcAdminCicekBilgilerDışMekan.isChecked==true)
        {
            YeniBilgiler.put("Mekan","Dış Mekan")
            if (ChcAdminCicekBilgilerIcMekan.isChecked==true)
            {
                YeniBilgiler.put("Mekan","Fark Etmez")
            }
            else
            {
                YeniBilgiler.put("Mekan","Dış Mekan")
            }
        }
        else if(ChcAdminCicekBilgilerIcMekan.isChecked==true)
        {
            if(ChcAdminCicekBilgilerDışMekan.isChecked==true)
            {
                YeniBilgiler.put("Mekan","Fark Etmez")
            }
            else
            {
                YeniBilgiler.put("Mekan","İç Mekan")
            }
        }
        else
        {
            YeniBilgiler.put("Mekan","Belirtilmemiş")
        }
        db.collection("Çiçekler").document(secilencicek).update(YeniBilgiler).addOnSuccessListener {
            Toast.makeText(applicationContext,"Güncellendi",Toast.LENGTH_LONG).show()
        }
    }
}