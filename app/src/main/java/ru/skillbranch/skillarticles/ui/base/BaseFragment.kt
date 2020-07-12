package ru.skillbranch.skillarticles.ui.base

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_root.*
import ru.skillbranch.skillarticles.ui.RootActivity
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState

abstract class BaseFragment<T : BaseViewModel<out IViewModelState>> : Fragment() {
    val root: RootActivity
        get() = activity as RootActivity
    open val binding: Binding? = null
    protected abstract val viewModel: T
    protected abstract val layout: Int

    open val prepareToolbar: (ToolbarBuilder.() -> Unit)? = null
    open val prepareBottombar: (BottombarBuilder.() -> Unit)? = null

    val toolbar
        get() = root.toolbar

    //set listeners, tuning views
    abstract fun setupViews()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layout, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //prepare toolbar
        root.toolbarBuilder
            .invalidate()
            .prepare(prepareToolbar)
            .build(root)

        root.bottombarBuilder
            .invalidate()
            .prepare(prepareBottombar)
            .build(root)

        //restore state
        viewModel.restoreState()
        binding?.restoreUi(savedInstanceState)

        //owner it is view
        viewModel.observeState(viewLifecycleOwner) { binding?.bind(it) }
        //bind default values if viewmodel not loaded data
        if (binding?.isInflated == false) binding?.onFinishInflate()

        viewModel.observeNotifications(viewLifecycleOwner) { root.renderNotification(it) }
        viewModel.observeNavigation(viewLifecycleOwner) { root.viewModel.navigate(it) }

        setupViews()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        binding?.rebind()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveState()
        binding?.saveUi(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        if (root.toolbarBuilder.items.isNotEmpty()) {
            for ((index, menuHolder) in root.toolbarBuilder.items.withIndex()) {
                val item = menu.add(0, menuHolder.menuId, index, menuHolder.title)
                item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                    .setIcon(menuHolder.icon)
                    .setOnMenuItemClickListener {
                        menuHolder.clickListener?.invoke(it)?.let { true } ?: false
                    }

                if (menuHolder.actionViewLayout != null) item.setActionView(menuHolder.actionViewLayout)
            }
        } else menu.clear()
        super.onPrepareOptionsMenu(menu)
    }

}