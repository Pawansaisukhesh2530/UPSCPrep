package com.example.upscprep.data.repository

import com.example.upscprep.data.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository class for handling authentication operations
 * Uses Firebase Authentication and Firestore
 */
class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    /**
     * Signs up a new user with email and password
     * Creates user account, updates display name, and stores user data in Firestore
     *
     * @param name User's full name
     * @param email User's email address
     * @param password User's password
     * @return Result<User> Success with User object or Failure with exception
     */
    suspend fun signUp(name: String, email: String, password: String): Result<User> {
        return try {
            // Create user account with Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Failed to create user account"))

            // Update display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // Create User object
            val user = User(
                uid = firebaseUser.uid,
                name = name,
                email = email,
                registrationDate = Timestamp.now()
            )

            // Store user data in Firestore
            usersCollection.document(firebaseUser.uid)
                .set(user.toMap())
                .await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logs in a user with email and password
     * Authenticates user and retrieves user data from Firestore
     *
     * @param email User's email address
     * @param password User's password
     * @return Result<User> Success with User object or Failure with exception
     */
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            // Authenticate user
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Login failed"))

            // Retrieve user data from Firestore
            val userDocument = usersCollection.document(firebaseUser.uid).get().await()

            if (!userDocument.exists()) {
                return Result.failure(Exception("User data not found"))
            }

            val user = User(
                uid = userDocument.getString("uid") ?: firebaseUser.uid,
                name = userDocument.getString("name") ?: firebaseUser.displayName ?: "",
                email = userDocument.getString("email") ?: firebaseUser.email ?: "",
                registrationDate = userDocument.getTimestamp("registrationDate") ?: Timestamp.now()
            )

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logs out the current user
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Gets the currently logged in user
     *
     * @return User object if logged in, null otherwise
     */
    suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null

        return try {
            val userDocument = usersCollection.document(firebaseUser.uid).get().await()

            if (!userDocument.exists()) {
                return null
            }

            User(
                uid = userDocument.getString("uid") ?: firebaseUser.uid,
                name = userDocument.getString("name") ?: firebaseUser.displayName ?: "",
                email = userDocument.getString("email") ?: firebaseUser.email ?: "",
                registrationDate = userDocument.getTimestamp("registrationDate") ?: Timestamp.now()
            )
        } catch (e: Exception) {
            null
        }
    }
}

