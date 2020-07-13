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

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_transcriptions, container, false)
//    }

}
