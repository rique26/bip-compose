package com.ebody.bip.features.wellbeing.data.datasource.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ebody.bip.core.database.BipDatabase
import com.ebody.bip.features.wellbeing.data.model.MoodEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MoodLocalDataSourceImplTest {

    private lateinit var database: BipDatabase
    private lateinit var moodDao: MoodDao
    private lateinit var dataSource: MoodLocalDataSourceImpl

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Cria um banco temporário isolado na memória
        database = Room.inMemoryDatabaseBuilder(context, BipDatabase::class.java)
            .allowMainThreadQueries() // Permite queries na main thread apenas para simplificar o teste
            .build()

        moodDao = database.moodDao()
        dataSource = MoodLocalDataSourceImpl(moodDao)
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertMood_deve_salvar_o_humor_corretamente_e_retornar_o_id_valido() = runTest {
        val entity = MoodEntity(
            id = 0L,
            level = 3,
            notes = "Sem sintomas hoje",
            timestamp = "2026-07-01T10:00:00",
            riskLevel = "ESTAVEL",
            aiInstruction = "Continue monitorando"
        )

        val generatedId = dataSource.insertMood(entity)

        Assert.assertTrue(generatedId > 0)

        // Coleta o primeiro histórico emitido pelo Flow
        val history = dataSource.getMoodHistory().first()
        Assert.assertEquals(1, history.size)
        Assert.assertEquals("Sem sintomas hoje", history.first().notes)
    }

    @Test
    fun getMoodsBetween_deve_filtrar_os_registros_corretamente_dentro_do_periodo() = runTest {
        // Três registros em datas diferentes
        val mood1 = MoodEntity(
            id = 1L,
            level = 1,
            notes = "Ontem",
            timestamp = "2026-06-30T15:00:00",
            riskLevel = "ALERTA",
            aiInstruction = ""
        )
        val mood2 = MoodEntity(
            id = 2L,
            level = 3,
            notes = "Hoje de manhã",
            timestamp = "2026-07-01T08:00:00",
            riskLevel = "ESTAVEL",
            aiInstruction = ""
        )
        val mood3 = MoodEntity(
            id = 3L,
            level = 2,
            notes = "Amanhã",
            timestamp = "2026-07-02T12:00:00",
            riskLevel = "ESTAVEL",
            aiInstruction = ""
        )

        dataSource.insertMoods(listOf(mood1, mood2, mood3))

        // Filtra buscando apenas o dia (01 de Julho)
        val startPeriod = "2026-07-01T00:00:00"
        val endPeriod = "2026-07-01T23:59:59"
        val filteredList = dataSource.getMoodsBetween(startPeriod, endPeriod)

        // Apenas o mood2 deve vir no resultado da query BETWEEN
        Assert.assertEquals(1, filteredList.size)
        Assert.assertEquals(2L, filteredList.first().id)
        Assert.assertEquals("Hoje de manhã", filteredList.first().notes)
    }
}