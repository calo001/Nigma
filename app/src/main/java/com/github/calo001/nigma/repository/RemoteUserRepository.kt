package com.github.calo001.nigma.repository

import com.github.calo001.nigma.util.toFile
import com.github.calo001.nigma.viewModel.Puzzle
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.Document
import io.appwrite.models.Session
import io.appwrite.models.User
import io.appwrite.services.Account
import io.appwrite.services.Database
import io.appwrite.services.Storage
import java.io.File
import java.lang.NullPointerException
import javax.inject.Inject

class RemoteUserRepository @Inject constructor(
    private val account: Account,
    private val storage: Storage,
    private val database: Database,
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

    suspend fun getCurrentSession(): Result<User> {
        return try {
            val currentSession = account.get()
            Result.success(currentSession)
        } catch (e: AppwriteException) {
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
            puzzle.imgBitmap?.toFile(puzzle.fileName)?.let { file ->
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
}