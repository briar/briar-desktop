package org.briarproject.briar.swing;

import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventListener;
import org.briarproject.bramble.api.sync.MessageId;
import org.briarproject.bramble.api.sync.event.MessagesSentEvent;
import org.briarproject.briar.api.messaging.MessagingManager;
import org.briarproject.briar.api.messaging.PrivateMessageHeader;
import org.briarproject.briar.api.messaging.event.PrivateMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class EventBusConnector implements EventListener {

	final static Logger logger =
			LoggerFactory.getLogger(EventBusConnector.class);

	private MessagingManager messagingManager;

	public EventBusConnector(MessagingManager messagingManager) {
		this.messagingManager = messagingManager;
	}

	@Override
	public void eventOccurred(Event e) {
		try {
			tryHandleEvent(e);
		} catch (DbException ex) {
			logger.warn("Exception while handling message", ex);
		}
	}

	private void tryHandleEvent(Event e) throws DbException {
		if (e instanceof MessagesSentEvent) {
			MessagesSentEvent mse = (MessagesSentEvent) e;
			Collection<MessageId> sent = mse.getMessageIds();
			for (MessageId mid : sent) {
				try {
					String messageText = messagingManager.getMessageText(mid);
					logger.info("sent: " + messageText);
				} catch (DbException exception) {
					logger.info("non-text message sent");
				}
			}
		} else if (e instanceof PrivateMessageReceivedEvent) {
			PrivateMessageReceivedEvent pmre =
					(PrivateMessageReceivedEvent) e;
			PrivateMessageHeader header = pmre.getMessageHeader();
			String messageText =
					messagingManager.getMessageText(header.getId());
			logger.debug("received: " + messageText);
		}
	}

}
