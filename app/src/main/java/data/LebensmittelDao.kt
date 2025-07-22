// Datei: data/LebensmittelDao.kt

package com.smartwellness.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.smartwellness.data.entities.Lebensmittel

@Dao
interface LebensmittelDao {
    @Insert
    suspend fun insertAll(lebensmittel: List<Lebensmittel>)

    @Query("DELETE FROM lebensmittel")
    suspend fun deleteAll()

    @Query("SELECT * FROM lebensmittel WHERE produkt LIKE '%' || :query || '%'")
    suspend fun searchByProdukt(query: String): List<Lebensmittel>
}