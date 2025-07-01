package ddwucom.mobile.medikeeper

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // 기존 MainActivity 레이아웃

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, TmapActivity::class.java) // 지도 Activity로 이동
            startActivity(intent)
            finish() // MainActivity 종료
        }, 2000) // 2000ms = 2초
    }
}
