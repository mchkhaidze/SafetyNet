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

class SignUpActivity : BaseActivity(), ErrorHandler {

    private lateinit var signInText: TextView
    private lateinit var signUpButton: Button
    private lateinit var emailField: TextView
    private lateinit var passwordField: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initViews()

        setUpSignUpButton()
        setUpSignInButton()

        hideSoftKeyboard(R.id.email_su)
        hideSoftKeyboard(R.id.pass_su)
    }

    private fun initViews() {
        emailField = findViewById(R.id.email_su)
        passwordField = findViewById(R.id.pass_su)
        signUpButton = findViewById(R.id.sign_up)
        signInText = findViewById(R.id.sign_in_link)
    }

    private fun setUpSignUpButton() {
        signUpButton.setOnClickListener {
            val email = emailField.text.toString()
            val pass = passwordField.text.toString()

            when {
                email == "" -> Utils.showWarning(getString(R.string.empty_email), signUpButton)
                pass == "" -> Utils.showWarning(getString(R.string.empty_pass), signUpButton)
                else -> AuthenticationService.signUp(email, pass, this::goToSettings, this::handleError)
            }
        }
    }

    private fun setUpSignInButton() {

        val clickableSpan = object : ClickableSpan() {

            override fun updateDrawState(textPaint: TextPaint) {
                super.updateDrawState(textPaint)
                textPaint.isUnderlineText = false
            }

            override fun onClick(textView: View) {
                NavigationService.loadPage(this@SignUpActivity, SignInActivity::class.java)
            }
        }

        val text = SpannableString(signInText.text)
        text.setSpan(
            clickableSpan,
            text.indexOf("Sign In"),
            text.indexOf("Sign In") + 7,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        signInText.text = text
        signInText.movementMethod = LinkMovementMethod.getInstance()
        signInText.highlightColor = Color.TRANSPARENT
    }

    private fun goToSettings(): Boolean {
        NavigationService.loadPage(this, Settings::class.java)
        return false
    }

    override fun handleError(err: String): Boolean {
        Utils.showWarning(err, signUpButton)
        return true
    }
}