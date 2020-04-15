package ru.skillbranch.skillarticles.ui.article

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions.circleCropTransform
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.android.synthetic.main.layout_bottombar.*
import kotlinx.android.synthetic.main.layout_bottombar.view.*
import kotlinx.android.synthetic.main.layout_submenu.*
import kotlinx.android.synthetic.main.layout_submenu.view.*
import kotlinx.android.synthetic.main.search_view_layout.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.format
import ru.skillbranch.skillarticles.extensions.hideKeyboard
import ru.skillbranch.skillarticles.extensions.setMarginOptionally
import ru.skillbranch.skillarticles.ui.base.*
import ru.skillbranch.skillarticles.ui.delegates.RenderProp
import ru.skillbranch.skillarticles.viewmodels.article.ArticleState
import ru.skillbranch.skillarticles.viewmodels.article.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.ViewModelFactory



class ArticleFragment : BaseFragment<ArticleViewModel>(), IArticleView {
    private val args: ArticleFragmentArgs by navArgs()
    override val viewModel: ArticleViewModel by viewModels {
        ViewModelFactory(
            owner = this,
            params = args.articleId
        )
    }
    override val layout: Int = R.layout.fragment_article
    override val binding: ArticleBinding by lazy { ArticleBinding() }
    override val prepareToolbar: (ToolbarBuilder.() -> Unit)? = {
        this.setTitle(args.title)
            .setSubtitle(args.category)
            .setLogo(args.categoryIcon)
            .addMenuItem(
                MenuItemHolder(
                    "search",
                    R.id.action_search,
                    R.drawable.ic_search_black_24dp,
                    R.layout.search_view_layout
                )
            )
    }

    override val prepareBottombar: (BottombarBuilder.() -> Unit)? = {
        this.addView(R.layout.layout_submenu)
            .addView(R.layout.layout_bottombar)
            .setVisibility(false)
    }

    private val bottombar
        get() = root.bottombar
    private val submenu
        get() = root.submenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun setupViews() {
        // window resize options
        root.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        setupBottomBar()
        setupSubmenu()

        // init views
        val avatarSize = root.dpToIntPx(40)
        val cornerRadius = root.dpToIntPx(8)

        Glide.with(root)
            .load(args.authorAvatar)
            .apply(circleCropTransform())
            .override(avatarSize)
            .into(iv_author_avatar)

        Glide.with(root)
            .load(args.poster)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .into(iv_poster)

        tv_title.text = args.title
        tv_author.text = args.author
        tv_date.text = args.date.format()

        et_comment.setOnEditorActionListener { view, _, _ ->
            root.hideKeyboard(view)
            viewModel.handleSendComment()
            true
        }
    }

    override fun onDestroyView() {
        root.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        super.onDestroyView()
    }

    override fun showSearchBar() {
        bottombar.setSearchState(true)
        scroll.setMarginOptionally(bottom = root.dpToIntPx(56))
    }

    override fun hideSearchBar() {
        bottombar.setSearchState(false)
        scroll.setMarginOptionally(bottom = root.dpToIntPx(0))
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val menuItem = menu.findItem(R.id.action_search)
        val searchView = (menuItem?.actionView as SearchView)
        searchView.queryHint = getString(R.string.article_search_placeholder)

        //restore searchView
        if (binding.isSearch) {
            menuItem.expandActionView()
            searchView.setQuery(binding.searchQuery, false)

            if (binding.isFocusedSearch) search_view?.requestFocus()
            else searchView.clearFocus()
        }

        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                viewModel.handleSearchMode(true)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                viewModel.handleSearchMode(false)
                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handleSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleSearch(newText)
                return true
            }
        })
    }


    private fun setupSubmenu() {
        submenu.btn_text_up.setOnClickListener { viewModel.handleUpText() }
        submenu.btn_text_down.setOnClickListener { viewModel.handleDownText() }
        submenu.switch_mode.setOnClickListener { viewModel.handleNightMode() }
    }

    private fun setupBottomBar(){

        bottombar.btn_like.setOnClickListener { viewModel.handleLike() }
        bottombar.btn_bookmark.setOnClickListener { viewModel.handleBookmark() }
        bottombar.btn_share.setOnClickListener { viewModel.handleShare() }
        bottombar.btn_settings.setOnClickListener { viewModel.handleToggleMenu() }

        bottombar.btn_result_up.setOnClickListener {
            if (!tv_text_content.hasFocus()) tv_text_content.requestFocus()
            root.hideKeyboard(it)
            viewModel.handleUpResult()
        }

        bottombar.btn_result_down.setOnClickListener {
            if (!tv_text_content.hasFocus()) tv_text_content.requestFocus()
            root.hideKeyboard(it)
            viewModel.handleDownResult()
        }

        bottombar.btn_search_close.setOnClickListener {
            viewModel.handleSearchMode(false)
            root.invalidateOptionsMenu()
        }
    }

    private fun setupCopyListener() {
        tv_text_content.setCopyListener { copy ->
            val clipboard =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied code", copy)
            clipboard.setPrimaryClip(clip)
            viewModel.handleCopyCode()
        }
    }

    inner class ArticleBinding() : Binding() {
        var isFocusedSearch: Boolean = false
        var searchQuery: String? = null

        private var isLoadingContent by RenderProp(true)

        private var isLike: Boolean by RenderProp(false) { bottombar.btn_like.isChecked = it }
        private var isBookmark: Boolean by RenderProp(false) { bottombar.btn_bookmark.isChecked = it }
        private var isShowMenu: Boolean by RenderProp(false) {
            bottombar.btn_settings.isChecked = it
            if (it) submenu.open() else submenu.close()
        }

        private var isBigText: Boolean by RenderProp(false) {
            if (it) {
                tv_text_content.textSize = 18f
                submenu.btn_text_up.isChecked = true
                submenu.btn_text_down.isChecked = false
            } else {
                tv_text_content.textSize = 14f
                submenu.btn_text_up.isChecked = false
                submenu.btn_text_down.isChecked = true
            }
        }

        private var isDarkMode: Boolean by RenderProp(value=false, needInit = false) {
            submenu.switch_mode.isChecked = it
            root.delegate.localNightMode = if (it) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        }

        var isSearch: Boolean by RenderProp(false) {
            if (it) {
                showSearchBar()
                with(toolbar) {
                    (layoutParams as AppBarLayout.LayoutParams).scrollFlags =
                        AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
                }
            } else {
                hideSearchBar()
                with(toolbar) {
                    (layoutParams as AppBarLayout.LayoutParams).scrollFlags =
                        AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                                AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED
                }
            }
        }

        private var searchResults: List<Pair<Int, Int>> by RenderProp(emptyList<Pair<Int, Int>>())
        private var searchPosition: Int by RenderProp(0)

        private var content: List<MarkdownElement> by RenderProp(emptyList<MarkdownElement>()) {
            tv_text_content.isLoading = it.isEmpty()
            tv_text_content.setContent(it)
            if (it.isNotEmpty()) setupCopyListener()

        }

        override val afterInflated: (() -> Unit)? =
            {
                dependsOn<Boolean, Boolean, List<Pair<Int, Int>>, Int>(
                    ::isLoadingContent,
                    ::isSearch,
                    ::searchResults,
                    ::searchPosition
                ) { ilc, iss, sr, sp ->
                    if (!ilc && iss) {
                        tv_text_content.renderSearchResult(sr)
                        tv_text_content.renderSearchPosition(sr.getOrNull(sp))
                    }
                    if (!ilc && !iss) {
                        tv_text_content.clearSearchResult()
                    }

                    bottombar.bindSearchInfo(sr.size, sp)
                }
            }

        override fun bind(data: IViewModelState) {
            data as ArticleState

            isLike = data.isLike
            isBookmark = data.isBookmark
            isShowMenu = data.isShowMenu
            isBigText = data.isBigText
            isDarkMode = data.isDarkMode
            content = data.content

            isLoadingContent = data.isLoadingContent
            isSearch = data.isSearch
            searchQuery = data.searchQuery
            searchPosition = data.searchPosition
            searchResults = data.searchResults
        }

        override fun saveUi(outState: Bundle) {
            outState.putBoolean(::isFocusedSearch.name, search_view?.hasFocus() ?: false)
        }

        override fun restoreUi(savedState: Bundle?) {
            isFocusedSearch = savedState?.getBoolean(::isFocusedSearch.name) ?: false
        }
    }
}