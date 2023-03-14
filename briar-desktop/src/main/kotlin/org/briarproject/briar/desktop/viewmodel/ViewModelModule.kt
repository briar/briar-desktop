/*
 * Briar Desktop
 * Copyright (C) 2021-2023 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarproject.briar.desktop.viewmodel

import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import org.briarproject.briar.desktop.contact.ContactListViewModel
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel
import org.briarproject.briar.desktop.conversation.ConversationViewModel
import org.briarproject.briar.desktop.forum.ForumListViewModel
import org.briarproject.briar.desktop.forum.sharing.ForumSharingViewModel
import org.briarproject.briar.desktop.introduction.IntroductionViewModel
import org.briarproject.briar.desktop.login.StartupViewModel
import org.briarproject.briar.desktop.mailbox.MailboxViewModel
import org.briarproject.briar.desktop.navigation.SidebarViewModel
import org.briarproject.briar.desktop.privategroup.PrivateGroupListViewModel
import org.briarproject.briar.desktop.settings.SettingsViewModel
import kotlin.reflect.KClass

@Module
abstract class ViewModelModule {
    @MapKey
    internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

    @Binds
    @IntoMap
    @ViewModelKey(StartupViewModel::class)
    abstract fun bindStartupViewModel(startupViewModel: StartupViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SidebarViewModel::class)
    abstract fun bindSidebarViewModel(sidebarViewModel: SidebarViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContactListViewModel::class)
    abstract fun bindContactListViewModel(contactListViewModel: ContactListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddContactViewModel::class)
    abstract fun bindAddContactViewModel(addContactViewModel: AddContactViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConversationViewModel::class)
    abstract fun bindConversationViewModel(conversationViewModel: ConversationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IntroductionViewModel::class)
    abstract fun bindIntroductionViewModel(introductionViewModel: IntroductionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PrivateGroupListViewModel::class)
    abstract fun bindPrivateGroupListViewModel(privateGroupListViewModel: PrivateGroupListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ForumListViewModel::class)
    abstract fun bindForumListViewModel(forumListViewModel: ForumListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ForumSharingViewModel::class)
    abstract fun bindForumSharingViewModel(forumSharingViewModel: ForumSharingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MailboxViewModel::class)
    abstract fun bindMailboxViewModel(mailboxViewModel: MailboxViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(settingsViewModel: SettingsViewModel): ViewModel
}
