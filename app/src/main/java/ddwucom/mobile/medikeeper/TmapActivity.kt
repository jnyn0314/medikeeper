package ddwucom.mobile.medikeeper

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skt.Tmap.TMapMarkerItem
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapView
import ddwucom.mobile.medikeeper.data.pharm.RefRepository
import ddwucom.mobile.medikeeper.data.pharm.database.RefDatabase
import ddwucom.mobile.medikeeper.data.pharm.network.ModelProperties
import ddwucom.mobile.medikeeper.data.pharm.network.RefServices
import ddwucom.mobile.medikeeper.data.pharm_ui.PharmacyAdapter
import ddwucom.mobile.medikeeper.data.pharm_ui.RefViewModel
import ddwucom.mobile.medikeeper.data.pharm_ui.RefViewModelFactory

class TmapActivity : AppCompatActivity() {

    private lateinit var tmapView: TMapView
    private lateinit var linearLayoutTmap: LinearLayout
    private lateinit var refViewModel: RefViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var adapter: PharmacyAdapter

    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private val TAG = "TmapActivity"

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheetLayout: View

    private val markerMap = HashMap<String, TMapMarkerItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tmap)

        // Toolbar 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 뒤로가기 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish() // 뒤로가기 버튼 클릭 시 현재 액티비티 종료
        }

        val fabAddMedicine: FloatingActionButton = findViewById(R.id.fab_add_medicine)
        fabAddMedicine.setOnClickListener {
            val intent = Intent(this, AddmedicineActivity::class.java)
            startActivity(intent)
        }


        if (checkAndRequestPermissions()) {
            initializeComponents()
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissionsNeeded = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET
        )
        val permissionsToRequest = permissionsNeeded.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        ) {
            initializeComponents()
        } else {
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializeComponents() {
        initializeTMap()
        setupBottomSheet()
        setupRecyclerView()
        loadPharmacyData()
    }

    private fun initializeTMap() {
        linearLayoutTmap = findViewById(R.id.linearLayoutTmap)
        tmapView = TMapView(this)
        tmapView.setSKTMapApiKey(getString(R.string.tmap_api_key))
        linearLayoutTmap.addView(tmapView)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                location?.let {
                    tmapView.setLocationPoint(it.longitude, it.latitude)
                    tmapView.setCenterPoint(it.longitude, it.latitude)
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "위치 요청 실패: ${e.message}")
            }
        }
        val refDao = RefDatabase.getDatabase(applicationContext).refDao()
        val refServices = RefServices.create(getString(R.string.url))
        val refRepository = RefRepository(refDao, refServices)
        val factory = RefViewModelFactory(refRepository)
        refViewModel = ViewModelProvider(this, factory)[RefViewModel::class.java]

        refViewModel.pharmacies.observe(this) { pharmacyList ->
            pharmacyList?.let {
                addMarkersToMap(it)
            }
        }
        val serviceKey = getString(R.string.serviceKey)
        refViewModel.getPharmacies(serviceKey)
    }

    private fun setupBottomSheet() {
        bottomSheetLayout = findViewById(R.id.bottom_sheet_layout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)

        bottomSheetBehavior.peekHeight = 100
        bottomSheetBehavior.isHideable = false

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> Log.d(TAG, "Bottom Sheet Expanded")
                    BottomSheetBehavior.STATE_COLLAPSED -> Log.d(TAG, "Bottom Sheet Collapsed")
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d(TAG, "Slide Offset: $slideOffset")
            }
        })
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewPharmacy)

        // 어댑터 초기화 시 클릭 이벤트 처리
        adapter = PharmacyAdapter(emptyList()) { pharmacy ->
            // 약국 클릭 시 실행되는 동작
            val markerItem = markerMap[pharmacy.pharmacyName]
            if (markerItem != null) {
                tmapView.setCenterPoint(
                    markerItem.tMapPoint.longitude,
                    markerItem.tMapPoint.latitude,
                    true
                )
                tmapView.bringMarkerToFront(markerItem) // 마커를 최상위로 가져옴
                markerItem.canShowCallout = true // Callout 활성화
                markerItem.calloutTitle = pharmacy.pharmacyName
                markerItem.calloutSubTitle = pharmacy.address
                tmapView.addMarkerItem(pharmacy.pharmacyName, markerItem) // 화면에 marker 업데이트
            } else {
                Log.e(TAG, "마커를 찾을 수 없습니다: ${pharmacy.pharmacyName}")
            }
        }


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        Log.d(TAG, "RecyclerView 설정 완료")
    }

    private fun moveToPharmacyLocation(pharmacy: ModelProperties) {
        val latitude = pharmacy.latitude.toDouble()
        val longitude = pharmacy.longitude.toDouble()
        val tMapPoint = TMapPoint(latitude, longitude)

        // 지도 중심 이동
        tmapView.setCenterPoint(longitude, latitude)

        // HashMap에서 마커를 찾기
        val markerItem = markerMap[pharmacy.pharmacyName]
        if (markerItem != null) {
            tmapView.bringMarkerToFront(markerItem)
        } else {
            Log.e(TAG, "마커를 찾을 수 없습니다: ${pharmacy.pharmacyName}")
        }
    }
    private fun loadPharmacyData() {
        val serviceKey = getString(R.string.serviceKey)

        refViewModel.getPharmacies(serviceKey)

        refViewModel.pharmacies.observe(this) { pharmacyList ->
            pharmacyList?.let {
                Log.d(TAG, "RecyclerView에 데이터 전달: ${it.size}개")
                adapter.setData(it)
            }
        }
    }

    private fun addMarkersToMap(pharmacyList: List<ModelProperties>) {
        for (pharmacy in pharmacyList) {
            val markerItem = TMapMarkerItem()
            val tMapPoint = TMapPoint(pharmacy.latitude.toDouble(), pharmacy.longitude.toDouble())
            markerItem.tMapPoint = tMapPoint
            markerItem.name = pharmacy.pharmacyName
            markerItem.id = pharmacy.pharmacyName
            markerItem.canShowCallout = true
            markerItem.calloutTitle = pharmacy.pharmacyName
            markerItem.calloutSubTitle = pharmacy.address
            tmapView.addMarkerItem(pharmacy.pharmacyName, markerItem)

            markerMap[pharmacy.pharmacyName] = markerItem
        }
    }
}
