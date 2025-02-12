package com.example.dress_den.util

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.util.*

object FirebaseUtils {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    // Authentication
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun isUserSignedIn(): Boolean = auth.currentUser != null

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Firestore Operations
    suspend fun <T> setDocument(
        collection: String,
        documentId: String,
        data: T
    ): Result<Unit> {
        return try {
            firestore.collection(collection)
                .document(documentId)
                .set(data!!)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun <T> getDocument(
        collection: String,
        documentId: String,
        clazz: Class<T>
    ): Result<T> {
        return try {
            val document = firestore.collection(collection)
                .document(documentId)
                .get()
                .await()
            
            if (document.exists()) {
                Result.success(document.toObject(clazz)!!)
            } else {
                Result.failure(NoSuchElementException("Document not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun <T> getCollection(
        collection: String,
        clazz: Class<T>
    ): Result<List<T>> {
        return try {
            val querySnapshot = firestore.collection(collection)
                .get()
                .await()
            
            val items = querySnapshot.documents.mapNotNull { it.toObject(clazz) }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteDocument(
        collection: String,
        documentId: String
    ): Result<Unit> {
        return try {
            firestore.collection(collection)
                .document(documentId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Storage Operations
    suspend fun uploadImage(
        imageUri: Uri,
        path: String
    ): Result<String> {
        return try {
            val filename = "${UUID.randomUUID()}.jpg"
            val ref = storage.reference.child("$path/$filename")
            val uploadTask = ref.putFile(imageUri).await()
            val downloadUrl = ref.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteImage(url: String): Result<Unit> {
        return try {
            val ref = storage.getReferenceFromUrl(url)
            ref.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // User Profile Operations
    suspend fun updateUserProfile(
        displayName: String? = null,
        photoUrl: String? = null
    ): Result<Unit> {
        return try {
            val user = getCurrentUser() ?: throw IllegalStateException("No user signed in")
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder().apply {
                displayName?.let { setDisplayName(it) }
                photoUrl?.let { setPhotoUri(Uri.parse(it)) }
            }.build()

            user.updateProfile(profileUpdates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Batch Operations
    suspend fun performBatchOperation(operations: suspend (FirebaseFirestore) -> Unit): Result<Unit> {
        return try {
            firestore.runBatch { batch ->
                operations(firestore)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Transaction Operations
    suspend fun <T> performTransaction(transaction: suspend (FirebaseFirestore) -> T): Result<T> {
        return try {
            val result = firestore.runTransaction { transaction ->
                transaction(firestore)
            }.await()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Query Operations
    fun getStorageReference(path: String): StorageReference {
        return storage.reference.child(path)
    }

    fun getCollectionReference(collection: String) = firestore.collection(collection)

    fun getDocumentReference(collection: String, documentId: String) =
        firestore.collection(collection).document(documentId)
}
