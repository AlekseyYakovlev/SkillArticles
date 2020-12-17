package ru.skillbranch.skillarticles.ui.transcriptions

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.viewmodels.transcriptions.TranscriptionsViewModel

class TranscriptionsFragment : Fragment(R.layout.fragment_transcriptions) {

    companion object {
        fun newInstance() = TranscriptionsFragment()
    }

    private val viewModel: TranscriptionsViewModel by viewModels()

//TODO Add translation functionality from ML Kit

}
