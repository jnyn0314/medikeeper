package ddwucom.mobile.medikeeper
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION_CODES.R
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmDebug", "Alarm received: ${intent.getStringExtra("time")}")
        val time = intent.getStringExtra("time") ?: "Unknown"
        val pillName = intent.getStringExtra("pill_name") ?: "Unknown Pill"

        // 알림 생성 및 표시
        val notification = NotificationCompat.Builder(context, "mychannel")
            .setContentTitle("Pill Reminder")
            .setContentText("It's time to take your $pillName ($time).")
            .setSmallIcon(R.drawable.ic_launcher_background) // 아이콘 설정
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(pillName.hashCode(), notification) // 약 이름으로 고유 ID 생성
    }
}
