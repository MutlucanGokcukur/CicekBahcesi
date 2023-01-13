package com.example.cicekbahcesi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_musteri_sayfa.*

class MusteriSayfa : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var cicekadi:String
    private lateinit var db: FirebaseFirestore
    private lateinit var girenkisisifre:String
    private lateinit var GirenKisiMail:String
    private lateinit var Liste:ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_musteri_sayfa)
        girenkisisifre=intent.getStringExtra("Sifre").toString()
        GirenKisiMail=intent.getStringExtra("E-Mail Adresi").toString()
        Liste=ArrayList<String>()
        auth= Firebase.auth
        db=Firebase.firestore
        title="Ana Sayfa"
        verilerigetir()
        //ListView çiçekleri ekleme
        val adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,Liste)
        LstMusteriSayfaCicekListe.adapter=adapter
        //Seçilen çiçeğin hangi çiçek olduğunu algılama ve o çiçeğin bilgilerini öbür sayfaya gönderme.
        LstMusteriSayfaCicekListe.onItemClickListener= AdapterView.OnItemClickListener{ parent, view, position, id->
            db.collection("Çiçekler").whereEqualTo("Çiçek Adı",Liste[position]).get().addOnSuccessListener { Bilgiler->
                val intent = Intent(applicationContext,MusteriCicekBilgiler::class.java)
                for (Bilgi in Bilgiler)
                {
                    intent.putExtra("siparismaili",auth.currentUser!!.email)
                    intent.putExtra("cicekadı",Bilgi.get("Çiçek Adı")as String)
                    intent.putExtra("cicekfiyat",Bilgi.get("Fiyat")as String)
                    intent.putExtra("cicekmekan",Bilgi.get("Mekan")as String)
                    intent.putExtra("cicekresim",Bilgi.get("Resim Link")as String)
                    intent.putExtra("cicekozellik",Bilgi.get("Çiçek Özellikleri")as String)
                }
                startActivity(intent)
            }
        }
    }

    fun verilerigetir()
    {
        db.collection("Çiçekler").addSnapshotListener{value,error->
            if(error!=null)
            {
                Toast.makeText(applicationContext,"Hata",Toast.LENGTH_LONG).show()
            }
            else
            {
                if (value!=null){
                    if (value.isEmpty){ }
                    else
                    {
                        val dosyalar=value.documents
                        for (Cicek in dosyalar)
                        {
                            cicekadi=Cicek.get("Çiçek Adı") as String
                            Liste.add(cicekadi)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.signout)
        {
            auth.signOut()
            val intent=Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else if(item.itemId==R.id.siparisdurum)
        {
            db.collection("Siparişler").whereEqualTo("Sipariş E-Mail Adresi",GirenKisiMail).get().addOnSuccessListener {Bilgiler->
                if (Bilgiler.isEmpty)
                {
                    Toast.makeText(applicationContext,"Sipariş bilgisi bulunamadı",Toast.LENGTH_LONG).show()
                }
                else
                {
                    for (Bilgi in Bilgiler)
                    {
                        val siparisdurum=Bilgi.get("Sipariş Durumu")as String
                        Toast.makeText(applicationContext,"Siparişiniz sisteme düşmüştür\n" +
                                "Şu anki sipariş durumu:\n$siparisdurum",Toast.LENGTH_LONG).show()
                    }
                }
            }.addOnFailureListener{
                Toast.makeText(applicationContext,"Sipariş bilgisi bulunamadı",Toast.LENGTH_LONG).show()
            }
        }
        else if(item.itemId==R.id.hesapbilgileri)
        {
            val intent=Intent(applicationContext,MusteriBilgiler::class.java)
            intent.putExtra("E-Mail Adresi",GirenKisiMail)
            intent.putExtra("GirenSifre",girenkisisifre)
            startActivity(intent)
        }
        else if(item.itemId==R.id.sepettekiler)
        {
            try {
                val intent=Intent(applicationContext,MusteriSepet::class.java)
                startActivity(intent)
            }
            catch (e:java.lang.Exception)
            {
                Toast.makeText(applicationContext,"Sepet boş",Toast.LENGTH_LONG).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}