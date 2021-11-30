package org.briarproject.briar.desktop.privategroups

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.privategroup.PrivateGroupManager
import org.briarproject.briar.desktop.viewmodel.BriarEventListenerViewModel
import javax.inject.Inject

class PrivateGroupListViewModel
@Inject
constructor(
    private val privateGroupManager: PrivateGroupManager,
    val conversationManager: ConversationManager,
    val connectionRegistry: ConnectionRegistry,
    eventBus: EventBus,
) : BriarEventListenerViewModel(eventBus) {

    private val _fullPrivateGroupList = mutableListOf<PrivateGroupItem>()

    val privateGroupList: List<PrivateGroupItem> = _fullPrivateGroupList

    private fun loadPrivateGroups() {
        _fullPrivateGroupList.apply {
            clear()
            addAll(
                privateGroupManager.privateGroups.map { privateGroup ->
                    PrivateGroupItem(
                        privateGroup,
                        privateGroupManager.getGroupCount(privateGroup.id),
                    )
                }
            )
        }
    }

    override fun onInit() {
        super.onInit()
        loadPrivateGroups()
    }

    private val _selectedContactId = mutableStateOf<GroupId?>(null)

    val selectedPrivateGroupId: State<GroupId?> = _selectedContactId

    fun selectPrivateGroup(privateGroupId: GroupId) {
        _selectedContactId.value = privateGroupId
    }

    fun isSelected(privateGroupId: GroupId) = _selectedContactId.value == privateGroupId

    override fun eventOccurred(e: Event?) {
        // TODO
    }
}
