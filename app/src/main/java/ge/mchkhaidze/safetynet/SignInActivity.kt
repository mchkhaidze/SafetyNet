package ge.mchkhaidze.safetynet

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import ge.mchkhaidze.safetynet.Utils.Companion.hideSoftKeyboard
import ge.mchkhaidze.safetynet.feed.NewsFeed


class SignInActivity : BaseActivity(), ErrorHandler {

    private lateinit var signUpText: TextView
    private lateinit var signInButton: Button
    private lateinit var emailField: TextView
    private lateinit var passwordField: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        initViews()

        setUpSignInButton()
        setUpSignUpButton()

        hideSoftKeyboard(R.id.email_si)
        hideSoftKeyboard(R.id.pass_si)
    }


    private fun initViews() {
        emailField = findViewById(R.id.email_si)
        passwordField = findViewById(R.id.pass_si)
        signUpText = findViewById(R.id.sign_up_link)
        signInButton = findViewById(R.id.sign_in)
    }

    private fun setUpSignInButton() {
        signInButton.setOnClickListener {
            val email = emailField.text.toString()
            val pass = passwordField.text.toString()

            when {
                email == "" -> Utils.showWarning(getString(R.string.empty_email), signInButton)
                pass == "" -> Utils.showWarning(getString(R.string.empty_pass), signInButton)
                else -> AuthenticationService.logIn(email, pass, this::goToFeed, this::handleError)
            }
        }
    }

    private fun setUpSignUpButton() {

        val clickableSpan = object : ClickableSpan() {

            override fun updateDrawState(textPaint: TextPaint) {
                super.updateDrawState(textPaint)
                textPaint.isUnderlineText = false
            }

            override fun onClick(textView: View) {
                NavigationService.loadPage(this@SignInActivity, SignUpActivity::class.java)
            }
        }

        val text = SpannableString(signUpText.text)
        text.setSpan(
            clickableSpan,
            text.indexOf("Sign Up"),
            text.indexOf("Sign Up") + 7,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        signUpText.text = text
        signUpText.movementMethod = LinkMovementMethod.getInstance()
        signUpText.highlightColor = Color.TRANSPARENT
    }


    private fun goToFeed(): Boolean {
        NavigationService.loadPage(this@SignInActivity, NewsFeed::class.java)
        return true
    }

    override fun handleError(err: String): Boolean {
        Utils.showWarning(err, signInButton)
        return true
    }
}
