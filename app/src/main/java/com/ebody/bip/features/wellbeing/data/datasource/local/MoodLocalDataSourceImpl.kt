package com.ebody.bip.features.wellbeing.data.datasource.local

import com.ebody.bip.features.wellbeing.data.mapper.toEntity
import com.ebody.bip.features.wellbeing.data.model.MoodEntity
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MoodLocalDataSourceImpl @Inject constructor(
    private val moodDao: MoodDao
) : MoodLocalDataSource {

    override suspend fun insertMood(mood: MoodEntry): Long {
        return moodDao.insertMood(mood.toEntity())
    }

    override suspend fun insertMoods(moods: List<MoodEntity>) {
        moodDao.insertMoods(moods)
    }

    override fun getMoodHistory(): Flow<List<MoodEntity>> {
        return moodDao.getMoodHistory()
    }

    override suspend fun getMoodsBetween(start: String, end: String): List<MoodEntity> {
        return moodDao.getMoodsBetween(start, end)
    }
    override suspend fun getLastMoods(limit: Int): List<MoodEntity> {
        return moodDao.getLastMoods(limit)
    }
}