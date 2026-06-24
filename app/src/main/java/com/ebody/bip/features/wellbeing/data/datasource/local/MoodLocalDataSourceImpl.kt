package com.ebody.bip.features.wellbeing.data.datasource.local

import com.ebody.bip.features.wellbeing.data.model.MoodEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MoodLocalDataSourceImpl @Inject constructor(
    private val moodDao: MoodDao
) : MoodLocalDataSource {

    override suspend fun insertMood(mood: MoodEntity) {
        moodDao.insertMood(mood)
    }

    override fun getMoodHistory(): Flow<List<MoodEntity>> {
        return moodDao.getMoodHistory()
    }
}