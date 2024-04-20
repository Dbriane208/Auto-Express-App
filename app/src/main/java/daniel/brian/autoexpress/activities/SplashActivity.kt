package daniel.brian.autoexpress.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import daniel.brian.autoexpress.databinding.ActivitySplashBinding

@Suppress("DEPRECATION")
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // hiding the support action bar
        supportActionBar?.hide()

        // delaying the screen for 3 sec
        Handler().postDelayed({
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        },3000)
    }
}