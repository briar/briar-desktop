package org.briarproject.briar.desktop.utils

import com.ibm.icu.text.MessageFormat
import java.util.Locale
import java.util.ResourceBundle

/**
 * Helper functions around internationalization.
 *
 * Instead of using the default Locale, one can also use, e.g., `Locale("ar")` instead
 * or call `Locale.setDefault()`.
 */
object InternationalizationUtils {

    /**
     * Returns the translated text of the string identified with `key`
     */
    fun i18n(key: String): String {
        val resourceBundle = createResourceBundle()
        return resourceBundle.getString(key)
    }

    /**
     * Returns the translated text of a string with plurals identified with `key`
     */
    fun i18nP(key: String, amount: Int): String {
        val pattern: String = i18n(key)
        val messageFormat = MessageFormat(pattern, Locale.getDefault())
        return messageFormat.format(arrayOf(amount))
    }

    /**
     * Returns the translated text of a formatted string with
     */
    fun i18nF(key: String, vararg params: Any): String {
        val pattern: String = i18n(key)
        return java.text.MessageFormat.format(pattern, *params)
    }

    /**
     * Returns the resource bundle used for i18n at Briar Desktop
     */
    private fun createResourceBundle(): ResourceBundle {
        val locale = Locale.getDefault()
        return ResourceBundle.getBundle("strings.BriarDesktop", locale)
    }
}
