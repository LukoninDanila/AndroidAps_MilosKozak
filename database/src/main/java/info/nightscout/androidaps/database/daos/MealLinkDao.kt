package info.nightscout.androidaps.database.daos

import androidx.room.Dao
import androidx.room.Query
import info.nightscout.androidaps.database.TABLE_MEAL_LINKS
import info.nightscout.androidaps.database.entities.links.MealLink

@Suppress("FunctionName")
@Dao
abstract class MealLinkDao : BaseDao<MealLink>() {

    @Query("SELECT * FROM $TABLE_MEAL_LINKS WHERE id = :id")
    abstract override fun findById(id: Long): MealLink?

    @Query("DELETE FROM $TABLE_MEAL_LINKS")
    abstract override fun deleteAllEntries()

    @Query("SELECT * FROM $TABLE_MEAL_LINKS WHERE bolusId = :bolusId")
    abstract fun findByBolusId(bolusId: Long): MealLink?

    @Query("SELECT * FROM $TABLE_MEAL_LINKS WHERE carbsId = :carbsId")
    abstract fun findByCarbsId(carbsId: Long): MealLink?
}