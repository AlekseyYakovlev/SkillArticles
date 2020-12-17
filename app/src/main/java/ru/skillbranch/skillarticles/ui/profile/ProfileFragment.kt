package ru.skillbranch.skillarticles.ui.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
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

@AndroidEntryPoint
class ProfileFragment : BaseFragment<ProfileViewModel>() {

    private lateinit var resultRegistry: ActivityResultRegistry

    override val viewModel: ProfileViewModel by activityViewModels()
    override val layout: Int = R.layout.fragment_profile
    override val binding: ProfileBinding by lazy { ProfileBinding() }


    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<out String>>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var galleryLauncher: ActivityResultLauncher<String>
    private lateinit var editPhotoLauncher: ActivityResultLauncher<Pair<Uri, Uri>>
    private lateinit var settingsLauncher: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (!::resultRegistry.isInitialized) resultRegistry =
            requireActivity().activityResultRegistry

        permissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            resultRegistry,
            ::callbackPermissions
        )
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture(),
            resultRegistry,
            ::callbackCamera
        )
        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            resultRegistry,
            ::callbackGallery
        )
        editPhotoLauncher = registerForActivityResult(
            EditImageContract(resources.getString(R.string.profile_fragment__choose_app_to_edit_in)),
            resultRegistry,
            ::callbackEditPhoto
        )
        settingsLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            resultRegistry,
            ::callbackSettings
        )
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
                            Glide
                                .with(requireActivity())
                                .asFile()
                                .load(binding.avatar)
                                .submit()
                                .get()
                        val sourceUri = FileProvider.getUriForFile(
                            requireContext(),
                            "${requireContext().packageName}.provider",
                            sourceFile
                        )

                        withContext(Dispatchers.Main) {
                            viewModel.handleEditAction(sourceUri, prepareTempUri())
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
            permissionsLauncher.launch(it.toTypedArray())
        }

        viewModel.observeActivityResults(viewLifecycleOwner) {
            when (it) {
                is PendingAction.GalleryAction -> galleryLauncher.launch(it.payload)
                is PendingAction.SettingsAction -> settingsLauncher.launch(it.payload)
                is PendingAction.CameraAction -> cameraLauncher.launch(it.payload)
                is PendingAction.EditAction -> editPhotoLauncher.launch(it.payload)
            }
        }

        iv_exit.setOnClickListener { viewModel.handleLogout() }
    }

    private fun updateAvatar(avatarUrl: String) {
        if (avatarUrl.isBlank()) {
            Glide.with(this)
                .load(R.drawable.ic_avatar)
                .into(iv_avatar)
        } else {
            Glide.with(this)
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

        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            tempFile
        )
    }

    private fun removeTempUri(uri: Uri?) {
        uri ?: return
        requireContext().contentResolver.delete(uri, null, null)
    }

    private fun callbackPermissions(result: Map<String, Boolean>) {
        val permissionsResult = result.mapValues { (permission, isGranted) ->
            if (isGranted) {
                true to true
            } else {
                false to shouldShowRequestPermissionRationale(
                    requireActivity(),
                    permission
                )
            }
        }

        // remove temp file by uri if permissions are denied
        val arePermissionsGranted = !permissionsResult.values.map { it.first }.contains(false)
        if (!arePermissionsGranted) {
            val tempUri = when (val pendingAction = binding.pendingAction) {
                is PendingAction.CameraAction -> pendingAction.payload
                is PendingAction.EditAction -> pendingAction.payload.second
                else -> null
            }
            removeTempUri(tempUri)
        }

        viewModel.handlePermission(requireContext(), permissionsResult)
    }

    private fun callbackCamera(result: Boolean) {
        val (payload) = binding.pendingAction as PendingAction.CameraAction
        // if photo from camera is received, then upload it to a server
        if (result) {
            requireContext().contentResolver.openInputStream(payload)
                ?.let { viewModel.handleUploadPhoto(it) }
        } else {
            // else remove temp uri
            removeTempUri(payload)
        }
    }

    private fun callbackGallery(result: Uri?) {
        result?.let {
            val inputStream = requireContext().contentResolver.openInputStream(it)
            viewModel.handleUploadPhoto(inputStream)
        }
    }

    private fun callbackEditPhoto(result: Uri?) {
        if (result != null) {
            val inputStream = requireContext().contentResolver.openInputStream(result)
            viewModel.handleUploadPhoto(inputStream)
        } else {
            val (payload) = binding.pendingAction as PendingAction.EditAction
            removeTempUri(payload.second)
        }
    }

    private fun callbackSettings(result: ActivityResult) {
        viewModel.executePendingAction()
    }

    inner class ProfileBinding : Binding() {
        var pendingAction: PendingAction? = null

        var avatar by RenderProp("") {
            updateAvatar(it)
        }

        var name by RenderProp("") {
            tv_name.text = it
            tv_name.setOnClickListener {
                viewModel.handleProfileUpdateClick()
            }
        }
        var about by RenderProp("") {
            tv_about.text =
                if (it.isBlank()) resources.getString(R.string.profile_fragment__about_me)
                else it
            tv_about.setOnClickListener {
                viewModel.handleProfileUpdateClick()
            }
        }
        var rating by RenderProp(0) {
            tv_rating.text =
                requireContext().resources.getString(R.string.profile_fragment__rating, it)
        }
        var respect by RenderProp(0) {
            tv_respect.text =
                requireContext().resources.getString(R.string.profile_fragment__respect, it)
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