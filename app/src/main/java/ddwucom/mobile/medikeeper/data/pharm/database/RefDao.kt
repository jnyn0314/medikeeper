package ddwucom.mobile.medikeeper.data.pharm.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RefDao {
    @Query("SELECT _id ,pharmacyName, address, phoneNumber, latitude, longitude, dong FROM ref_table")
    fun getPharamacy() : Flow<List<RefEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPharmacy(pharmacyList: List<RefEntity>)
}

