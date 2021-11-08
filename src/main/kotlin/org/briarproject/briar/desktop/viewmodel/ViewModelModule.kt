package org.briarproject.briar.desktop.viewmodel

import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import org.briarproject.briar.desktop.contact.ContactListViewModel
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel
import org.briarproject.briar.desktop.conversation.ConversationViewModel
import org.briarproject.briar.desktop.introduction.IntroductionViewModel
import org.briarproject.briar.desktop.login.LoginViewModel
import org.briarproject.briar.desktop.login.RegistrationViewModel
import org.briarproject.briar.desktop.navigation.SidebarViewModel
import kotlin.reflect.KClass

@Module
abstract class ViewModelModule {
    @MapKey
    internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    abstract fun bindRegistrationViewModel(registrationViewModel: RegistrationViewModel): ViewModel

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
}
