package ddwucom.mobile.medikeeper
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import ddwucom.mobile.medikeeper.data.pill.Pill
import ddwucom.mobile.medikeeper.data.pill.PillDao
import ddwucom.mobile.medikeeper.data.pill.PillDatabase
import ddwucom.mobile.medikeeper.databinding.ActivityAddmedicineBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AddmedicineActivity : AppCompatActivity() {

    val TAG = "AddmedicineActivity"

    val binding by lazy {
        ActivityAddmedicineBinding.inflate(layoutInflater)
    }

    lateinit var pillDao: PillDao
    private var isStartDate = true // 시작 날짜인지 종료 날짜인지 상태 저장

    val pillDB by lazy {
        PillDatabase.getDatabase(this)
    }
    val channelID by lazy {
        "mychannel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 뒤로가기 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish() // 뒤로가기 버튼 클릭 시 현재 액티비티 종료
        }

        // 데이터베이스 초기화
        pillDao = pillDB.pillDao()

        // 캘린더에서 날짜 선택 이벤트 처리
        binding.cvCalendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$year-${month + 1}-$dayOfMonth" // 날짜 포맷 문자열

            if (isStartDate) {
                binding.tvStartDt.text = "Start Date: $selectedDate" // 시작 날짜 설정
                isStartDate = false
            } else {
                binding.tvEndDt.text = "End Date: $selectedDate" // 종료 날짜 설정
                isStartDate = true
            }
        }

        // 저장 버튼 클릭 이벤트 처리
        binding.btSubmit.setOnClickListener {
            Log.d(TAG, "Submit button clicked")

            val name = binding.etMediName.text.toString()
            val disease = binding.etDisease.text.toString()
            val morning = binding.cbMorning.isChecked
            val lunch = binding.cbLunch.isChecked
            val dinner = binding.cbDinner.isChecked
            val beforeBed = binding.cbBeforebed.isChecked
            val memo = binding.etMemo.text.toString()

            // 시작 날짜와 종료 날짜 가져오기
            val startDate = getDateFromTextView(binding.tvStartDt.text.toString())
            val endDate = getDateFromTextView(binding.tvEndDt.text.toString())

            // 날짜 유효성 검사
            if (startDate == null || endDate == null) {
                Toast.makeText(this, "Please select both start and end dates.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 데이터베이스에 저장할 Pill 객체 생성
            val pill = Pill(
                name = name,
                disease = disease,
                morning = morning,
                lunch = lunch,
                dinner = dinner,
                beforeBed = beforeBed,
                startDate = startDate,
                endDate = endDate,
                memo = memo
            )

            // 비동기 작업 실행
            lifecycleScope.launch(Dispatchers.IO) {
                pillDao.insertPill(pill) // Pill 삽입

                // 알람 재설정은 UI 스레드에서 실행
                withContext(Dispatchers.Main) {
                    pillDao.getAllPills().observe(this@AddmedicineActivity) { pillList ->
                        setupAlarmWithPills(pillList) // pillList를 기반으로 알람 재설정
                    }
                    Toast.makeText(
                        this@AddmedicineActivity,
                        "Pill information saved and alarms set successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                val intent = Intent(this@AddmedicineActivity, RvlistActivity::class.java)
                startActivity(intent)  // 새로운 액티비티 시작
                finish()  // 현재 액티비티 종료
            }
        }
    }


    // TextView의 텍스트를 Date 객체로 변환하는 함수
    private fun getDateFromTextView(text: String): Date? {
        val datePattern = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(datePattern, Locale.getDefault())
        return try {
            val dateString = text.split(": ")[1] // "Start Date: yyyy-MM-dd"에서 날짜 부분만 추출
            sdf.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    private fun setupAlarmWithPills(pillList: List<Pill>) {
        val manager = getSystemService(ALARM_SERVICE) as AlarmManager

        // 기존 알람 제거
        for (pill in pillList) {
            cancelAlarm(manager, pill._id * 10 + 1) // Morning
            cancelAlarm(manager, pill._id * 10 + 2) // Lunch
            cancelAlarm(manager, pill._id * 10 + 3) // Dinner
            cancelAlarm(manager, pill._id * 10 + 4) // Before Bed
        }

        for (pill in pillList) {
            if (pill.morning) {
                val morningIntent = Intent(this, AlarmReceiver::class.java).apply {
                    putExtra("time", "Morning")
                    putExtra("pill_name", pill.name)
                }
                val morningPendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    pill._id * 10 + 1,
                    morningIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setActualAlarm(manager, morningPendingIntent, 9)
            }
            if (pill.lunch) {
                val lunchIntent = Intent(this, AlarmReceiver::class.java).apply {
                    putExtra("time", "Lunch")
                    putExtra("pill_name", pill.name)
                }
                val lunchPendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    pill._id * 10 + 2,
                    lunchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setActualAlarm(manager, lunchPendingIntent, 12)
            }
            if (pill.dinner) {
                val dinnerIntent = Intent(this, AlarmReceiver::class.java).apply {
                    putExtra("time", "Dinner")
                    putExtra("pill_name", pill.name)
                }
                val dinnerPendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    pill._id * 10 + 3,
                    dinnerIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setActualAlarm(manager, dinnerPendingIntent, 18)
            }
            if (pill.beforeBed) {
                val beforeBedIntent = Intent(this, AlarmReceiver::class.java).apply {
                    putExtra("time", "Before Bed")
                    putExtra("pill_name", pill.name)
                }
                val beforeBedPendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    pill._id * 10 + 4,
                    beforeBedIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                // 변경된 시간 (4시 7분)
                val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.HOUR_OF_DAY, 4) // 4시
                    set(Calendar.MINUTE, 15)     // 7분
                    set(Calendar.SECOND, 0)     // 0초
                }
                manager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    beforeBedPendingIntent
                )
            }
        }
        createNotificationChannel()
    }

        // 기존 알람 취소 함수
    private fun cancelAlarm(manager: AlarmManager, requestCode: Int) {
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE // 기존 PendingIntent 검색
        )
        if (pendingIntent != null) {
            manager.cancel(pendingIntent) // 알람 취소
            pendingIntent.cancel() // PendingIntent 제거
        }
    }


    private fun setActualAlarm(manager: AlarmManager, pendingIntent: PendingIntent, hourOfDay: Int) {
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay) // 지정된 시간 설정
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        manager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, // 하루 간격 반복
            pendingIntent
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = "Test Channel"
            val descriptionText = "Test Channel Message"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(channelID, name, importance)
            mChannel.description = descriptionText

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                if (notificationManager.areNotificationsEnabled()) {
                    Toast.makeText(applicationContext, "Notifications are enabled", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Notifications are not enabled", Toast.LENGTH_SHORT).show()

                    // 알림 권한을 요청하려면 사용자를 설정 페이지로 안내
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    startActivity(intent)  // 알림 설정 페이지로 이동
                }
            } else {
                // 안드로이드 13 이하에서는 알림 권한이 자동으로 활성화되므로 이 부분은 필요 없음
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(mChannel)
                Toast.makeText(applicationContext, "Notifications are enabled", Toast.LENGTH_SHORT).show()
            }


        }
    }
}
