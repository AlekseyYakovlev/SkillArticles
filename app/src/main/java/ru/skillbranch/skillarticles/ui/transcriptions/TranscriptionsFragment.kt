package ru.skillbranch.skillarticles.ui.transcriptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.viewmodels.transcriptions.TranscriptionsViewModel

class TranscriptionsFragment : Fragment() {

    companion object {
        fun newInstance() = TranscriptionsFragment()
    }

    private lateinit var viewModel: TranscriptionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transcriptions, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TranscriptionsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
