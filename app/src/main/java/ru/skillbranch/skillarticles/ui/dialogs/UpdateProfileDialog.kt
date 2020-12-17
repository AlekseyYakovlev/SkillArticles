package ru.skillbranch.skillarticles.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.update_profile_dialog.view.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.viewmodels.profile.ProfileViewModel

class UpdateProfileDialog : DialogFragment() {

    val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = layoutInflater.inflate(R.layout.update_profile_dialog, null).also {
            setupViews(it)
        }

        val adb = AlertDialog.Builder(requireContext())
            .setTitle(R.string.update_profile_dialog__title)
            .setView(view)
            .setPositiveButton(
                R.string.update_profile_dialog__update
            ) { dialog, _ ->
                updateProfileData(view)
                dialog.dismiss()
            }
            .setNegativeButton(
                R.string.update_profile_dialog__cancel
            ) { dialog, _ ->
                dialog.cancel()
            }
        return adb.create()
    }

    private fun setupViews(view: View) {
        with(viewModel.currentState) {
            name?.let { view.et_update_name.setText(it) }
            about?.let { view.et_update_about.setText(it) }
        }
    }

    private fun updateProfileData(view: View) {
        viewModel.handleProfileDataChange(
            newName = view.et_update_name.text.toString(),
            newAbout = view.et_update_about.text.toString(),
        )
    }
}