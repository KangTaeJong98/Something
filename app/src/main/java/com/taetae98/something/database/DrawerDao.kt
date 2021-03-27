package com.taetae98.something.database

import androidx.room.Dao
import com.taetae98.something.base.BaseDao
import com.taetae98.something.dto.Drawer

@Dao
interface DrawerDao : BaseDao<Drawer>{
}