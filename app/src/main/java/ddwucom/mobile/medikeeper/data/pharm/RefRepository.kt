package ddwucom.mobile.medikeeper.data.pharm
import ddwucom.mobile.medikeeper.data.pharm.database.RefDao
import ddwucom.mobile.medikeeper.data.pharm.database.RefEntity
import ddwucom.mobile.medikeeper.data.pharm.network.ModelProperties
import ddwucom.mobile.medikeeper.data.pharm.network.RefServices
import kotlinx.coroutines.flow.Flow

class RefRepository (private val refDao : RefDao, private val refServices: RefServices) {
    val allRefs : Flow<List<RefEntity>> = refDao.getPharamacy()

    suspend fun getPharmacy(key : String) : List<ModelProperties>? {
        return refServices.getPharmacy(key)
    }

    suspend fun fetchAndSavePharmacies(key: String) {
        val response = refServices.getPharmacy(key)
        response?.let { pharmacies ->
            val entities = pharmacies.map {
                RefEntity(
                    _id = 0, // Room에서 auto-generate 설정
                    pharmacyName = it.pharmacyName,
                    address = it.address,
                    phoneNumber = it.phoneNumber,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    dong = it.dong
                )
            }
            refDao.insertPharmacy(entities)
        }
    }
}