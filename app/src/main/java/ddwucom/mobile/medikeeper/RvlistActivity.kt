package ddwucom.mobile.medikeeper

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import ddwucom.mobile.medikeeper.data.pill.PillDao
import ddwucom.mobile.medikeeper.data.pill.PillDatabase
import ddwucom.mobile.medikeeper.databinding.ActivityRvlistBinding

class RvlistActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityRvlistBinding.inflate(layoutInflater)
    }

    private lateinit var pillDao: PillDao
    private lateinit var adapter: PillAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 뒤로가기 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // RecyclerView 초기화
        binding.rvListContainer.layoutManager = LinearLayoutManager(this)
        adapter = PillAdapter(emptyList()) // 초기 데이터를 빈 리스트로 설정
        binding.rvListContainer.adapter = adapter

        // 데이터베이스 초기화
        val pillDB = PillDatabase.getDatabase(this)
        pillDao = pillDB.pillDao()

        // LiveData 관찰하여 데이터 업데이트
        pillDao.getAllPills().observe(this) { pills ->
            Log.d("RvlistActivity", "Pills fetched: ${pills.size}")
            adapter.updateData(pills) // 어댑터에 데이터 업데이트
        }
    }
}
