package ru.skillbranch.skillarticles.ui.dialogs

import android.content.pm.PackageManager.FEATURE_CAMERA_ANY
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet.*
import ru.skillbranch.skillarticles.R

class AvatarActionsDialog : BottomSheetDialogFragment() {

    companion object {
        const val AVATAR_ACTIONS_KEY = "AVATAR_ACTIONS_KEY"
        const val SELECT_ACTION_KEY = "SELECT_ACTION_KEY"
        const val CAMERA_KEY = "CAMERA_KEY"
        const val GALLERY_KEY = "GALLERY_KEY"
        const val EDIT_KEY = "EDIT_KEY"
        const val DELETE_KEY = "DELETE_KEY"
    }

    private val args: AvatarActionsDialogArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hasCamera = requireContext().packageManager.hasSystemFeature(FEATURE_CAMERA_ANY)
        item_camera.isVisible = hasCamera
        item_camera.setOnClickListener { onItemClicked(CAMERA_KEY) }

        item_gallery.setOnClickListener { onItemClicked(GALLERY_KEY) }

        val hasAvatar = args.hasAvatar

        item_edit.isVisible = hasAvatar
        item_edit.setOnClickListener { onItemClicked(EDIT_KEY) }

        item_delete.isVisible = hasAvatar
        item_delete.setOnClickListener { onItemClicked(DELETE_KEY) }
    }

    private fun onItemClicked(itemKey: String) {
        setFragmentResult(AVATAR_ACTIONS_KEY, bundleOf(SELECT_ACTION_KEY to itemKey))
        dismiss()
    }
}