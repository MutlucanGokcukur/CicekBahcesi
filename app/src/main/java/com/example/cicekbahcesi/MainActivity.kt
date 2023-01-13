package com.example.cicekbahcesi

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.example.cicekbahcesi.Animasyon.GirisAnimasyon
import com.example.cicekbahcesi.databinding.YukleniyorAnimasyonBinding
import com.google.android.gms.common.util.concurrent.HandlerExecutor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_hesap_olusturma.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var emailadres: SharedPreferences
    var KayitliMail : String? = null
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title="Çiçek Bahçesi"
        auth= Firebase.auth
        db=Firebase.firestore
        //region Son girilen kullanıcı adını Kayıtta Tutma
        emailadres=this.getSharedPreferences("com.example.cicekbahcesi",Context.MODE_PRIVATE)
        KayitliMail=emailadres.getString("EMail","")
        if(KayitliMail!="")
        {
            txtEmailAdres.setText(KayitliMail)
        }
        //endregion
    }

    fun btnSifreUnuttu(view: View)
    {
        val intent=Intent(applicationContext,SifremiUnuttum::class.java)
        startActivity(intent)
    }

    fun btnGiris(view: View)
    {

        //Text boş değilse son girileni kaydeder.
        emailadres.edit().putString("EMail", txtEmailAdres.text.toString()).apply()

        val kayıtlımail=txtEmailAdres.text.toString()
        val sifre=txtSifre.text.toString()

        auth.signInWithEmailAndPassword(kayıtlımail,sifre).addOnSuccessListener {
            val yukleniyor=GirisAnimasyon(this)
            yukleniyor.YüklemeEkranıBaslangıc()
            val handler= Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    yukleniyor.Kapat()
                    intent = Intent(applicationContext, MusteriSayfa::class.java)
                    intent.putExtra("Sifre", txtSifre.text.toString())
                    startActivity(intent)
                }
            },5000)
        }.addOnFailureListener{
            if (txtEmailAdres.text.toString() == "admin" && txtSifre.text.toString() == "123") {
                val intent = Intent(applicationContext, AdminSayfa::class.java)
                intent.putExtra("Sifre",sifre)
                intent.putExtra("E-Mail Adresi",kayıtlımail)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(applicationContext, "Bir hata meydana geldi\nTekrar deneyiniz", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun btnHesapOlusturma(view: View)
    {
        val intent=Intent(applicationContext,HesapOlusturma::class.java)
        startActivity(intent)
    }

    fun btnInsta(view: View)
    {
        val insta = findViewById<View>(R.id.btnInstagram)
        insta.setOnClickListener(View.OnClickListener
        {
            val uri = Uri.parse("https://www.instagram.com/ikizlercicekbahcesi")
            val instagram= Intent(Intent.ACTION_VIEW,uri)
            instagram.setPackage("com.instragram.android")
            try
            {
                startActivity(instagram)
            }
            catch (e: ActivityNotFoundException)
            {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/ikizlercicekbahcesi/")))
            }
        })
    }

    fun btnKonum(view: View)
    {
        val konum = findViewById<View>(R.id.btnKonum)
        konum.setOnClickListener(View.OnClickListener
        {
            val uri = Uri.parse("https://www.google.com/maps/place/%C4%B0kizler+%C3%87i%C3%A7ek+Bah%C3%A7esi/@40.9705593,29.0763664,15z/data=!4m2!3m1!1s0x0:0x698c20a8f0259ca5?sa=X&ved=2ahUKEwiggvm7vdD6AhXFgf0HHYQ1BoAQ_BJ6BAhdEAU")
            val konum= Intent(Intent.ACTION_VIEW,uri)
            konum.setPackage("com.Google.android")
            try
            {
                startActivity(konum)
            }
            catch (e: ActivityNotFoundException)
            {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/place/%C4%B0kizler+%C3%87i%C3%A7ek+Bah%C3%A7esi/@40.9705593,29.0763664,15z/data=!4m2!3m1!1s0x0:0x698c20a8f0259ca5?sa=X&ved=2ahUKEwiggvm7vdD6AhXFgf0HHYQ1BoAQ_BJ6BAhdEAU")))
            }
        })
    }
}