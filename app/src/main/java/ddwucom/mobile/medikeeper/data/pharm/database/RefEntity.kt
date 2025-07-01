package ddwucom.mobile.medikeeper.data.pharm.database
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="ref_table")
class RefEntity (
    @PrimaryKey(autoGenerate = true)
    val _id : Int,
    val pharmacyName: String,
    val address: String,
    val phoneNumber: String,
    val latitude : String,
    val longitude : String,
    val dong: String
)
