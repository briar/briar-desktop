Translation of Briar Desktop is provided by the Localization Lab. To make
Briar Desktop available in your language, please visit
https://www.transifex.com/otf/briar/ and ask to join one of the LocLab’s
language teams. You’ll then be able to contribute on
https://www.transifex.com/otf/briar/briar-desktop/.

The Localization Lab has some instructions and advice for
translators at https://wiki.localizationlab.org/index.php/Briar.

#### Updating translations in Briar Desktop

_This section is of interest only for developers of Briar Desktop_.

To update translations locally, first install `transifex-client`.
You can then pull updates with `tx pull -a -f`.

If that command added a language that was previously not supported,
you have to manually insert the language code
in [UnencryptedSettings.kt](./briar-desktop/src/main/kotlin/org/briarproject/briar/desktop/settings/UnencryptedSettings.kt)
for it to be shown in the language selection.

The Localization Lab has some instructions and advice for
developers, too, at https://www.localizationlab.org/roles-guidelines#developer.

This is how updating translations in Briar Desktop works:

* Transifex periodically fetches the source file from code.briarproject.org
* Translators submit their updates via Transifex
* We pull the updates from Transifex and commit them before each release
* Occasionally we make minor updates locally and push them to Transifex

Translations only happen on Transifex.
You might want to make your changes through Transifex by joining the respective language teams.

To test Briar Desktop in your language,
you can simply change the respective setting in the settings screen.

There's also a section in [HACKING.md](./HACKING.md#testing-different-locales)
dedicated to testing of different locales in order to try out translations.
