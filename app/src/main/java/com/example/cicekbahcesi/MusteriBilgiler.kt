package com.example.cicekbahcesi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_musteri_bilgiler.*

class MusteriBilgiler : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var Ilce:String?=null
    private lateinit var GirenKisiMail:String
    private lateinit var GirenKisiSifre:String
    private lateinit var GirenKisiAdı:String
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_musteri_bilgiler)
        db = Firebase.firestore
        auth = Firebase.auth
        GirenKisiSifre=intent.getStringExtra("GirenSifre").toString()
        GirenKisiMail=intent.getStringExtra("E-Mail Adresi").toString()
        BilgileriAl()
        btnMusteriBilgiGuncelle.visibility=View.INVISIBLE
        //region İlçeler Ekleme
        var İlceler=resources.getStringArray(R.array.İlceler)
        if (PlnMusteriBilgilerIlce!=null)
        {
            val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,İlceler)
            PlnMusteriBilgilerIlce.adapter=adapter
        }
        PlnMusteriBilgilerIlce.onItemSelectedListener=object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected
                        (p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Ilce=İlceler.get(p2)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(applicationContext,"Lütfen bir ilçe seçiniz",Toast.LENGTH_LONG).show()
            }
        }
        //endregion

        //region PlainText yazı yazma kapalı
        PlnTextMusteriBilgilerAdı.isEnabled=false
        PlnTextMusteriBilgilerEmailAdres.isEnabled=false
        PlnTextMusteriBilgilerTelefonNo.isEnabled=false
        PlnTextMusteriBilgilerSifre.isEnabled=false
        PlnMusteriBilgilerAdres.isEnabled=false
        PlnMusteriBilgilerIlce.isEnabled=false
        TextMusteriBilgilerIlce.isEnabled=false

        PlnMusteriBilgilerIlce.visibility=View.INVISIBLE
        //endregion

    }

    private fun BilgileriAl()
    {
        var emailadres=auth.currentUser!!.email
        db.collection("Müşteriler").whereEqualTo("E-Mail Adresi",emailadres).get().addOnSuccessListener { Bilgiler->
            for (Bilgi in Bilgiler)
            {
                GirenKisiAdı=Bilgi.get("Ad Soyad")as String
                PlnTextMusteriBilgilerAdı.setText(Bilgi.get("Ad Soyad")as String)
                PlnTextMusteriBilgilerEmailAdres.setText(Bilgi.get("E-Mail Adresi")as String)
                PlnTextMusteriBilgilerTelefonNo.setText(Bilgi.get("Telefon Numarası")as String)
                PlnTextMusteriBilgilerSifre.setText(GirenKisiSifre)
                PlnMusteriBilgilerAdres.setText(Bilgi.get("Adres")as String)
                TextMusteriBilgilerIlce.setText(Bilgi.get("İlçe")as String)
            }
        }
    }

    fun GuncellemeAc(view: View)
    {
        PlnTextMusteriBilgilerEmailAdres.isEnabled=true
        PlnTextMusteriBilgilerSifre.isEnabled=true
        PlnMusteriBilgilerAdres.isEnabled=true
        PlnMusteriBilgilerIlce.isEnabled=true
        PlnTextMusteriBilgilerTelefonNo.isEnabled=true
        TextMusteriBilgilerIlce.isEnabled=true
        PlnMusteriBilgilerIlce.visibility=View.VISIBLE
        TextMusteriBilgilerIlce.visibility=View.INVISIBLE
        btnGuncellemeAc.visibility=View.INVISIBLE
        btnMusteriBilgiGuncelle.visibility=View.VISIBLE

    }

    fun BilgileriGüncele(view: View)
    {
        if (PlnTextMusteriBilgilerAdı.text!=null&&PlnTextMusteriBilgilerEmailAdres!=null&&
            PlnTextMusteriBilgilerTelefonNo.text!=null&&Ilce!=null&&PlnMusteriBilgilerAdres.text!=null&&
            PlnTextMusteriBilgilerSifre.text!=null)
        {
            val adres=PlnMusteriBilgilerAdres.text
            val telefon=PlnTextMusteriBilgilerTelefonNo.text
            val email=PlnTextMusteriBilgilerEmailAdres.text
            val sifre=PlnTextMusteriBilgilerSifre.text
            val ilce=Ilce

            val YeniBilgiler = hashMapOf<String, Any>()
            YeniBilgiler.put("İlçe", ilce.toString())
            YeniBilgiler.put("E-Mail Adresi", email.toString())
            YeniBilgiler.put("Adres", adres.toString())
            YeniBilgiler.put("Telefon Numarası", telefon.toString())
            YeniBilgiler.put("Şifre", sifre.toString())

            db.collection("Müşteriler").document(GirenKisiAdı).update(YeniBilgiler).addOnSuccessListener {
                Toast.makeText(applicationContext,"Güncellendi",Toast.LENGTH_LONG).show()
                val user=FirebaseAuth.getInstance().currentUser
                val emailadresi=PlnTextMusteriBilgilerEmailAdres.text.toString()
                user!!.updateEmail(emailadresi).addOnSuccessListener {
                    val yenisifre=PlnTextMusteriBilgilerSifre.text.toString()
                    user!!.updatePassword(yenisifre)
                    val intent=Intent(applicationContext,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener{
                Toast.makeText(applicationContext,"Bir hata meydana geldi",Toast.LENGTH_LONG).show()
            }
        }
        else
        {
            Toast.makeText(applicationContext,"Lütfen bilgileri eksiksiz giriniz.",Toast.LENGTH_LONG).show()
        }

    }

    fun KayıtSil(view: View)
    {
        db.collection("Müşteriler").document(GirenKisiAdı).delete()
        val kullanıcı=auth.currentUser
        kullanıcı!!.delete().addOnSuccessListener {
            val intent=Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(applicationContext,"Hesabınız Silindi!!",Toast.LENGTH_LONG).show()
        }
    }
}
