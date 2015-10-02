package com.hs.mail.webmail.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import javax.mail.Folder;
import javax.mail.MessagingException;

import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.impl.WmaFolderImpl;

public class WmaFolderList {

	/**
	 * Builds this list of folders from the given array of folders.
	 * If recursive, then it will build a flat list of the complete
	 * folder tree.
	 */
	private static List<WmaFolder> buildFolderList(WmaFolder folder,
			String pattern) throws MessagingException, WmaException {
		Folder[] folders = folder.getFolder().list(pattern);
		List<WmaFolder> flist = new ArrayList<WmaFolder>(folders.length);
		for (int i = 0; i < folders.length; i++) {
			flist.add(WmaFolderImpl.createLight(folders[i]));
		}
		return flist;
	}
	
	private static void buildFolderTree(WmaFolder folder,
			List<WmaFolder> flist, char separator) {
		Stack<WmaFolder> stack = new Stack<WmaFolder>();
		stack.push(folder); // guard
		for (WmaFolder child : flist) {
			WmaFolder parent = stack.pop();
			while (!stack.isEmpty() && !parent.isParent(child, separator)) {
				// pop until current folder's parent is found
				parent = stack.pop();
			}
			stack.push(parent.addChild(child));
		}
	}

	/**
	 * Factory method that creates a list of all subfolders of the given
	 * folder.
	 * 
	 * @param folder the <tt>WmaFolder</tt> instance to be listed.
	 * @param recursive flags if the list should be build recursive.
	 * @return the newly created <tt>WmaFolderList</tt> instance.
	 * @throws WmaException if it fails to build the folder list.
	 */
	public static void createSubfolderList(WmaFolder folder, boolean recursive,
			char separator) throws WmaException {
		try {
			List<WmaFolder> subfolders = buildFolderList(folder,
					(recursive) ? "*" : "%");
			if (recursive) {
				Collections.sort(subfolders, HIERCHICAL);
				buildFolderTree(folder, subfolders, separator);
			} else {
				folder.setSubfolders(subfolders);
			}
		} catch (MessagingException mex) {
			throw new WmaException(mex.getMessage());
		}
	}

	public static final Comparator<WmaFolder> HIERCHICAL = new Comparator<WmaFolder>() {
		public int compare(WmaFolder f1, WmaFolder f2) {
			return f1.getPath().compareTo(f2.getPath());
		}
	};
	
}
