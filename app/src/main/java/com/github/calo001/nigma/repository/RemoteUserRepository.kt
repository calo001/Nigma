package com.github.calo001.nigma.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.calo001.nigma.repository.model.UserInfo
import com.github.calo001.nigma.ui.model.PuzzleView
import com.github.calo001.nigma.util.toFile
import com.github.calo001.nigma.viewModel.Puzzle
import dagger.hilt.android.qualifiers.ApplicationContext
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.Document
import io.appwrite.models.Session
import io.appwrite.models.User
import io.appwrite.services.Account
import io.appwrite.services.Database
import io.appwrite.services.Realtime
import io.appwrite.services.Storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import java.lang.NullPointerException
import java.nio.ByteBuffer
import javax.inject.Inject

class RemoteUserRepository @Inject constructor(
    private val account: Account,
    private val storage: Storage,
    private val database: Database,
    private val realtime: Realtime,
    @ApplicationContext private val context: Context,
) {
    suspend fun createUser(username: String, email: String, password: String): Result<User> {
        return try {
            val user = account.create("unique()", email, password, username)
            Result.success(user)
        } catch (e: AppwriteException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Session> {
        return try {
            val result = account.createSession(email, password)
            Result.success(result)
        } catch (e: AppwriteException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentSession(): Result<UserInfo> {
        return try {
            val currentSession = account.get()
            val imgByteArray = (currentSession.prefs.data["image_profile"] as? String)?.let { fileId ->
                storage.getFileDownload("profile-images", fileId)
            }
            Result.success(UserInfo.fromUser(currentSession).copy(imageProfile = imgByteArray))
        } catch (e: AppwriteException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            account.deleteSessions()
            Result.success(Unit)
        } catch (e: AppwriteException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadPuzzleImage(
        puzzle: Puzzle
    ): Result<io.appwrite.models.File> {
        return try {
            puzzle.imgBitmap?.toFile(puzzle.fileName, context)?.let { file ->
                Result.success(
                    storage.createFile(
                        bucketId = "puzzles-imgs",
                        fileId = "unique()",
                        file = file,
                    )
                )
            } ?: run {
                Result.failure(NullPointerException())
            }
        } catch (e: AppwriteException) {
                Result.failure(e)
        } catch (e: Exception) {
                Result.failure(e)
        }
    }

    suspend fun savePuzzle(puzzle: Puzzle): Result<Document> {
        return try {
            Result.success(
                database.createDocument(
                    collectionId = "puzzle-collection",
                    documentId = "unique()",
                    data = mapOf(
                        "name" to puzzle.name,
                        "description" to puzzle.description,
                        "img_file_id" to puzzle.fileId,
                        "puzzles_completed" to listOf<String>()
                    )
                )
            )
        } catch (e: AppwriteException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun subscribeRealtime() = callbackFlow {
        val subscription = realtime.subscribe("collections.puzzle-collection.documents") { response ->
            trySend(response.timestamp.toString())
        }
        awaitClose {
            subscription.close()
        }
    }

    suspend fun getAllPuzzles(): Result<List<PuzzleView>> {
        return try {
            val list = database.listDocuments("puzzle-collection").documents
                .asReversed()
                .map { document ->
                    val bitArrayImg = storage.getFileDownload("puzzles-imgs",
                        document.data["img_file_id"] as String
                    )
                    val resolvedBy: List<String> = (document.data["puzzles_completed"] as List<*>).mapNotNull {
                        it.toString()
                    }
                    PuzzleView(
                        id = document.data["\$id"] as String,
                        username = "",
                        userImageProfileUrl = document.data["img_file_id"] as String,
                        puzzleImage = bitArrayImg,
                        gridSize = 3,
                        puzzleName = document.data["name"] as String,
                        resolvedBy = resolvedBy,
                        description = document.data["description"] as String,
                    )
                }
            Result.success(list)
        } catch (e: AppwriteException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePuzzleItem(puzzle: PuzzleView, userId: String): Result<Any> {
        return try {
            val lastUpdatedPuzzle = database.getDocument(
                collectionId = "puzzle-collection",
                documentId = puzzle.id,
            )
            val resolvedBy: List<String> = (lastUpdatedPuzzle.data["puzzles_completed"] as List<*>).mapNotNull {
                it.toString()
            }
            database.updateDocument(
                collectionId = "puzzle-collection",
                documentId = puzzle.id,
                data = mapOf(
                    "name" to puzzle.puzzleName,
                    "description" to puzzle.description,
                    "img_file_id" to puzzle.userImageProfileUrl,
                    "puzzles_completed" to resolvedBy + listOf(userId)
                )
            )
            Result.success(Unit)
        } catch (e: AppwriteException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadImageProfile(bitmap: Bitmap, userInfo: UserInfo): Result<io.appwrite.models.File> {
        return try {
            bitmap.toFile(userInfo.id, context)?.let { file ->
                Result.success(
                    storage.createFile(
                        bucketId = "profile-images",
                        fileId = "unique()",
                        file = file,
                    )
                )
            } ?: run {
                Result.failure(NullPointerException())
            }
        } catch (e: AppwriteException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfileInfo(userInfo: UserInfo): Result<UserInfo> {
        return try {
            var currentAccount = account.get()

            if (currentAccount.name != userInfo.username) {
                currentAccount = account.updateName(userInfo.username)
            }
            if (currentAccount.prefs.data.getOrDefault("image_profile", "") != userInfo.imageProfileFileId) {
                val result = account.updatePrefs(mapOf("image_profile" to userInfo.imageProfileFileId))
                Result.success(
                    UserInfo.fromUser(result)
                )
            } else {
                Result.success(UserInfo.fromUser(currentAccount))
            }
        } catch (e: AppwriteException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}