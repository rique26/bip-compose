package com.ebody.bip.features.wellbeing.data.repository

import com.ebody.bip.features.auth.domain.model.UserSession
import com.ebody.bip.features.auth.domain.repository.SessionManager
import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.wellbeing.data.datasource.local.MoodLocalDataSource
import com.ebody.bip.features.wellbeing.data.datasource.remote.MoodRemoteDataSource
import com.ebody.bip.features.wellbeing.domain.model.MoodEntry
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class MoodRepositoryImplTest {

    private val localDataSource: MoodLocalDataSource = mockk()
    private val remoteDataSource: MoodRemoteDataSource = mockk()
    private val sessionManager: SessionManager = mockk()
    private lateinit var repository: MoodRepositoryImpl

    @Before
    fun setUp() {
        repository = MoodRepositoryImpl(localDataSource, remoteDataSource, sessionManager)
    }

    @Test
    fun `saveMood deve salvar localmente e sincronizar com remoto se o usuario estiver logado`() = runTest {
        val inputEntry = MoodEntry(
            id = 0L,
            level = 2,
            notes = "Sem dores.",
            dateTime = LocalDateTime.now()
        )

        val fakeUserId = "user_ceara_123"
        val fakeGeneratedId = 99L

        // Simula o comportamento das extensões toEntity() se necessário, ou mocka o comportamento do datasource
        // Ensaia: O banco local insere a entidade e gera o ID 99
        coEvery { localDataSource.insertMood(any()) } returns fakeGeneratedId

        // Ensaia: O session manager retorna um usuário logado ativo
        val fakeSession = mockk<UserSession> {
            coEvery { userId } returns fakeUserId
        }
        coEvery { sessionManager.getUserSession() } returns flowOf(fakeSession)

        // Ensaia: O datasource remoto aceita a sincronização normalmente
        coEvery { remoteDataSource.syncMood(fakeUserId, any()) } returns Result.Success(Unit)

        // WHEN
        val result = repository.saveMood(inputEntry)

        // O id retornado deve ser o ID gerado pelo banco local (99)
        assertEquals(fakeGeneratedId, result.id)

        // Verifica se o fluxo foi respeitado na ordem
        coVerify(exactly = 1) { localDataSource.insertMood(any()) }
        coVerify(exactly = 1) { sessionManager.getUserSession() }
        coVerify(exactly = 1) { remoteDataSource.syncMood(fakeUserId, any()) }
    }

    @Test
    fun `saveMood deve salvar localmente mas NAO deve sincronizar se o usuario estiver deslogado`() = runTest {
        val inputEntry = MoodEntry(id = 0L, level = 3, notes = "Notas", dateTime = LocalDateTime.now())

        coEvery { localDataSource.insertMood(any()) } returns 1L
        // Simula que não há sessão ativa (retorna fluxo vazio ou nulo)
        coEvery { sessionManager.getUserSession() } returns flowOf(UserSession())

        val result = repository.saveMood(inputEntry)

        assertEquals(1L, result.id)

        // Garante que salvou no Room local...
        coVerify(exactly = 1) { localDataSource.insertMood(any()) }
        // ...mas NUNCA chamou o servidor remoto, prevenindo crashes de userId nulo
        coVerify(exactly = 0) { remoteDataSource.syncMood(any(), any()) }
    }
}