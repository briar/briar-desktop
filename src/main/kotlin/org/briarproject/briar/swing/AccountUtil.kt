package org.briarproject.briar.swing

import com.github.ajalt.clikt.core.UsageError
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator
import org.briarproject.bramble.api.identity.AuthorConstants
import org.briarproject.briar.swing.dialogs.NewAccountPrompt
import org.briarproject.briar.swing.dialogs.UpdatePasswordPrompt

class AccountUtil {

    companion object {
        @JvmStatic
        fun check(passwordStrengthEstimator: PasswordStrengthEstimator, result: NewAccountPrompt.Result) {
            if (passwordStrengthEstimator.estimateStrength(String(result.password)) < PasswordStrengthEstimator.QUITE_WEAK)
                throw UsageError("Please enter a stronger password!")
            if (!String(result.password).equals(String(result.passwordRepeat)))
                throw UsageError("Passwords do not match!")
            if (result.nickname.length > AuthorConstants.MAX_AUTHOR_NAME_LENGTH)
                throw UsageError("Please choose a shorter nickname!")
        }

        @JvmStatic
        fun check(passwordStrengthEstimator: PasswordStrengthEstimator, result: UpdatePasswordPrompt.Result) {
            if (passwordStrengthEstimator.estimateStrength(String(result.password)) < PasswordStrengthEstimator.QUITE_WEAK)
                throw UsageError("Please enter a stronger password!")
            if (!String(result.password).equals(String(result.passwordRepeat)))
                throw UsageError("Passwords do not match!")
        }
    }

}