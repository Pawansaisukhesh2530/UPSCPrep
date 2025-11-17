package com.example.upscprep.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Data class representing a User in the application
 * Compatible with Firestore for storing and retrieving user data
 */
data class User(
    @DocumentId
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val registrationDate: Timestamp = Timestamp.now()
) {
    // No-argument constructor required for Firestore deserialization
    constructor() : this("", "", "", Timestamp.now())

    /**
     * Converts User object to a Map for Firestore storage
     */
    fun toMap(): Map<String, Any> {
        return hashMapOf(
            "uid" to uid,
            "name" to name,
            "email" to email,
            "registrationDate" to registrationDate
        )
    }
}

