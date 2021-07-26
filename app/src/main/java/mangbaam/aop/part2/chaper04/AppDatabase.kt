package mangbaam.aop.part2.chaper04

import androidx.room.Database
import androidx.room.RoomDatabase
import mangbaam.aop.part2.chaper04.dao.HistoryDao
import mangbaam.aop.part2.chaper04.model.History

@Database(entities = [History::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao() : HistoryDao
}