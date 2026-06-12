package com.ebody.bip.core.data.remote.firebase

object FirebaseConstants {
    // Collections
    const val USERS_COLLECTION = "users"
    const val MEDICATIONS_COLLECTION = "medications"
    const val ALARMS_COLLECTION = "alarms"
    const val SYMPTOMS_COLLECTION = "symptoms"
    const val EMERGENCY_CONTACTS_COLLECTION = "emergency_contacts"

    // Subcollections
    const val USER_MEDICATIONS_SUBCOLLECTION = "medications"
    const val USER_ALARMS_SUBCOLLECTION = "alarms"
    const val USER_SYMPTOMS_SUBCOLLECTION = "symptoms"

    // Document fields
    const val FIELD_USER_ID = "userId"
    const val FIELD_EMAIL = "email"
    const val FIELD_DISPLAY_NAME = "displayName"
    const val FIELD_CREATED_AT = "createdAt"
    const val FIELD_UPDATED_AT = "updatedAt"
    const val FIELD_IS_ACTIVE = "isActive"
}