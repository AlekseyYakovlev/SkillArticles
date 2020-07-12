package ru.skillbranch.skillarticles.ui.auth

import android.text.Spannable
import androidx.core.text.set
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_auth.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.ui.custom.spans.UnderlineSpan
import ru.skillbranch.skillarticles.viewmodels.auth.AuthViewModel
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand

class AuthFragment : BaseFragment<AuthViewModel>() {
    override val viewModel: AuthViewModel by viewModels()
    override val layout: Int = R.layout.fragment_auth
    private val args: AuthFragmentArgs by navArgs()


    override fun setupViews() {
        tv_privacy.setOnClickListener {
            viewModel.navigate(NavigationCommand.To(R.id.page_privacy_policy))
        }
        btn_login.setOnClickListener {
            viewModel.handleLogin(
                et_login.text.toString(),
                et_password.text.toString(),
                if (args.privateDestination == -1) null else args.privateDestination
            )
        }
        val color = root.attrValue(R.attr.colorPrimary)
        (tv_access_code.text as Spannable).let { it[0..it.length] = UnderlineSpan(color) }
        (tv_privacy.text as Spannable).let { it[0..it.length] = UnderlineSpan(color) }
    }
}
