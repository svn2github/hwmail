-- hedwig Oracle schema
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--    http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.


--
-- Table `hw_alias`
--
CREATE SEQUENCE sq_hw_alias INCREMENT BY 1 START WITH 1;
CREATE TABLE hw_alias (
  aliasid number NOT NULL,
  alias varchar(100) NOT NULL,
  deliver_to varchar(100) NOT NULL
);
CREATE UNIQUE INDEX pk_hw_alias ON hw_alias (aliasid);
CREATE UNIQUE INDEX ux_hw_alias_1 ON hw_alias (alias ASC, deliver_to);
ALTER TABLE hw_alias ADD CONSTRAINT pk_hw_alias PRIMARY KEY (aliasid);


--
-- Table `hw_user`
--
CREATE SEQUENCE sq_hw_user INCREMENT BY 1 START WITH 1;
CREATE TABLE hw_user (
  userid number NOT NULL,
  loginid varchar(100) NOT NULL,
  passwd varchar(34) NOT NULL,
  name varchar(60) NULL,
  maxmail_size number DEFAULT 0 NOT NULL,
  forward varchar(100) NULL
);
CREATE UNIQUE INDEX pk_hw_user ON hw_user (userid);
CREATE UNIQUE INDEX ux_hw_user_1 ON hw_user (loginid);
ALTER TABLE hw_user ADD CONSTRAINT pk_hw_user PRIMARY KEY (userid);

--
-- Table `hw_mailbox`
--
CREATE SEQUENCE sq_hw_mailbox INCREMENT BY 1 START WITH 1;
CREATE TABLE hw_mailbox (
  mailboxid number NOT NULL,
  ownerid number NOT NULL,
  name varchar(255) NOT NULL,
  noinferiors_flag char(1) DEFAULT 'N' NOT NULL,
  noselect_flag char(1) DEFAULT 'N' NOT NULL,
  readonly_flag char(1) DEFAULT 'N' NOT NULL,
  nextuid number NOT NULL,
  uidvalidity number NOT NULL
);
CREATE UNIQUE INDEX pk_hw_mailbox ON hw_mailbox (mailboxid);
CREATE INDEX ix_hw_mailbox_1 ON hw_mailbox (ownerid);
CREATE INDEX ix_hw_mailbox_2 ON hw_mailbox (name ASC);
ALTER TABLE hw_mailbox ADD CONSTRAINT pk_hw_mailbox PRIMARY KEY (mailboxid);


--
-- Table `hw_subscription`
--
CREATE TABLE hw_subscription (
  mailboxid number NOT NULL,
  userid number NOT NULL,
  name varchar(255) NOT NULL 
);
CREATE UNIQUE INDEX pk_hw_subscription ON hw_subscription (userid ASC, name ASC);
ALTER TABLE hw_subscription ADD CONSTRAINT pk_hw_subscription PRIMARY KEY (userid, name);


--
-- Table `hw_physmessage`
--
CREATE SEQUENCE sq_hw_physmessage INCREMENT BY 1 START WITH 1;
CREATE TABLE hw_physmessage (
  physmessageid number NOT NULL,
  rfcsize number NOT NULL,
  internaldate timestamp NOT NULL,
  subject varchar(500) NULL,
  sentdate timestamp NULL,
  fromaddr varchar(100) DEFAULT ''
);
CREATE UNIQUE INDEX pk_hw_phymessage ON hw_physmessage (physmessageid);
ALTER TABLE hw_physmessage ADD CONSTRAINT pk_hw_phymessage PRIMARY KEY (physmessageid);


--
-- Table `hw_message`
--
CREATE SEQUENCE sq_hw_message INCREMENT BY 1 START WITH 1;
CREATE TABLE hw_message (
  messageid number NOT NULL,
  mailboxid number NOT NULL,
  physmessageid number NOT NULL,
  seen_flag char(1) DEFAULT 'N' NOT NULL,
  answered_flag char(1) DEFAULT 'N' NOT NULL,
  deleted_flag char(1) DEFAULT 'N' NOT NULL,
  flagged_flag char(1) DEFAULT 'N' NOT NULL,
  recent_flag char(1) DEFAULT 'Y' NOT NULL,
  draft_flag char(1) DEFAULT 'N' NOT NULL
);
CREATE UNIQUE INDEX pk_hw_message ON hw_message (messageid);
CREATE INDEX ix_hw_message_1 ON hw_message (mailboxid);
CREATE INDEX ix_hw_message_2 ON hw_message (physmessageid);
ALTER TABLE hw_message ADD CONSTRAINT pk_hw_message PRIMARY KEY (messageid);
CREATE OR REPLACE TRIGGER trg_ai_hw_message AFTER INSERT ON hw_message FOR EACH ROW
BEGIN
    UPDATE hw_mailbox SET nextuid=:new.messageid+1 WHERE mailboxid=:new.mailboxid;
END;
/


--
-- Table `hw_headername`
--
CREATE SEQUENCE sq_hw_headername INCREMENT BY 1 START WITH 1;
CREATE TABLE hw_headername (
  headernameid number NOT NULL,
  headername varchar(100) NOT NULL
);
CREATE UNIQUE INDEX pk_hw_headername ON hw_headername (headernameid);
ALTER TABLE hw_headername ADD CONSTRAINT PK_hw_headername PRIMARY KEY (headernameid);


--
-- Table `hw_headervalue`
--
CREATE SEQUENCE sq_hw_headervalue INCREMENT BY 1 START WITH 1;
CREATE TABLE hw_headervalue (
  headervalueid number NOT NULL,
  physmessageid number NOT NULL,
  headernameid number NOT NULL,
  headervalue varchar(4000)
);
CREATE UNIQUE INDEX pk_hw_headervalue ON hw_headervalue (headervalueid);
CREATE INDEX ix_hw_headervalue_1 ON hw_headervalue (headernameid);
CREATE INDEX ix_hw_headervalue_2 ON hw_headervalue (physmessageid);
ALTER TABLE hw_headervalue ADD CONSTRAINT pk_hw_headervalue PRIMARY KEY (headervalueid);


--
-- Table `hw_acl`
--
CREATE TABLE hw_acl (
	userid NUMBER NOT NULL,
	mailboxid NUMBER NOT NULL,
	lookup_flag CHAR(1) DEFAULT 'N' NOT NULL,
	read_flag CHAR(1) DEFAULT 'N' NOT NULL,
	seen_flag CHAR(1) DEFAULT 'N' NOT NULL,
	write_flag CHAR(1) DEFAULT 'N' NOT NULL,
	insert_flag CHAR(1) DEFAULT 'N' NOT NULL,
	post_flag CHAR(1) DEFAULT 'N' NOT NULL,
	create_flag CHAR(1) DEFAULT 'N' NOT NULL,
	delete_flag CHAR(1) DEFAULT 'N' NOT NULL,
	deletemsg_flag CHAR(1) DEFAULT 'N' NOT NULL,
	expunge_flag CHAR(1) DEFAULT 'N' NOT NULL,
	admin_flag CHAR(1) DEFAULT 'N' NOT NULL
);
CREATE UNIQUE INDEX pk_hw_acl ON hw_acl (userid ASC, mailboxid);
CREATE INDEX ix_hw_acl_1 ON hw_acl (mailboxid);
ALTER TABLE hw_acl ADD CONSTRAINT pk_hw_acl PRIMARY KEY (userid, mailboxid);


--
-- Table `hw_keyword`
--
CREATE TABLE hw_keyword (
  messageid number NOT NULL,
  keyword varchar(255) NOT NULL 
);
CREATE INDEX ix_hw_keyword_1 ON hw_keyword (messageid);
