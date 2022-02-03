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

package org.briarproject.briar.desktop;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class TestNativeNotifications {

	public interface LibNotify extends Library {
		LibNotify INSTANCE = Native.load("notify", LibNotify.class);

		boolean notify_init(String appName);

		Pointer notify_notification_new(String summary, String body,
				String icon);

		boolean notify_notification_show(Pointer notification, Pointer error);
	}

	public static void main(String[] args) {
		System.out.println("Initializing libnotify");
		LibNotify.INSTANCE.notify_init("jna sandbox");

		System.out.println("Creating a notification");
		Pointer notification = LibNotify.INSTANCE.notify_notification_new(
				"Hey there", "You've got 13 new messages",
				null);

		System.out.println("Sending the notification");
		LibNotify.INSTANCE.notify_notification_show(notification, null);

		System.out.println("Waiting a few seconds");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}
}
