package com.ebody.bip.core.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface NavGraph
sealed interface NavRoute

// --- GRUPOS DE NAVEGAÇÃO (Nested Graphs) ---

@Serializable object AuthGraph : NavGraph
@Serializable object HomeGraph : NavGraph
@Serializable object ScheduleGraph : NavGraph
@Serializable object EmergencyGraph : NavGraph
@Serializable object SymptomsGraph : NavGraph
@Serializable object ProfileGraph : NavGraph

// --- DESTINOS INDIVIDUAIS (Screens) ---

// Auth
@Serializable object Login : NavRoute
@Serializable object Register : NavRoute
@Serializable object RegisterStep1 : NavRoute
@Serializable object RegisterStep2 : NavRoute
@Serializable object RegisterStep3 : NavRoute
@Serializable object ResetPassword : NavRoute

// Home
@Serializable object Home : NavRoute

// Schedule (Medicamentos)
@Serializable object MedicationList : NavRoute
@Serializable object MedicationSearch : NavRoute
@Serializable object AddMedication : NavRoute