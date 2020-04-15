package ru.skillbranch.skillarticles.ui.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.viewmodels.bookmarks.BookmarksViewModel

class BookmarksFragment : Fragment() {

    companion object {
        fun newInstance() = BookmarksFragment()
    }

    private lateinit var viewModel: BookmarksViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookmarks, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BookmarksViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
