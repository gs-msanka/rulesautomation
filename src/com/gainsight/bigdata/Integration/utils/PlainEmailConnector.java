package com.gainsight.bigdata.Integration.utils;


import java.util.HashMap;
import java.util.Properties;
import java.util.Map.Entry;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;

import com.gainsight.sfdc.pages.Constants;
import com.gainsight.testdriver.Log;
import com.gainsight.utils.wait.CommonWait;
import com.gainsight.utils.wait.ExpectedCommonWaitCondition;

/**
 * Utility class to connect to a mail box and perform email validations, for the
 * emails sent via Gainsight Email Services.
 * 
 */
public class PlainEmailConnector implements Constants{
	
	protected static Store store;

	/**
	 * @param folderName
	 *            - which folder to check the mail for
	 * @param msgDetails
	 *            - details that need to be validated basing on the receipient
	 *            Email and subject...Will add more checkpoints as per the
	 *            reqmnt
	 * @return true if both map objects are equal
	 * @throws Exception
	 */
	public static boolean isMailDelivered(String folderName,
			final HashMap<String, String> msgDetails) throws Exception {
		final HashMap<String, String> msg = new HashMap<String, String>();
		boolean result = false;;
		Folder folder = store.getFolder(folderName);
		folder.open(Folder.READ_WRITE);
		// search for all "unseen" messages
		Flags seen = new Flags(Flags.Flag.SEEN);
		FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
		// search for all "Recent" messages
		Flags recent = new Flags(Flags.Flag.RECENT);
		FlagTerm recentFlagTerm = new FlagTerm(recent, true);
		Message[] foundMessages = folder.search(unseenFlagTerm);
		if (foundMessages.length == 0) {
			Log.error("No messages found, Please Check.");
			return result;
		} else {
			Log.info("Total number of messages:" + foundMessages.length);
			for (int i = 0; i < foundMessages.length; i++) {
				Log.info("In MailBox, Email subject is "
						+ foundMessages[i].getSubject());
				Address[] a = foundMessages[i].getAllRecipients();
				String tempToAddress = a[0].toString();
				String toAddress = tempToAddress.substring(
						tempToAddress.lastIndexOf("<") + 1,
						tempToAddress.lastIndexOf(">"));
				Log.info("In MailBox, Receipient email Address is " + toAddress);
				Log.info("Sender email is" +foundMessages[i].getFrom());
				msg.put(toAddress.trim(), foundMessages[i].getSubject().trim());
			}
			for (Entry<String, String> entry : msg.entrySet()) {
				Log.info("Key : " + entry.getKey() + " Value : "
						+ entry.getValue());
			}
			CommonWait.waitForCondition(MAX_TIME, MIN_TIME,
					new ExpectedCommonWaitCondition<Boolean>() {
						@Override
						public Boolean apply() {
							return msg.equals(msgDetails);
						}
					});
			Log.info("comparision value is" + msg.equals(msgDetails));
			if (msg.equals(msgDetails)) {
				result = true;
			}
		}
	//	folder.close(true);
	//	store.close();
		return result;
	}
	
		/**
		 * @param folderName
		 *            - which folder to check the mail for            
		 * @return true if all messages are marked as Read/Seen
		 * 
		 * @throws Exception
		 */
	  
	public static boolean isAllEmailsSeen(String folderName) throws Exception {
		boolean result = false;
		Folder folder = store.getFolder(folderName);
		folder.open(Folder.READ_WRITE);
		// search for all "unseen" messages
		Flags seen = new Flags(Flags.Flag.SEEN);
		FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
		Message[] foundMessages = folder.search(unseenFlagTerm);
		Log.info("Length of emails Before marking as Seen "
				+ foundMessages.length);
		// Setting flag to Read/Seen
		folder.setFlags(foundMessages, new Flags(Flags.Flag.SEEN), true);
		Log.info("Length of emails after marking as Seen "
				+ folder.getUnreadMessageCount());
		if (folder.getUnreadMessageCount() == 0) {
			Log.error("All Messages are marked as Read/Seen");
			result = true;
		}
		return result;
	}
	
	/**
	 * @param host
	 *            - imap/pop2 etc
	 * @param emailId
	 *            - mailBox userName
	 * @param pwd
	 *            - mailBox password
	 */
	
	public PlainEmailConnector(String host, String userName, String password){
		Properties properties = System.getProperties();
		Session session = Session.getDefaultInstance(properties);
		Store storeConnection = null;
		try {
			storeConnection = session.getStore("imap");
		} catch (NoSuchProviderException e) {
			throw new RuntimeException("No such provider, Please check");
		}
		try {
			storeConnection.connect(host, userName, password);
		} catch (MessagingException e) {
			throw new RuntimeException("Please check authentication details");
		}
		Log.info("Connecting to Store...");
		store=storeConnection;
	}
}
	

