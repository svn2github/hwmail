package com.hs.mail.web.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import com.hs.mail.container.config.Config;
import com.hs.mail.imap.dao.DaoFactory;
import com.hs.mail.imap.dao.HwDaoFactory;
import com.hs.mail.imap.dao.HwUserDao;
import com.hs.mail.imap.dao.MailboxDao;
import com.hs.mail.imap.dao.MessageDao;
import com.hs.mail.imap.message.PhysMessage;
import com.hs.mail.imap.user.Alias;
import com.hs.mail.imap.user.User;
import com.hs.mail.web.model.PublicFolder;

public class HwUserManager {

	private static Logger logger = LoggerFactory.getLogger(HwUserManager.class);

	private TransactionTemplate transactionTemplate;

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		Assert.notNull(transactionManager, "The 'transactionManager' argument must not be null.");
		this.transactionTemplate = new TransactionTemplate(transactionManager);
	}
	
	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	
	public User getUser(long id) {
		return HwDaoFactory.getHwUserDao().getUser(id);
	}
	
	public long getUserID(String address) {
		return HwDaoFactory.getUserDao().getUserID(address);
	}

	public User getUserByAddress(String address) {
		return HwDaoFactory.getUserDao().getUserByAddress(address);
	}

	public int getUserCount(String domain) {
		return HwDaoFactory.getHwUserDao().getUserCount(domain);
	}
	
	public List<User> getUserList(String domain, int page, int pageSize) {
		return HwDaoFactory.getHwUserDao().getUserList(domain, page, pageSize);
	}
	
	public long addUser(final User user) {
		return getTransactionTemplate().execute(
				new TransactionCallback<Long>() {
					public Long doInTransaction(TransactionStatus status) {
						try {
							return HwDaoFactory.getHwUserDao().addUser(user);
						} catch (DataAccessException ex) {
							status.setRollbackOnly();
							throw ex;
						}
					}
				});
	}
	
	public int updateUser(final User user) {
		return getTransactionTemplate().execute(
				new TransactionCallback<Integer>() {
					public Integer doInTransaction(TransactionStatus status) {
						try {
							return HwDaoFactory.getHwUserDao().updateUser(user);
						} catch (DataAccessException ex) {
							status.setRollbackOnly();
							throw ex;
						}
					}
				});
	}
	
	public void deleteUser(final long id) {
		getTransactionTemplate().execute(
				new TransactionCallbackWithoutResult() {
					public void doInTransactionWithoutResult(
							TransactionStatus status) {
						try {
							HwDaoFactory.getHwUserDao().deleteUser(id);
							emptyMailboxes(id);
						} catch (DataAccessException ex) {
							status.setRollbackOnly();
							throw ex;
						}
					}
				});
	}
	
	public void emptyUser(final long id) {
		getTransactionTemplate().execute(
				new TransactionCallbackWithoutResult() {
					public void doInTransactionWithoutResult(
							TransactionStatus status) {
						try {
							emptyMailboxes(id);
						} catch (DataAccessException ex) {
							status.setRollbackOnly();
							throw ex;
						}
					}
				});
	}
	
	private void emptyMailboxes(long ownerID) {
		MailboxDao dao = DaoFactory.getMailboxDao();
		List<PhysMessage> danglings = dao.getDanglingMessageIDList(ownerID);
		dao.deleteMessages(ownerID);
		dao.deleteMailboxes(ownerID);
		if (CollectionUtils.isNotEmpty(danglings)) {
			for (PhysMessage pm : danglings) {
				deletePhysicalMessage(pm);
			}
		}
	}
	
	private void deletePhysicalMessage(PhysMessage pm) {
		MessageDao dao = DaoFactory.getMessageDao();
		dao.deletePhysicalMessage(pm.getPhysMessageID());
		try {
			File file = Config.getDataFile(pm.getInternalDate(), pm.getPhysMessageID());
			FileUtils.forceDelete(file);
		} catch (IOException ex) {
			logger.warn(ex.getMessage(), ex); // Ignore - What we can do?
		}
	}

	public Alias getAlias(long id) {
		return HwDaoFactory.getHwUserDao().getAlias(id); 
	}
	
	public int getAliasCount(String domain) {
		return HwDaoFactory.getHwUserDao().getAliasCount(domain);
	}
	
	public List<Alias> getAliasList(String domain, int page, int pageSize) {
		return HwDaoFactory.getHwUserDao().getAliasList(domain, page, pageSize);
	}

	public long addAlias(final Alias alias) {
		return getTransactionTemplate().execute(
				new TransactionCallback<Long>() {
					public Long doInTransaction(TransactionStatus status) {
						try {
							return HwDaoFactory.getHwUserDao().addAlias(alias);
						} catch (DataAccessException ex) {
							status.setRollbackOnly();
							throw ex;
						}
					}
				});
	}
	
	public int updateAlias(final Alias alias) {
		return getTransactionTemplate().execute(
				new TransactionCallback<Integer>() {
					public Integer doInTransaction(TransactionStatus status) {
						try {
							return HwDaoFactory.getHwUserDao().updateAlias(alias);
						} catch (DataAccessException ex) {
							status.setRollbackOnly();
							throw ex;
						}
					}
				});
	}
	
	public void deleteAlias(final long id) {
		getTransactionTemplate().execute(
				new TransactionCallbackWithoutResult() {
					public void doInTransactionWithoutResult(
							TransactionStatus status) {
						try {
							HwDaoFactory.getHwUserDao().deleteAlias(id);
						} catch (DataAccessException ex) {
							status.setRollbackOnly();
							throw ex;
						}
					}
				});
	}
	
	public long getQuotaUsage(long ownerID) {
		return HwDaoFactory.getUserDao().getQuotaUsage(ownerID, 0);
	}

	public String toAddress(String user) {
		if (user.indexOf('@') != -1)
			return user;
		else
			return new StringBuffer(user)
							.append('@')
							.append(Config.getDefaultDomain())
							.toString();
	}
	
	public void createPublicFolder(final PublicFolder folder) {
		getTransactionTemplate().execute(
				new TransactionCallbackWithoutResult() {
					protected void doInTransactionWithoutResult(
							TransactionStatus status) {
						try {
							MailboxDao dao = HwDaoFactory.getMailboxDao();
							dao.createMailbox(0, folder.getFullName());
							setSubmissionAddr(folder);
						} catch (DataAccessException ex) {
							status.setRollbackOnly();
							throw ex;
						}
					}
				});
	}
	
	public void updatePublicFolder(final PublicFolder folder) {
		final PublicFolder org = getPublicFolder(folder.getNamespace(), folder.getMailboxID());
		if (!folder.equals(org)) {
			getTransactionTemplate().execute(
					new TransactionCallbackWithoutResult() {
						protected void doInTransactionWithoutResult(
								TransactionStatus status) {
							try {
								if (!StringUtils.equals(org.getName(), folder.getName())) {
									MailboxDao dao = HwDaoFactory.getMailboxDao();
									dao.renameMailbox(org.getMailbox(), folder.getFullName());
								}
								setSubmissionAddr(folder);
							} catch (DataAccessException ex) {
								status.setRollbackOnly();
								throw ex;
							}
						}
					});
		}
	}
	
	public PublicFolder getPublicFolder(String namespace, long mailboxID) {
		return HwDaoFactory.getHwUserDao().getPublicFolder(namespace, mailboxID);
	}

	public List<PublicFolder> getPublicFolders(long ownerid, String namespace) {
		return HwDaoFactory.getHwUserDao().getPublicFolders(ownerid, namespace);
	}

	private void setSubmissionAddr(final PublicFolder folder) {
		HwUserDao dao = HwDaoFactory.getHwUserDao();
		if (folder.getAliasID() != 0) {
			dao.deleteAlias(folder.getAliasID());
		}
		if (StringUtils.isNotBlank(folder.getSubmissionAddress())) {
			Alias alias = new Alias();
			alias.setAlias(folder.getSubmissionAddress());
			alias.setDeliverTo(folder.getFullName());
			dao.addAlias(alias);
		}
	}
	
}
