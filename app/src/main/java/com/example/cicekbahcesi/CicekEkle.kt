package com.example.cicekbahcesi

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_cicek_ekle.*
import kotlinx.android.synthetic.main.activity_hesap_olusturma.*
import java.util.*
import kotlin.collections.ArrayList

class CicekEkle : AppCompatActivity() {
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var secilenResim: Uri?=null
    private lateinit var CicekMekan:String
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cicek_ekle)
        title="Çiçek Ekle"
        registerLauncher()
        auth= Firebase.auth
        db= Firebase.firestore
        storage= Firebase.storage
    }

    fun Ekle(view: View)
    {
            //evrensel benzersiz id
            val uuid= UUID.randomUUID()
            val resimadı="$uuid.jpg"
            val reference=storage.reference
            val resimreference=reference.child("Resimler").child(resimadı)
            if (secilenResim!=null)
            {
                resimreference.putFile(secilenResim!!).addOnSuccessListener {
                    val yüklenenresimreference=storage.reference.child("Resimler").child(resimadı)
                    yüklenenresimreference.downloadUrl.addOnSuccessListener { uri->
                        val resimlink=uri.toString()
                        val CicekBilgiler= hashMapOf<String,Any>()
                        CicekBilgiler.put("Çiçek Adı",txtCicekAdı.text.toString())
                        CicekBilgiler.put("Fiyat",txtCicekFiyat.text.toString())
                        CicekBilgiler.put("Çiçek Özellikleri",txtCicekÖzellik.text.toString())
                        CicekBilgiler.put("Resim Link",resimlink)
                        if(chcDısMekan.isChecked)
                        {
                            CicekMekan="Dış Mekan"
                        }
                        else if(chcIcMekan.isChecked)
                        {
                            CicekMekan="İç Mekan"
                        }
                        else if(chcIcMekan.isChecked&&chcDısMekan.isChecked)
                        {
                            CicekMekan="Fark Etmez"
                        }
                        else
                        {
                            CicekMekan="Belirtilmemiş"
                        }
                        CicekBilgiler.put("Mekan",CicekMekan)
                        db.collection("Çiçekler").document(txtCicekAdı.text.toString()).set(CicekBilgiler).addOnSuccessListener {
                            Toast.makeText(applicationContext,"Başarıyla Eklendi",Toast.LENGTH_LONG).show()
                        }
                        /*db.collection("Çiçekler").add(CicekBilgiler).addOnSuccessListener {
                            Toast.makeText(applicationContext,"Başarıyla Eklendi",Toast.LENGTH_LONG).show()
                        }.addOnFailureListener{
                            Toast.makeText(applicationContext,"Bir hata meydana geldi\n" +
                            "Tekrar deneyiniz!",Toast.LENGTH_LONG).show()
                        }

                         */
                    }
            }
        }
    }

    fun ResimEkle(view: View)
    {
        txtCicekAdı.setText("")
        txtCicekFiyat.setText("")
        txtCicekÖzellik.setText("")
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            Snackbar.make(view,"Galeri için izin gerekmektedir.", Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver",){
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                //İzin İsteme
            }.show()
        }
        else
        {
            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
    else
    {
        val intenttogallery=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(intenttogallery)
    }
    }

    private fun registerLauncher()
    {
        activityResultLauncher =registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if (result.resultCode== RESULT_OK)
            {
                val intentfromResult=result.data
                if (intentfromResult!=null)
                {
                    secilenResim=intentfromResult.data
                    secilenResim?.let {
                        CicekResim.setImageURI(it)
                    }
                }
            }
        }
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
            if (result==true)
            {
                val intenttogallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intenttogallery)
            }
            else
            {
                Toast.makeText(applicationContext,"Resim eklemesi yapılabilmesi için izin gerekmektedir!", Toast.LENGTH_LONG).show()
            }
        }
    }
}