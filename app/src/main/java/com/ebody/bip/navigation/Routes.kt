package com.ebody.bip.navigation

import kotlinx.serialization.Serializable

// Interface selada comum para todos os destinos e grafos
sealed interface NavigationRoute

// --- GRUPOS DE NAVEGAÇÃO (Nested Graphs) ---
sealed interface GraphRoute : NavigationRoute

@Serializable object AuthGraph : GraphRoute
@Serializable object HomeGraph : GraphRoute
@Serializable object ScheduleGraph : GraphRoute
@Serializable object EmergencyGraph : GraphRoute
@Serializable object SymptomsGraph : GraphRoute
@Serializable object ProfileGraph : GraphRoute
@Serializable object WellbeingGraph : GraphRoute

// --- DESTINOS INDIVIDUAIS (Screens) ---
sealed interface ScreenRoute : NavigationRoute

// Auth
@Serializable object Login : ScreenRoute
@Serializable object Register : ScreenRoute
@Serializable object RegistrationSuccess : ScreenRoute
// Home
@Serializable object Home : ScreenRoute

// Schedule
@Serializable object MedicationSelection : ScreenRoute
@Serializable data class MedicationSchedule(val medicationIds: List<Long>)

// Emergency
@Serializable object Emergency : ScreenRoute
@Serializable object EmergencyContacts : ScreenRoute

// Wellbeing
@Serializable object MoodRoute : ScreenRoute
@Serializable object HistoryRoute : ScreenRoute
@Serializable object AnalyticsRoute : ScreenRoute