package com.hs.mail.imap.dao;

import java.util.List;
import java.util.Map;

import com.hs.mail.imap.user.Alias;
import com.hs.mail.imap.user.User;
import com.hs.mail.web.model.PublicFolder;

public interface HwUserDao extends DaoSupport {

	public User getUser(long id);

	public int getUserCount(String domain);
	
	public List<User> getUserList(String domain, int page, int pageSize);
	
	public long addUser(User user);
	
	public int updateUser(User user);
	
	public int deleteUser(long id);
	
	public Alias getAlias(long id);
	
	public int getAliasCount(String domain);

	public List<Alias> getAliasList(String domain, int page, int pageSize);

	public long addAlias(Alias alias);
	
	public int updateAlias(Alias alias);

	public int deleteAlias(long id);

	public PublicFolder getPublicFolder(String namespace, long mailboxID);
	
	public List<PublicFolder> getPublicFolders(long ownerid, final String namespace);

	public List<Map<String, Object>> getHeaderCounts();

	public int deleteHeaderValues(String headerNameID);

}
