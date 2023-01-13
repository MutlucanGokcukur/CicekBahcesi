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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_hesap_olusturma.*
import kotlinx.android.synthetic.main.activity_hesap_olusturma.PlnMusteriListesiAd
import kotlinx.android.synthetic.main.activity_hesap_olusturma.txtHesapOlusturmaEmail
import kotlinx.android.synthetic.main.activity_hesap_olusturma.PlnMusteriListesiSifre
import kotlinx.android.synthetic.main.activity_hesap_olusturma.PlnMusteriListesiTelefonNumara
import kotlinx.android.synthetic.main.activity_hesap_olusturma.txtHesapOlusturmaIlceler

class HesapOlusturma : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    var Ilce=""
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hesap_olusturma)
        title="Hesap Oluşturma"
        auth= Firebase.auth
        db=Firebase.firestore
        //Values Klasöründeki strings.xml içindeki İlceler listesi
        var İlceler=resources.getStringArray(R.array.İlceler)

        //region Spinner içine Listeyi Ekleme
        if (txtHesapOlusturmaIlceler!=null)
        {
            val adapter=ArrayAdapter(this,
            android.R.layout.simple_spinner_item,İlceler)
            txtHesapOlusturmaIlceler.adapter=adapter
        }
        //endregion

        //region Spinner Üstünde Hangi İlçeyi Seçtiğini Takip Etme
        txtHesapOlusturmaIlceler.onItemSelectedListener=object :AdapterView.OnItemSelectedListener
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
    }

        fun Olustur(view: View)
    {
        val emailadres=txtHesapOlusturmaEmail.text.toString()
        val sifre=txtsifretekrar.text.toString()

        val alert = AlertDialog.Builder(this)
        if (PlnMusteriListesiAd.text.toString()!=""&&txtHesapOlusturmaEmail.text.toString()!=""&&
        PlnMusteriListesiTelefonNumara.text.toString()!=""&& PlnMusteriListesiSifre.text.toString()!=""&&
        txtsifretekrar.text.toString()!=""&& txtHesapOlusturmaAdres.text.toString()!="")
        {
                if(PlnMusteriListesiSifre.text.toString()!=txtsifretekrar.text.toString())
                {
                    alert.setTitle("Bir sorun var")
                    alert.setMessage("Girdiğiniz şifreler birbiri ile eşleşmemektedir.\n" +
                            "Lütfen şifrenizi yeniden oluşturunuz.")
                    alert.setPositiveButton("Tamam"){dialog,which ->}
                    PlnMusteriListesiSifre.setText("")
                    txtsifretekrar.setText("")
                    alert.show()
                }
                else {
                    alert.setTitle("Emin Misiniz?")
                    alert.setMessage("Hesap Oluşturuluyor...\nBilgilerinizin doğruluğundan emin misiniz?")
                    alert.setNegativeButton("Hayır") { dialog, which ->
                        Toast.makeText(applicationContext,
                            "Hesap oluşturma işlemi iptal edildi.",
                            Toast.LENGTH_LONG).show()
                    }
                    alert.setPositiveButton("Evet") { dialog, which ->
                        val MusteriBilgi = hashMapOf<String, String>()
                        MusteriBilgi.put("Ad Soyad", PlnMusteriListesiAd.text.toString())
                        MusteriBilgi.put("İlçe", Ilce)
                        MusteriBilgi.put("E-Mail Adresi", txtHesapOlusturmaEmail.text.toString())
                        MusteriBilgi.put("Adres", txtHesapOlusturmaAdres.text.toString())
                        MusteriBilgi.put("Telefon Numarası", PlnMusteriListesiTelefonNumara.text.toString())
                        MusteriBilgi.put("Şifre", PlnMusteriListesiSifre.text.toString())


                        val dosya=db.collection("Müşteriler").document(PlnMusteriListesiAd.text.toString())

                        dosya.set(MusteriBilgi).addOnSuccessListener {
                            auth.createUserWithEmailAndPassword(emailadres,sifre).addOnSuccessListener {
                                Toast.makeText(applicationContext,"Hesabınız başarıyla oluşturuldu\n" +
                                "Aramıza Hoşgeldiniz!",Toast.LENGTH_LONG).show()
                                intent=Intent(applicationContext,MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }.addOnFailureListener{
                                Toast.makeText(applicationContext,"Bilgilerin kaydedilirken bir hata ile karşılaşıldı" +
                                "\nTekrar deneyiniz.",Toast.LENGTH_LONG).show()
                            }
                        }
                    }.show()
                }
        }

        else
        {
            alert.setTitle("Bir sorun var")
            alert.setMessage("Eksik bilgiler bulunmaktadır.\n" +
            "Lütfen bütün bilgileri eksiksiz bir şekilde giriniz!!!")
            alert.setPositiveButton("Tamam"){dialog,which ->}
            alert.show()
        }
    }
}