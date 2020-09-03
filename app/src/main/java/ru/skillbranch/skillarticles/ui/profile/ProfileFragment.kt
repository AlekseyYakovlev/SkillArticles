package ru.skillbranch.skillarticles.ui.profile

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.ui.base.Binding
import ru.skillbranch.skillarticles.ui.delegates.RenderProp
import ru.skillbranch.skillarticles.ui.dialogs.AvatarActionsDialog
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.profile.PendingAction
import ru.skillbranch.skillarticles.viewmodels.profile.ProfileState
import ru.skillbranch.skillarticles.viewmodels.profile.ProfileViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : BaseFragment<ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()
    override val layout: Int = R.layout.fragment_profile
    override val binding: ProfileBinding by lazy { ProfileBinding() }

    private val permissionResultCallback =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val permissionsResult = result.mapValues { (permission, isGranted) ->
                if (isGranted) {
                    true to true
                } else {
                    false to ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        permission
                    )
                }
            }
            viewModel.handlePermission(permissionsResult)
        }

    private val galleryResultCallback =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            result?.let {
                val inputStream = requireContext().contentResolver.openInputStream(it)
                viewModel.handleUploadedPhoto(inputStream)
            }
        }

    private val settingsResultCallback =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            //Do something
        }

    private val cameraResultCallback =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result: Boolean ->
            val (payload) = binding.pendingAction as PendingAction.CameraAction

            if (result) {
                requireContext().contentResolver.openInputStream(payload)
                    ?.let { viewModel.handleUploadedPhoto(it) }
            } else {
                removeTempUri(payload)
            }
        }

    private val editPhotoResultCallback =
        registerForActivityResult(EditImageContract()) { result ->
            if (result != null) {
                val inputStream = requireContext().contentResolver.openInputStream(result)
                viewModel.handleUploadedPhoto(inputStream)
            } else {
                val (payload) = binding.pendingAction as PendingAction.EditAction
                removeTempUri(payload.second)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(AvatarActionsDialog.AVATAR_ACTIONS_KEY) { _, bundle ->
            when (bundle[AvatarActionsDialog.SELECT_ACTION_KEY] as String) {
                AvatarActionsDialog.CAMERA_KEY -> viewModel.handleCameraAction(prepareTempUri())
                AvatarActionsDialog.GALLERY_KEY -> viewModel.handleGalleryAction()
                AvatarActionsDialog.DELETE_KEY -> viewModel.handleDeleteAction()
                AvatarActionsDialog.EDIT_KEY -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val sourceFile =
                            Glide.with(requireActivity()).asFile().load(binding.avatar).submit()
                                .get()
                        val sourceUri = FileProvider.getUriForFile(
                            requireContext(),
                            "${requireContext().packageName}.provider",
                            sourceFile
                        )

                        withContext(Dispatchers.Main) {
                            viewModel.handleEditAction(sourceUri, prepareTempUri())
                            // viewModel.handleEditAction(sourceUri, sourceUri)
                        }
                    }
                }
            }

        }
    }


    override fun setupViews() {
        iv_avatar.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionNavProfileToDialogAvatarActions(binding.avatar.isNotBlank())
            viewModel.navigate(NavigationCommand.To(action.actionId, action.arguments))
        }

        viewModel.observePermissions(viewLifecycleOwner) {
            permissionResultCallback.launch(it.toTypedArray())
        }

        viewModel.observeActivityResults(viewLifecycleOwner) {
            when (it) {
                is PendingAction.GalleryAction -> galleryResultCallback.launch(it.payload)
                is PendingAction.SettingsAction -> settingsResultCallback.launch(it.payload)
                is PendingAction.CameraAction -> cameraResultCallback.launch(it.payload)
                is PendingAction.EditAction -> editPhotoResultCallback.launch(it.payload)
            }
        }

    }

    private fun updateAvatar(avatarUrl: String) {
        if (avatarUrl.isBlank()) {
            Glide.with(root)
                .load(R.drawable.ic_avatar)
                .into(iv_avatar)
        } else {
            Glide.with(root)
                .load(avatarUrl)
                .placeholder(R.drawable.ic_avatar)
                .apply(RequestOptions.circleCropTransform())
                .into(iv_avatar)
        }
    }

    private fun prepareTempUri(): Uri {
        val timestamp = SimpleDateFormat("HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val tempFile = File.createTempFile(
            "JPEG_${timestamp}",
            ".jpg",
            storageDir
        )

        val contentUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            tempFile
        )

//        Log.d("ProfileFragment", "file uri(toUri): ${tempFile.toUri()}")
//        Log.d("ProfileFragment", "content uri: $contentUri")

        return contentUri
    }

    private fun removeTempUri(uri: Uri) {
        requireContext().contentResolver.delete(uri, null, null)
    }

    inner class ProfileBinding() : Binding() {
        var pendingAction: PendingAction? = null

        var avatar by RenderProp("") {
            updateAvatar(it)
        }

        var name by RenderProp("") {
            tv_name.text = it
        }
        var about by RenderProp("") {
            tv_about.text = it
        }
        var rating by RenderProp(0) {
            tv_rating.text = "Rating: $it"
        }
        var respect by RenderProp(0) {
            tv_respect.text = "Respect: $it"
        }

        override fun bind(data: IViewModelState) {
            data as ProfileState
            data.avatar?.let { avatar = it }
            data.name?.let { name = it }
            data.about?.let { about = it }
            data.rating.let { rating = it }
            data.respect.let { respect = it }
            data.pendingAction?.let { pendingAction = it }
        }
    }
}