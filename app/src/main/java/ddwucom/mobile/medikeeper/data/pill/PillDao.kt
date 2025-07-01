package ddwucom.mobile.medikeeper.data.pill// 20220803 컴퓨터학과 정여진

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PillDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPill(pill: Pill)

    @Query("SELECT * FROM pill_table")
    fun getAllPills(): LiveData<List<Pill>>

    @Query("UPDATE pill_table SET disease = :disease WHERE _id = :id")
    fun updatePillDisease(id: Int, disease: String)
}