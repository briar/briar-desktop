/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
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

package org.briarproject.briar.desktop

import dagger.Module
import org.briarproject.briar.attachment.AttachmentModule
import org.briarproject.briar.autodelete.AutoDeleteModule
import org.briarproject.briar.avatar.AvatarModule
import org.briarproject.briar.blog.BlogModule
import org.briarproject.briar.client.BriarClientModule
import org.briarproject.briar.conversation.ConversationModule
import org.briarproject.briar.feed.FeedModule
import org.briarproject.briar.forum.ForumModule
import org.briarproject.briar.identity.IdentityModule
import org.briarproject.briar.introduction.IntroductionModule
import org.briarproject.briar.messaging.MessagingModule
import org.briarproject.briar.privategroup.PrivateGroupModule
import org.briarproject.briar.privategroup.invitation.GroupInvitationModule
import org.briarproject.briar.sharing.SharingModule

@Module(
    includes = [
        AttachmentModule::class,
        AttachmentModule::class,
        AutoDeleteModule::class,
        AvatarModule::class,
        BlogModule::class,
        BriarClientModule::class,
        ConversationModule::class,
        FeedModule::class,
        ForumModule::class,
        GroupInvitationModule::class,
        IdentityModule::class,
        IntroductionModule::class,
        MessagingModule::class,
        PrivateGroupModule::class,
        SharingModule::class,
        ModifiedTestDataModule::class
    ]
)
internal class ModifiedBriarCoreModule()
