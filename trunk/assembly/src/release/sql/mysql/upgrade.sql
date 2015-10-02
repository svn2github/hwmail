USE hedwig;

-- -----------------------------------------------------
-- Revision 14
-- -----------------------------------------------------
-- ALTER TABLE user ADD COLUMN name VARCHAR(60) ;

-- -----------------------------------------------------
-- Revision 22
-- -----------------------------------------------------
-- ALTER TABLE physmessage MODIFY subject VARCHAR(500) NULL ;

-- -----------------------------------------------------
-- Revision 
-- -----------------------------------------------------
ALTER TABLE `alias` 
	DROP INDEX `fk_alias_user1` , 
	ADD INDEX `fk_hw_alias_user` (`deliver_to` ASC) ,
	RENAME TO `hw_alias` ;

ALTER TABLE `user` 
	DROP INDEX `uk_user_userid` , 
	ADD UNIQUE INDEX `uk_hw_user_userid` (`userid` ASC) ,
	RENAME TO `hw_user` ;

ALTER TABLE `mailbox` 
	CHANGE `noinferiors` `noinferiors_flag` CHAR NOT NULL DEFAULT 'N' ,
	CHANGE `noselect` `noselect_flag` CHAR NOT NULL DEFAULT 'N' ,
	CHANGE `readonly` `readonly_flag` CHAR NOT NULL DEFAULT 'N' ,
	DROP INDEX `fk_mailbox_user` , 
	ADD INDEX `ix_hw_mailbox_1` (`ownerid` ASC) ,
	RENAME TO `hw_mailbox` ;

ALTER TABLE `subscription` 
	DROP INDEX `uk_subscription` , 
	ADD UNIQUE INDEX `uk_hw_subscription` (`userid` ASC, `name` ASC) ,
	RENAME TO `hw_subscription` ;

ALTER TABLE `physmessage`
	CHANGE `size` `rfcsize` BIGINT UNSIGNED NOT NULL
	RENAME TO `hw_physmessage` ;

ALTER TABLE `message` 
	CHANGE `seen` `seen_flag` CHAR NOT NULL DEFAULT 'N' ,
	CHANGE `answered` `answered_flag` CHAR NOT NULL DEFAULT 'N' ,
	CHANGE `deleted` `deleted_flag` CHAR NOT NULL DEFAULT 'N' ,
	CHANGE `flagged` `flagged_flag` CHAR NOT NULL DEFAULT 'N' ,
	CHANGE `recent` `recent_flag` CHAR NOT NULL DEFAULT 'Y' ,
	CHANGE `draft` `draft_flag` CHAR NOT NULL DEFAULT 'N' ,
	DROP INDEX `fk_message_mailbox1` , 
	DROP INDEX `fk_message_physmessage1` , 
	ADD INDEX `fk_hw_message_mailbox` (`mailboxid` ASC) ,
	ADD INDEX `fk_hw_message_physmessage` (`physmessageid` ASC) ,
	RENAME TO `hw_message` ;

ALTER TABLE `headername` 
	DROP INDEX `uk_headername` , 
	ADD UNIQUE INDEX `uk_hw_headername` (`headername` ASC) ,
	RENAME TO `hw_headername` ;

ALTER TABLE `headervalue` 
	DROP INDEX `fk_headervalue_physmessage1`, 
	DROP INDEX `fk_headervalue_headername1` , 
	ADD INDEX `fk_hw_headervalue_physmessage`  (`physmessageid` ASC) ,
	ADD INDEX `fk_hw_headervalue_headername` (`headernameid` ASC) ,
	RENAME TO `hw_headervalue` ;

ALTER TABLE `acl` 
	CHANGE `lookup` `lookup_flag` CHAR NOT NULL DEFAULT 'N' ,
	CHANGE `read` `read_flag` CHAR NOT NULL DEFAULT 'N' ,
	CHANGE `seen` `seen_flag` CHAR NOT NULL DEFAULT 'N' ,
	CHANGE `write` `write_flag` CHAR NOT NULL DEFAULT 'N' ,
	CHANGE `insert` `insert_flag` CHAR NOT NULL DEFAULT 'N' ,
	CHANGE `post` `post_flag` CHAR NOT NULL DEFAULT 'N' ,
	CHANGE `create` `create_flag` CHAR NOT NULL DEFAULT 'N' ,
	CHANGE `delete` `delete_flag` CHAR NOT NULL DEFAULT 'N' ,
	CHANGE `deletemsg` `deletemsg_flag` CHAR NOT NULL DEFAULT 'N' ,
	CHANGE `expunge` `expunge_flag` CHAR NOT NULL DEFAULT 'N' ,
	CHANGE `admin` `admin_flag` CHAR NOT NULL DEFAULT 'N' ,
	DROP INDEX `fk_acl_user1` , 
	DROP INDEX `fk_acl_mailbox1` , 
	ADD INDEX `fk_hw_acl_user` (`user_id` ASC) ,
	ADD INDEX `fk_hw_acl_mailbox` (`mailboxid` ASC) ,
	RENAME TO `hw_acl` ;

ALTER TABLE `keyword` 
	DROP INDEX `fk_keyword_message1`, 
	ADD INDEX `fk_hw_keyword_message` (`messageid` ASC) ,
	RENAME TO `hw_keyword` ;

DELIMITER //

DROP TRIGGER `ins_message` //
CREATE TRIGGER ins_hw_message AFTER INSERT ON hw_message FOR EACH ROW BEGIN
    UPDATE hw_mailbox SET nextuid=new.messageid+1 WHERE mailboxid=new.mailboxid;
END
//

DELIMITER ;