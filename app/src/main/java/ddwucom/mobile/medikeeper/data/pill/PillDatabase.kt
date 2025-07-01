package ddwucom.mobile.medikeeper.data.pill

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Pill::class], version = 1)
@TypeConverters(Converters::class)
abstract class PillDatabase : RoomDatabase(){
    abstract fun pillDao() : PillDao

    companion object{
        @Volatile
        private var INSTANCE : PillDatabase?= null

        fun getDatabase(context : Context) : PillDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext, PillDatabase::class.java, "pill_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}