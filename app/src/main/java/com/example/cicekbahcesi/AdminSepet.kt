package com.example.cicekbahcesi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_admin_sepet.*
import kotlinx.android.synthetic.main.activity_musteri_sepet.*

class AdminSepet : AppCompatActivity() {
    private lateinit var SepettekiCicekler:ArrayList<String>
    private lateinit var SiparisMailleri:ArrayList<String>
    private lateinit var CicekAdetleri:ArrayList<String>
    private lateinit var CicekFiyatları:ArrayList<String>
    private lateinit var db:FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_sepet)
        SepettekiCicekler=ArrayList<String>()
        SiparisMailleri=ArrayList<String>()
        CicekAdetleri=ArrayList<String>()
        CicekFiyatları=ArrayList<String>()
        title="Gelen Siparişler"
        db=Firebase.firestore
        VerileriAl()
        val adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,SepettekiCicekler)
        LstAdminSepetGelenSiparisler.adapter=adapter
        LstAdminSepetGelenSiparisler.onItemClickListener=AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val secilencicek=SepettekiCicekler.get(i)
            val secilencicekmail=SiparisMailleri.get(i)
            val cicekadedi=CicekAdetleri.get(i)
            val cicekfiyatı=CicekFiyatları.get(i)
            val intent=Intent(applicationContext,AdminSiparisBilgiler::class.java)
            intent.putExtra("cicekadı",secilencicek)
            intent.putExtra("siparismaili",secilencicekmail)
            intent.putExtra("cicekfiyatı",cicekfiyatı)
            intent.putExtra("cicekadet",cicekadedi)
            startActivity(intent)
        }
    }

    fun VerileriAl()
    {
            db.collection("Siparişler").addSnapshotListener{value,error->
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
                            val Bilgiler=value.documents
                            for (Bilgi in Bilgiler)
                            {
                                SepettekiCicekler.add(Bilgi.get("Çiçek Adı") as String)
                                SiparisMailleri.add(Bilgi.get("Sipariş E-Mail Adresi")as String)
                                CicekAdetleri.add(Bilgi.get("Çiçek Sayısı")as String)
                                CicekFiyatları.add(Bilgi.get("Çiçek Fiyatı")as String)
                            }
                        }
                    }
                }
            }
    }
}