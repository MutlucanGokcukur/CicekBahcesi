package com.example.cicekbahcesi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sifremi_unuttum.*

class SifremiUnuttum : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sifremi_unuttum)
        title = "Şifre Sıfırlama"
    }
    fun MailGonder(view: View)
    {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Emin Misiniz?")
        alert.setMessage("Girdiğiniz E-Mail adresine şifre sıfırlama maili gönderilecektir." +
                "\nE-Mail adresinizin doğru olduğundan emin misiniz?")
        alert.setNegativeButton("Hayır") { dialog, which ->
            Toast.makeText(applicationContext,
                "Mail gönderme işlemi iptal edildi.",
                Toast.LENGTH_LONG)
        }
        alert.setPositiveButton("Evet") { dialog, which ->
            val email=PlnSıfreSıfırlamaMail.text.toString()
            if (email.isEmpty())
            {
                Toast.makeText(applicationContext,"Lütfen şifresini sıfırlamak istediğiniz hesabın mail adresini giriniz.",Toast.LENGTH_LONG).show()
            }
            else
            {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener {
                    Toast.makeText(applicationContext,"Mail başarılı bir şekilde gönderildi\n" +
                            "Lütfen mail adresinize bakınız.",Toast.LENGTH_LONG).show()
                }.addOnFailureListener{
                    Toast.makeText(applicationContext,"Mail gönderilemedi\n" +
                            "Lütfen mail adresinin doğruluğunu kontrol ediniz.",Toast.LENGTH_LONG).show()
                }
            }
        }.show()
    }

}