package com.example.cicekbahcesi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_admin_siparis_bilgiler.*
import kotlinx.android.synthetic.main.activity_admin_siparis_bilgiler.view.*

class AdminSiparisBilgiler : AppCompatActivity() {
    private lateinit var db:FirebaseFirestore
    private lateinit var cicekadı:String
    private lateinit var siparisverenad:String
    private lateinit var cicekadedi:String
    private lateinit var siparismaili:String
    private lateinit var cicekfiyat:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_siparis_bilgiler)
        db=Firebase.firestore
        cicekadı=intent.getStringExtra("cicekadı").toString()
        siparismaili=intent.getStringExtra("siparismaili").toString()
        cicekfiyat=intent.getStringExtra("cicekfiyatı").toString()
        cicekadedi=intent.getStringExtra("cicekadet").toString()
        TextAdminSiparisBilgilerCıcek.setText(cicekadı)
        TextAdminSiparisBilgilerCicekAdet.setText(cicekadedi)
        TextAdminSiparisBilgilerAdı.setText(siparismaili)
        //region Değerler değiştirilemez
        TextAdminSiparisBilgilerCıcek.isEnabled=false
        TextAdminSiparisBilgilerTelefon.isEnabled=false
        TextAdminSiparisBilgilerCicekAdet.isEnabled=false
        TextAdminSiparisBilgilerIlce.isEnabled=false
        TextAdminSiparisBilgilerAdres.isEnabled=false
        TextAdminSiparisBilgilerAdı.isEnabled=false
        //endregion
        VerileriAl()

        db.collection("Siparişler").whereEqualTo("Çiçek Adı",cicekadı).get().addOnSuccessListener {Bilgiler->
            var siparisdurumu=""
            for (Bilgi in Bilgiler)
            {
                siparisdurumu=Bilgi.get("Sipariş Durumu")as String
            }
            if (siparisdurumu=="Hazırlanıyor")
            {
                btnAdminSiparisOnay.visibility=View.VISIBLE
                btnSiparisiSil.visibility=View.INVISIBLE
            }
            else if(siparisdurumu=="Yola Çıktı")
            {
                btnAdminSiparisOnay.visibility=View.INVISIBLE
                btnSiparisiSil.visibility=View.VISIBLE
            }
            else
            {
                Toast.makeText(applicationContext,"Bir hata var",Toast.LENGTH_LONG).show()
            }
        }
    }
    fun VerileriAl()
    {
        db.collection("Müşteriler").whereEqualTo("E-Mail Adresi",siparismaili).get().addOnSuccessListener{Bilgiler->
            for (Bilgi in Bilgiler)
            {
                siparisverenad=Bilgi.get("Ad Soyad")as String
                TextAdminSiparisBilgilerAdı.setText(siparisverenad)
                TextAdminSiparisBilgilerAdres.setText(Bilgi.get("Adres")as String)
                TextAdminSiparisBilgilerIlce.setText(Bilgi.get("İlçe")as String)
                TextAdminSiparisBilgilerTelefon.setText(Bilgi.get("Telefon Numarası")as String)
            }
        }
    }
    fun Onay(view: View)
    {
        val SiparisDurum = hashMapOf<String, String>()
        SiparisDurum.put("Sipariş Durumu","Yola Çıktı")
        SiparisDurum.put("Ad Soyad",siparisverenad)
        SiparisDurum.put("Sipariş E-Mail Adresi",siparismaili)
        SiparisDurum.put("Çiçek Adı",cicekadı)
        SiparisDurum.put("Çiçek Sayısı",cicekadedi)
        SiparisDurum.put("Çiçek Fiyatı",intent.getStringExtra("cicekfiyatı")as String)
        val alert=AlertDialog.Builder(this)
        alert.setTitle("Sipariş onaylanıyor")
        alert.setMessage("Gelen siparişi onaylamak istediğinzden emin misiniz?")
        alert.setPositiveButton("Evet"){dialog,which->
            db.collection("Siparişler").document(cicekadı).set(SiparisDurum).addOnSuccessListener {
                val intent= Intent(applicationContext,AdminSepet::class.java)
                Thread.sleep(3000)
                startActivity(intent)
                finish()
            }.addOnFailureListener{
                Toast.makeText(applicationContext,"Bir hata meydana geldi",Toast.LENGTH_LONG).show()
            }
        }.setNegativeButton("Hayır"){dialog,which->
            Toast.makeText(applicationContext,"Sipariş Onaylanmadı",Toast.LENGTH_LONG).show()
        }.show()
        btnAdminSiparisOnay.visibility=View.INVISIBLE
        btnSiparisiSil.visibility=View.INVISIBLE
    }
    fun SiparişiSil(view: View)
    {
        db.collection("Siparişler").document(cicekadı).delete()
        val intent= Intent(applicationContext,AdminSepet::class.java)
        Thread.sleep(3000)
        startActivity(intent)
        finish()
    }
}