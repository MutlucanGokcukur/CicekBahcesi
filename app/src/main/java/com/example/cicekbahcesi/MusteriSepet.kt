package com.example.cicekbahcesi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.oAuthCredential
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_hesap_olusturma.*
import kotlinx.android.synthetic.main.activity_musteri_sayfa.*
import kotlinx.android.synthetic.main.activity_musteri_sepet.*

class MusteriSepet : AppCompatActivity() {
    var toplam=0
    var cicekadi=""
    var cicekfiyat=""
    var cicekadet=""
    var toplamtutar=0
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var SepettekiCicekler:ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_musteri_sepet)
        SepettekiCicekler=ArrayList<String>()
        auth= Firebase.auth
        var GirenkisiMail=auth.currentUser!!.email
        PlnMusteriSepetToplamTutar.isEnabled=false
        title="Sepet"
        db=Firebase.firestore

        db.collection("Sepettekiler").whereEqualTo("Sipariş Veren Kişi E-Mail Adresi",GirenkisiMail).get().addOnSuccessListener { Sepet->
            for (Sepettekiler in Sepet)
            {
                cicekadi=Sepettekiler.get("Çiçek Adı")as String
                cicekfiyat=Sepettekiler.get("Çiçek Fiyatı")as String
                cicekadet=Sepettekiler.get("Çiçek Sayısı")as String
                toplamtutar=cicekadet.toInt()*cicekfiyat.toInt()
                SepettekiCicekler.add(cicekadi)
                toplam=toplam+toplamtutar
            }
            PlnMusteriSepetToplamTutar.setText(toplam.toString())
        }
        val adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,SepettekiCicekler)
        LstMusteriSepetCicekler.adapter=adapter
        LstMusteriSepetCicekler.onItemClickListener= AdapterView.OnItemClickListener { parent, view, position, id ->
            val alert=AlertDialog.Builder(this)
            val secilencicek=SepettekiCicekler.get(position)
            db.collection("Sepettekiler").whereEqualTo("Çiçek Adı",secilencicek).get().addOnSuccessListener { Bilgiler->
                var secilenintutar=0
                for (Cicekler in Bilgiler)
                {
                    cicekfiyat=Cicekler.get("Çiçek Fiyatı")as String
                    cicekadet=Cicekler.get("Çiçek Sayısı")as String
                    secilenintutar=cicekadet.toInt()*cicekfiyat.toInt()
                }
                toplam=toplam-secilenintutar
                alert.setMessage("Seçili Çiçeği listenizden çıkarmak istediğinzden emin misiniz?" +
                        "\nÇıkarılmasını istediğiniz çiçek bilgileri\nSeçilen Çiçek:$secilencicek\tToplam Tutarı:${secilenintutar}")
                alert.setPositiveButton("Evet"){dialog,which->
                    adapter.remove(secilencicek)
                    db.collection("Sepettekiler").document(secilencicek).delete()
                    PlnMusteriSepetToplamTutar.setText(toplam.toString())
                }
                alert.setNegativeButton("Hayır"){dialog,which->

                }.show()
            }
        }
    }

    fun SepetOnay(view: View)
    {
        var girenkisimail=auth.currentUser!!.email
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Emin misiniz?")
        alert.setMessage("Sepetinizi onaylamak istediğinizden emin misiniz?")
        alert.setPositiveButton("Evet"){dialog,which->
            db.collection("Sepettekiler").whereEqualTo("Sipariş Veren Kişi E-Mail Adresi",girenkisimail).get().addOnSuccessListener {Bilgiler->
                var cicekisim=""
                var siparissahibi=""
                var siparisdurum=""
                var siparismaili=""
                var cicekfiyat=""
                var cicekadet=""
                for (Bilgi in Bilgiler)
                {
                    cicekisim=Bilgi.get("Çiçek Adı")as String
                    siparisdurum=Bilgi.get("Sipariş Durumu")as String
                    siparissahibi=Bilgi.get("Sipariş Veren Ad Soyad")as String
                    siparismaili=Bilgi.get("Sipariş Veren Kişi E-Mail Adresi")as String
                    cicekfiyat=Bilgi.get("Çiçek Fiyatı")as String
                    cicekadet=Bilgi.get("Çiçek Sayısı")as String
                    val SiparisBilgiler = hashMapOf<String, String>()
                    SiparisBilgiler.put("Ad Soyad",siparissahibi)
                    SiparisBilgiler.put("Çiçek Adı",cicekisim)
                    SiparisBilgiler.put("Sipariş Durumu",siparisdurum)
                    SiparisBilgiler.put("Sipariş E-Mail Adresi",siparismaili)
                    SiparisBilgiler.put("Çiçek Fiyatı",cicekfiyat)
                    SiparisBilgiler.put("Çiçek Sayısı",cicekadet)
                    val dosya=db.collection("Siparişler").document("$cicekisim")
                    dosya.set(SiparisBilgiler)
                    db.collection("Sepettekiler").document(cicekisim).delete()
                    val adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,SepettekiCicekler)
                    adapter.remove(cicekisim)
                }
            }
            Toast.makeText(applicationContext,"Sepetiniz Onaylandı\nGün içinde telefonunuzu açık tutunuz",Toast.LENGTH_LONG).show()
            val intent=Intent(applicationContext,MusteriSayfa::class.java)
            startActivity(intent)
            finish()
        }
        alert.setNegativeButton("Hayır"){dialog,which->
            Toast.makeText(applicationContext,"Sepetiniz Onaylanmadı",Toast.LENGTH_LONG).show()
        }.show()
    }
}