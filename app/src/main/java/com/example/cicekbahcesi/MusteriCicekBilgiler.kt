package com.example.cicekbahcesi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_musteri_cicek_bilgiler.*

class MusteriCicekBilgiler : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var cicekadı:String
    private lateinit var cicekfiyat:String
    private lateinit var cicekmekan:String
    private lateinit var cicekozellik:String
    private lateinit var SiparisVerenKisiMail:String
    private lateinit var cicekresim:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_musteri_cicek_bilgiler)
        db= Firebase.firestore
        title="Çiçek Bilgileri"
        //region  gelen verileri alma
        SiparisVerenKisiMail=intent.getStringExtra("siparismaili").toString()
        cicekadı= intent.getStringExtra("cicekadı",).toString()
        cicekfiyat=intent.getStringExtra("cicekfiyat").toString()
        cicekmekan=intent.getStringExtra("cicekmekan").toString()
        cicekozellik=intent.getStringExtra("cicekozellik").toString()
        cicekresim=intent.getStringExtra("cicekresim").toString()
        //endregion

        //region Gelen Bilgiler
        PlnCicekBilgilerCicekAdı.setText(cicekadı)
        PlnCicekBilgilerCicekFiyat.setText("Fiyat:"+cicekfiyat+"₺")
        PlnCicekBilgilerCicekOzellikleri.setText(cicekozellik)
        //Picasso implement edildi
        Picasso.get().load(cicekresim).into(PlnCicekBilgilerCicekResim)
        //endregion
        //region Mekan Durumu
        if (cicekmekan=="Dış Mekan")
        {
            ChcCicekBilgilerCicekDısMekan.isChecked=true
            ChcCicekBilgilerCicekDısMekan.isClickable=false
        }
        else if(cicekmekan=="İç Mekan")
        {
            ChcCicekBilgilerCicekIcMekan.isChecked=true
            ChcCicekBilgilerCicekIcMekan.isClickable=false
        }
        else if(cicekmekan=="Fark Etmez")
        {
            ChcCicekBilgilerCicekIcMekan.isChecked=true
            ChcCicekBilgilerCicekIcMekan.isClickable=false
            ChcCicekBilgilerCicekDısMekan.isChecked=true
            ChcCicekBilgilerCicekDısMekan.isClickable=false
        }
        else if(cicekmekan=="Belirtilmemiş")
        {
            ChcCicekBilgilerCicekIcMekan.isChecked=false
            ChcCicekBilgilerCicekIcMekan.isClickable=false
            ChcCicekBilgilerCicekDısMekan.isChecked=false
            ChcCicekBilgilerCicekDısMekan.isClickable=false
        }
        //endregion
        //region Bilgi Değiştirme Kapalı
        PlnCicekBilgilerCicekAdı.isEnabled=false
        PlnCicekBilgilerCicekFiyat.isEnabled=false
        PlnCicekBilgilerCicekOzellikleri.isEnabled=false
        ChcCicekBilgilerCicekDısMekan.isEnabled=false
        ChcCicekBilgilerCicekIcMekan.isEnabled=false
        //endregion

    }

    fun SepeteEkle(view: View)
    {
        val CicekAdet=PlnCicekBilgilerCicekAdet.text.toString()
        if (!PlnCicekBilgilerCicekAdet.text.isEmpty())
        {
            db.collection("Müşteriler").whereEqualTo("E-Mail Adresi",SiparisVerenKisiMail).get().addOnSuccessListener {Bilgiler->
                var isim =""
                for (Bilgi in Bilgiler)
                {
                    isim = Bilgi.get("Ad Soyad") as String
                }
                val SiparisBilgi = hashMapOf<String, Any>()
                SiparisBilgi.put("Çiçek Adı",cicekadı)
                SiparisBilgi.put("Çiçek Sayısı",CicekAdet)
                SiparisBilgi.put("Sipariş Veren Kişi E-Mail Adresi",SiparisVerenKisiMail)
                SiparisBilgi.put("Çiçek Fiyatı",cicekfiyat)
                SiparisBilgi.put("Sipariş Veren Ad Soyad",isim)
                SiparisBilgi.put("Sipariş Durumu","Hazırlanıyor")
                db.collection("Sepettekiler").document(cicekadı).set(SiparisBilgi)
                Toast.makeText(applicationContext,"Sepete eklendi",Toast.LENGTH_LONG).show()
            }
        }
        else
        {
            Toast.makeText(applicationContext,"Lütfen geçerli bir çiçek sayısı giriniz",Toast.LENGTH_LONG).show()
        }
    }
}