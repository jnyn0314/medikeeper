package ddwucom.mobile.medikeeper.data.pill

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "pill_table")
data class Pill (
    @PrimaryKey(autoGenerate = true)
    val _id : Int = 0,

    var name : String,

    var disease : String,

    @ColumnInfo(name = "morning")
    var morning : Boolean = false,

    @ColumnInfo(name = "lunch")
    var lunch : Boolean = false,

    @ColumnInfo(name = "dinner")
    var dinner : Boolean = false,

    @ColumnInfo(name = "beforeBed")
    var beforeBed : Boolean = false,

    var startDate : Date,

    var endDate : Date,

    var memo : String,
)