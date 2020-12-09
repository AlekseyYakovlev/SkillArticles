package ru.skillbranch.skillarticles.viewmodels.profile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import kotlinx.android.parcel.Parcelize
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.repositories.ProfileRepository
import ru.skillbranch.skillarticles.data.repositories.RootRepository
import ru.skillbranch.skillarticles.viewmodels.base.*
import java.io.InputStream


class ProfileViewModel @ViewModelInject constructor(
    @Assisted handle: SavedStateHandle,
    private val repository: ProfileRepository,
    private val rootRepository: RootRepository,
) : BaseViewModel<ProfileState>(handle, ProfileState()) {

    private val activityResults = MutableLiveData<Event<PendingAction>>()

    private val storagePermissions = listOf<String>(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    init {
        subscribeOnDataSource(repository.getProfile()) { profile, state ->
            profile?.run {
                state.copy(
                    avatar = avatar,
                    name = name,
                    about = about,
                    rating = rating,
                    respect = respect
                )
            }
        }
    }

    private fun startForResult(action: PendingAction) {
        activityResults.value = Event(action)
    }

    fun handlePermission(context: Context, permissionsResult: Map<String, Pair<Boolean, Boolean>>) {
        val arePermissionsGranted = !permissionsResult.values.map { it.first }.contains(false)
        val isPermissionRequestBlocked = permissionsResult.values.map { it.second }.contains(false)

        when {
            arePermissionsGranted -> executePendingAction()
            isPermissionRequestBlocked -> executeOpenSettings(context)
            else -> {
                val msg = Notify.ErrorMessage(
                    context.getString(R.string.profile_view_model__storage_permissions_required), //"Need permissions for storage"
                    context.getString(R.string.retry),
                ) { requestPermissions(storagePermissions) }
                notify(msg)
            }
        }
    }

    private fun executeOpenSettings(context: Context) {
        val errHandler = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:ru.skillbranch.skillarticles")
            }
            startForResult(PendingAction.SettingsAction(intent))
        }
        notify(
            Notify.ErrorMessage(
                context.getString(R.string.profile_view_model__storage_permissions_required),
                context.getString(R.string.profile_view_model__open_settings),
            ) { errHandler() }
        )
    }

    private fun executePendingAction() {
        currentState.pendingAction?.let { startForResult(it) }
    }

    fun handleUploadPhoto(inputStream: InputStream?) {
        inputStream ?: return

        launchSafely(null, { updateState { it.copy(pendingAction = null) } }) {
            val byteArray = inputStream.use { it.readBytes() }

            val requestFile = byteArray.toRequestBody("image/jpeg".toMediaType())
            val body = MultipartBody.Part.createFormData("avatar", "name.jpg", requestFile)
            repository.uploadAvatar(body)
        }
    }

    fun observeActivityResults(owner: LifecycleOwner, handle: (action: PendingAction) -> Unit) {
        activityResults.observe(owner, EventObserver { handle(it) })
    }

    fun handleCameraAction(uri: Uri) {
        val pendingAction = PendingAction.CameraAction(uri)
        updateState { it.copy(pendingAction = pendingAction) }
        requestPermissions(storagePermissions)
    }

    fun handleGalleryAction() {
        val pendingAction = PendingAction.GalleryAction("image/jpeg")
        updateState { it.copy(pendingAction = pendingAction) }
        requestPermissions(storagePermissions)
    }

    fun handleEditAction(source: Uri, destination: Uri) {
        val pendingAction = PendingAction.EditAction(source to destination)
        updateState { it.copy(pendingAction = pendingAction) }
        requestPermissions(storagePermissions)
    }

    fun handleDeleteAction() {
        launchSafely {
            repository.removeAvatar()
        }
    }

    fun handleLogout() {
        rootRepository.logout()
        navigate(NavigationCommand.StartLogin())
    }
}

data class ProfileState(
    val avatar: String? = null,
    val name: String? = null,
    val about: String? = null,
    val rating: Int = 0,
    val respect: Int = 0,
    val pendingAction: PendingAction? = null
) : IViewModelState {
    override fun save(outState: SavedStateHandle) {
        outState.set("pendingAction", pendingAction)
    }

    override fun restore(savedState: SavedStateHandle): IViewModelState {
        return copy(pendingAction = savedState["pendingAction"])
    }
}

sealed class PendingAction : Parcelable {
    abstract val payload: Any?

    @Parcelize
    data class GalleryAction(override val payload: String) : PendingAction()

    @Parcelize
    data class SettingsAction(override val payload: Intent) : PendingAction()

    @Parcelize
    data class CameraAction(override val payload: Uri) : PendingAction()


    data class EditAction(override val payload: Pair<Uri, Uri>) : PendingAction(), Parcelable {
        constructor(parcel: Parcel) : this(Uri.parse(parcel.readString()) to Uri.parse(parcel.readString()))

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(payload.first.toString())
            parcel.writeString(payload.second.toString())
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<EditAction> {
            override fun createFromParcel(parcel: Parcel): EditAction {
                return EditAction(parcel)
            }

            override fun newArray(size: Int): Array<EditAction?> {
                return arrayOfNulls(size)
            }
        }
    }
}