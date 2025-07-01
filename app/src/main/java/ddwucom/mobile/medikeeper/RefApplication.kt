package ddwucom.mobile.medikeeper

import android.app.Application
import ddwucom.mobile.medikeeper.data.pharm.RefRepository
import ddwucom.mobile.medikeeper.data.pharm.database.RefDatabase
import ddwucom.mobile.medikeeper.data.pharm.network.RefServices

class RefApplication : Application() {

    // Room Database DAO 초기화
    val refDao by lazy {
        RefDatabase.getDatabase(this).refDao()
    }

    // Retrofit 서비스 초기화
    val refServices by lazy {
        RefServices.create(getString(R.string.url)) // Retrofit을 통한 RefServices 생성
    }

    // Repository 초기화
    val refRepository by lazy {
        RefRepository(refDao, refServices)
    }
}
