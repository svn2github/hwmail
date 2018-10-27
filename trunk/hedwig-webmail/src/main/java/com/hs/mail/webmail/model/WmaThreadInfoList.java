package com.hs.mail.webmail.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.SearchTerm;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.impl.HwFolder;
import com.hs.mail.webmail.model.impl.HwFolder.Threadable;
import com.hs.mail.webmail.model.impl.WmaThreadInfoImpl;
import com.hs.mail.webmail.util.Pager;
import com.hs.mail.webmail.util.WmaUtils;
import com.sun.mail.imap.IMAPFolder;

public class WmaThreadInfoList extends WmaMessageInfoList {
	
	private static Logger log = LoggerFactory.getLogger(WmaThreadInfoList.class);

	private WmaThreadInfoList() {
		super();
	}
	
	public static WmaMessageInfoList createWmaThreadInfoList(HwFolder folder,
			String algorithm, SearchTerm term, Pager pager)
			throws WmaException {
		IMAPFolder f = folder.getFolder();
		try {
			// for listing only
			if (!f.isOpen()) {
				f.open(Folder.READ_ONLY);
			}

			List<Threadable> threads = folder.thread(true, algorithm, term);
			if (CollectionUtils.isEmpty(threads)) {
				return EMPTY_LIST;
			} else if (pager != null) {
				pager.setItemCount(threads.size());
				threads = threads.subList(pager.getBegin(), pager.getEnd() + 1);
			}

			long[] uids = new long[threads.size()];
			int i = 0, j = 0;
			while (i < threads.size()) {
				long uid = threads.get(i++).getNumber();
				if (uid >= 0)
					uids[j++] = uid;
				// TODO - if dummy then get the first child's UID.
			}
			if (uids.length != j)
				uids = Arrays.copyOfRange(uids, 0, j);

			Message[] msgs = WmaUtils.getMessagesByUID(f, uids, 100);
			return createWmaThreadInfoList(f, threads, msgs);
		} catch (MessagingException mex) {
			log.error(mex.getMessage(), mex);
			throw new WmaException("wma.messagelist.failedcreation")
					.setException(mex);
		} finally {
			try {
				f.close(false);
			} catch (MessagingException e) {
			}
		}
	}
	
	private void buildThreadInfoList(IMAPFolder f, List<Threadable> threads,
			Message[] msgs) throws WmaException {
		this.messageInfos = new ArrayList<WmaMessageInfo>(threads.size());
		for (int i = threads.size() - 1, j = msgs.length - 1; i >= 0; i--) {
			Threadable thread = threads.get(i);
			long uid = thread.getNumber();
			int depth = thread.getDepth();
			WmaThreadInfoImpl msginfo = (uid >= 0)
					? WmaThreadInfoImpl.createMessageInfo(uid, msgs[j--], depth)
					: WmaThreadInfoImpl.createDummpy(depth);

			List<WmaMessageInfo> children = createConversations(f,
					thread.getChildren());
			msginfo.setConversations(children);
			addMessageInfo(msginfo);
		}
	}

	private static List<WmaMessageInfo> createConversations(IMAPFolder f,
			List<Threadable> threads) throws WmaException {
		if (CollectionUtils.isEmpty(threads)) {
			return null;
		}
		
		long[] uids = new long[threads.size()];
		for (int i = 0; i < threads.size(); i++)
			uids[i] = threads.get(i).getNumber();

		List<WmaMessageInfo> conv = null;
		try {
		Message[] msgs = f.getMessagesByUID(uids);
		conv = new ArrayList<WmaMessageInfo>(uids.length);
		for (int i = 0; i < uids.length; i++) {
				WmaThreadInfoImpl msginfo = WmaThreadInfoImpl.createMessageInfo(
						uids[i], msgs[i], threads.get(i).getDepth());
				conv.add(msginfo);
			}
		} catch (MessagingException ex) {
		}
		return conv;
	}

	private static WmaMessageInfoList createWmaThreadInfoList(IMAPFolder f,
			List<Threadable> threads, Message[] msgs)
			throws WmaException, MessagingException {
		WmaThreadInfoList msginfos = new WmaThreadInfoList();
		msginfos.buildThreadInfoList(f, threads, msgs);
		return msginfos;
	}
	
}
