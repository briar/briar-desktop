package org.briarproject.briar.desktop.privategroups

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.privategroup.PrivateGroup
import org.briarproject.briar.api.privategroup.PrivateGroupManager
import org.briarproject.briar.desktop.utils.removeFirst
import org.briarproject.briar.desktop.utils.replaceFirst
import org.briarproject.briar.desktop.viewmodel.BriarEventListenerViewModel
import java.util.logging.Logger
import javax.inject.Inject

class PrivateGroupListViewModel
@Inject
constructor(
    val privateGroupManager: PrivateGroupManager,
    val conversationManager: ConversationManager,
    val connectionRegistry: ConnectionRegistry,
    eventBus: EventBus,
) : BriarEventListenerViewModel(eventBus) {

    companion object {
        private val LOG = Logger.getLogger(PrivateGroupListViewModel::class.java.name)
    }

    private val _fullContactList = mutableListOf<PrivateGroupItem>()
    private val _filteredContactList = mutableStateListOf<PrivateGroupItem>()

    val privateGroupList: List<PrivateGroupItem> = _filteredContactList

    private fun loadPrivateGroups() {
        _fullContactList.apply {
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
        updateFilteredList()
    }

    private fun updateItem(contactId: ContactId, update: (PrivateGroupItem) -> PrivateGroupItem) {
        _fullContactList.replaceFirst({ it.privateGroup.id == contactId }, update)
        updateFilteredList()
    }

    private fun removeItem(groupId: GroupId) {
        _fullContactList.removeFirst { it.privateGroup.id == groupId }
        updateFilteredList()
    }

    override fun onInit() {
        super.onInit()
        loadPrivateGroups()
    }

    private val _filterBy = mutableStateOf("")
    private val _selectedContactId = mutableStateOf<GroupId?>(null)

    val filterBy: State<String> = _filterBy
    val selectedPrivateGroupId: State<GroupId?> = _selectedContactId

    fun selectPrivateGroup(privateGroupId: GroupId) {
        _selectedContactId.value = privateGroupId
    }

    fun isSelected(privateGroupId: GroupId) = _selectedContactId.value == privateGroupId

    private fun filterContact(privateGroup: PrivateGroup) =
        // todo: also filter on alias
        privateGroup.name.contains(_filterBy.value, ignoreCase = true)

    fun setFilterBy(filter: String) {
        _filterBy.value = filter
        updateFilteredList()
    }

    // todo: when migrated to StateFlow, this could be done implicitly instead
    fun updateFilteredList() {
        _filteredContactList.apply {
            clear()
            addAll(_fullContactList.filter { filterContact(it.privateGroup) }.sortedByDescending { it.timestamp })
        }

        // reset selected contact to null if not available after filtering
        val id = _selectedContactId.value
        if (id != null && !privateGroupList.map { it.privateGroup.id }.contains(id)) {
            _selectedContactId.value = null
        }
    }

    override fun eventOccurred(e: Event?) {
        /*
        when (e) {
            is ContactAddedEvent -> {
                LOG.info("Contact added, reloading")
                loadPrivateGroups()
            }
            is ContactRemovedEvent -> {
                LOG.info("Contact removed, removing item")
                removeItem(e.contactId)
            }
        }
        when (e) {
            is ConversationMessageReceivedEvent<*> -> {
                LOG.info("Conversation message received, updating item")
                updateItem(e.contactId) { it.updateFromMessageHeader(e.messageHeader) }
            }
            is ConversationMessageToBeSentEvent -> {
                LOG.info("Conversation message added, updating item")
                updateItem(e.contactId) { it.updateFromMessageHeader(e.messageHeader) }
            }
            // is AvatarUpdatedEvent -> {}
            is ContactAliasChangedEvent -> {
                updateItem(e.contactId) { it.updateAlias(e.alias) }
            }
        }
         */
    }
}
