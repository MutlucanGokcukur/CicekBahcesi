package com.example.cicekbahcesi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_hesap_olusturma.*
import kotlinx.android.synthetic.main.activity_musteri_bilgiler.*
import kotlinx.android.synthetic.main.activity_musteri_listesi.*
import kotlinx.android.synthetic.main.activity_musteri_listesi.PlnMusteriListesiAd
import kotlinx.android.synthetic.main.activity_musteri_listesi.PlnMusteriListesiTelefonNumara
import kotlinx.android.synthetic.main.activity_musteri_listesi.view.*

class MusteriListesi : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var GirenKisiSifre:String
    private lateinit var Liste:ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_musteri_listesi)
        title="Müşteri Listesi"
        Liste=ArrayList<String>()
        db = Firebase.firestore
        auth = Firebase.auth
        GirenKisiSifre=intent.getStringExtra("Sifre").toString()
        KullanıcıyaBasma()
        //region Bilgileri Değiştirmeyi Kapatma
        PlnMusteriListesiAd.isEnabled=false
        PlnMusteriListesiTelefonNumara.isEnabled=false
        PlnMusteriListesiIlce.isEnabled=false
        PlnMusteriListesiEmailAdres.isEnabled=false
        PlnMusteriListesiAdres.isEnabled=false
        //endregion

        val adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,Liste)
        LstMüsteriListesiMusteriListesi.adapter=adapter
        LstMüsteriListesiMusteriListesi.onItemClickListener=AdapterView.OnItemClickListener{ parent, view, position, id->
            db.collection("Müşteriler").whereEqualTo("Ad Soyad",Liste[position]).get().addOnSuccessListener { Bilgiler->
                for(Bilgi in Bilgiler)
                {
                    PlnMusteriListesiAd.setText(Bilgi.get("Ad Soyad")as String)
                    PlnMusteriListesiTelefonNumara.setText(Bilgi.get("Telefon Numarası") as String)
                    PlnMusteriListesiIlce.setText(Bilgi.get("İlçe")as String)
                    PlnMusteriListesiEmailAdres.setText(Bilgi.get("E-Mail Adresi") as String)
                    PlnMusteriListesiAdres.setText(Bilgi.get("Adres")as String)
                }
            }
        }
    }

    fun KullanıcıyaBasma()
    {
            db.collection("Müşteriler").addSnapshotListener{value,error->
                val KullanıcıBilgiler=value!!.documents
                for (Kullanıcı in KullanıcıBilgiler)
                {
                    val adsoyad=Kullanıcı.get("Ad Soyad") as String
                    Liste.add(adsoyad)
                }
            }

    }


    fun KullanıcıSil(view: View)
    {
        val alert = AlertDialog.Builder(this)
        if (PlnMusteriListesiAd.text.toString()=="")
        {
            Toast.makeText(applicationContext,"Lütfen bir müşteri seçiniz",Toast.LENGTH_LONG).show()
        }
        else
        {
            alert.setTitle("Emin Misiniz?")
            alert.setMessage("Seçili müşteriyi silmek istediğinizden emin misiniz?\n" +
            "Seçili müşteri: "+PlnMusteriListesiAd.text.toString())
            alert.setPositiveButton("Evet"){dialog,which ->
                db.collection("Müşteriler").document(PlnMusteriListesiAd.text.toString()).delete()
                PlnMusteriListesiAd.setText("")
                PlnMusteriListesiTelefonNumara.setText("")
                PlnMusteriListesiIlce.setText("")
                PlnMusteriListesiEmailAdres.setText("")
                PlnMusteriListesiAdres.setText("")
            }
            alert.setNegativeButton("Hayır"){dialog,which->
                Toast.makeText(applicationContext,"Silme işlemi iptal edildi.", Toast.LENGTH_LONG).show()
            }
        }
        alert.show()
    }
}