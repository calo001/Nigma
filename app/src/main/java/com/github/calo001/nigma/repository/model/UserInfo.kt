package com.github.calo001.nigma.repository.model

import io.appwrite.models.User

data class UserInfo(
    val id: String,
    val email: String,
    val username: String,
    val imageProfile: ByteArray?,
    val imageProfileFileId: String,
) {
    companion object {
        fun fromUser(user: User) = UserInfo(
            id = user.id,
            email = user.email,
            username = user.name,
            imageProfile = null,
            imageProfileFileId = user.prefs.data.getOrDefault("image_profile", "") as String,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserInfo

        if (id != other.id) return false
        if (email != other.email) return false
        if (username != other.username) return false
        if (!imageProfile.contentEquals(other.imageProfile)) return false
        if (imageProfileFileId != other.imageProfileFileId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + imageProfile.contentHashCode()
        result = 31 * result + imageProfileFileId.hashCode()
        return result
    }
}