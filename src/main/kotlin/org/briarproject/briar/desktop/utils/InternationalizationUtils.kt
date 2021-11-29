package org.briarproject.briar.desktop.utils

import com.ibm.icu.text.MessageFormat
import mu.KotlinLogging
import java.util.Locale
import java.util.MissingResourceException
import java.util.ResourceBundle

/**
 * Helper functions around internationalization.
 *
 * Instead of using the default Locale, one can also use, e.g., `Locale("ar")` instead
 * or call `Locale.setDefault()`.
 */
object InternationalizationUtils {

    private val LOG = KotlinLogging.logger {}

    /**
     * Returns the translated text of the string identified with `key`
     */
    fun i18n(key: String): String =
        try {
            val resourceBundle = createResourceBundle()
            resourceBundle.getString(key)
        } catch (e: MissingResourceException) {
            LOG.warn { "Missing string resource for key '$key'" }
            ""
        }

    /**
     * Returns the translated text of a string with plurals identified with `key`
     */
    fun i18nP(key: String, amount: Int): String =
        try {
            val pattern: String = i18n(key)
            val messageFormat = MessageFormat(pattern, Locale.getDefault())
            messageFormat.format(arrayOf(amount))
        } catch (e: IllegalArgumentException) {
            LOG.warn { "Pattern does not match arguments for resource '$key' and locale '${Locale.getDefault()}" }
            ""
        }

    /**
     * Returns the translated text of a formatted string with
     */
    fun i18nF(key: String, vararg params: Any): String =
        try {
            val pattern: String = i18n(key)
            java.text.MessageFormat.format(pattern, *params)
        } catch (e: IllegalArgumentException) {
            LOG.warn { "Pattern does not match arguments for resource '$key'" }
            ""
        }

    /**
     * Returns the resource bundle used for i18n at Briar Desktop
     */
    private fun createResourceBundle(): ResourceBundle {
        val locale = Locale.getDefault()
        return ResourceBundle.getBundle("strings.BriarDesktop", locale)
    }
}
