package com.example.cicekbahcesi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.cicekbahcesi.Animasyon.GirisAnimasyon
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_admin_sayfa.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_musteri_sayfa.*

class AdminSayfa : AppCompatActivity() {
    private lateinit var CicekListesi:ArrayList<String>
    private lateinit var GirenKisiSifre:String
    private lateinit var db:FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_sayfa)
        title="Admin Sayfa"
        CicekListesi=ArrayList<String>()
        db=Firebase.firestore
        GirenKisiSifre=intent.getStringExtra("Sifre").toString()

        VerileriGetir()
        val adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,CicekListesi)
        LstAdminSayfaCicekler.adapter=adapter
        LstAdminSayfaCicekler.onItemClickListener= AdapterView.OnItemClickListener{ parent, view, position, id->
            val intent=Intent(applicationContext,AdminCicekBilgiler::class.java)
            val secilencicek=CicekListesi.get(position)
            intent.putExtra("secilencicek",secilencicek)
            startActivity(intent)
        }
    }

    fun VerileriGetir()
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
                            CicekListesi.add(Cicek.get("Çiçek Adı")as String)
                        }
                    }
                }
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.adminmenu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.signout)
        {
            val intent=Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else if(item.itemId==R.id.musteribilgileri)
        {
            val intent=Intent(applicationContext,MusteriListesi::class.java)
            intent.putExtra("Sifre",GirenKisiSifre)
            startActivity(intent)
        }
        else if(item.itemId==R.id.cicekekle)
        {
            val intent=Intent(applicationContext,CicekEkle::class.java)
            startActivity(intent)
        }
        else if(item.itemId==R.id.gelensiparisler)
        {
            val yükleniyor= GirisAnimasyon(this)
            yükleniyor.YüklemeEkranıBaslangıc()
            val handler= Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    yükleniyor.Kapat()
                    val intent=Intent(applicationContext,AdminSepet::class.java)
                    startActivity(intent)
                }
            },3000)
        }
        return super.onOptionsItemSelected(item)
    }
}