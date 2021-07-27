package mangbaam.aop.part2.chaper04.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class History(
    @PrimaryKey val uid: Int?,
    @ColumnInfo val expression: String?,
    @ColumnInfo val result: String?
)