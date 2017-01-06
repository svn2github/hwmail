-- hedwig mysql schema
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


SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS hedwig;
CREATE SCHEMA hedwig;
USE hedwig;

--
-- Table `hw_alias`
--
DROP TABLE IF EXISTS `hw_alias` ;

CREATE  TABLE IF NOT EXISTS `hw_alias` (
  `aliasid` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `alias` VARCHAR(100) NOT NULL ,
  `deliver_to` VARCHAR(100) NOT NULL ,
  PRIMARY KEY (`aliasid`) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `ux_hw_alias_1` ON `hw_alias` (`alias` ASC, `deliver_to`) ;

-- -----------------------------------------------------
-- Table `hw_user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `hw_user` ;

CREATE  TABLE IF NOT EXISTS `hw_user` (
  `userid` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `loginid` VARCHAR(100) NOT NULL ,
  `passwd` VARCHAR(34) NOT NULL ,
  `name` VARCHAR(60) NULL ,
  `maxmail_size` BIGINT UNSIGNED NOT NULL DEFAULT '0',
  `forward` VARCHAR(100) NULL ,
  PRIMARY KEY (`userid`) )
ENGINE = InnoDB DEFAULT CHARSET=utf8;

CREATE UNIQUE INDEX `ux_hw_user_1` ON `hw_user` (`loginid` ASC) ;

-- -----------------------------------------------------
-- Table `hw_mailbox`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `hw_mailbox` ;

CREATE  TABLE IF NOT EXISTS `hw_mailbox` (
  `mailboxid` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `ownerid` BIGINT UNSIGNED NOT NULL ,
  `name` VARCHAR(255) NOT NULL ,
  `noinferiors_flag` CHAR NOT NULL DEFAULT 'N' ,
  `noselect_flag` CHAR NOT NULL DEFAULT 'N' ,
  `readonly_flag` CHAR NOT NULL DEFAULT 'N' ,
  `nextuid` BIGINT UNSIGNED NOT NULL ,
  `uidvalidity` BIGINT UNSIGNED NOT NULL ,
  PRIMARY KEY (`mailboxid`) )
ENGINE = InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX `ix_hw_mailbox_1` ON `hw_mailbox` (`ownerid`) ;

CREATE INDEX `ix_hw_mailbox_2` ON `hw_mailbox` (`name` ASC) ;


-- -----------------------------------------------------
-- Table `hw_subscription`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `hw_subscription` ;

CREATE  TABLE IF NOT EXISTS `hw_subscription` (
  `mailboxid` BIGINT UNSIGNED NOT NULL ,
  `userid` BIGINT UNSIGNED NOT NULL ,
  `name` VARCHAR(255) NOT NULL )
ENGINE = InnoDB DEFAULT CHARSET=utf8;

CREATE UNIQUE INDEX `pk_hw_subscription` ON `hw_subscription` (`userid` ASC, `name` ASC) ;

-- -----------------------------------------------------
-- Table `hw_physmessage`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `hw_physmessage` ;

CREATE  TABLE IF NOT EXISTS `hw_physmessage` (
  `physmessageid` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `rfcsize` BIGINT UNSIGNED NOT NULL ,
  `internaldate` DATETIME NOT NULL ,
  `subject` VARCHAR(500) NULL ,
  `sentdate` DATETIME NULL ,
  `fromaddr` VARCHAR(100) NULL DEFAULT '' ,
  PRIMARY KEY (`physmessageid`) )
ENGINE = InnoDB DEFAULT CHARSET=utf8;


-- -----------------------------------------------------
-- Table `hw_message`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `hw_message` ;

CREATE  TABLE IF NOT EXISTS `hw_message` (
  `messageid` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `mailboxid` BIGINT UNSIGNED NOT NULL ,
  `physmessageid` BIGINT UNSIGNED NOT NULL ,
  `seen_flag` CHAR NOT NULL DEFAULT 'N' ,
  `answered_flag` CHAR NOT NULL DEFAULT 'N' ,
  `deleted_flag` CHAR NOT NULL DEFAULT 'N' ,
  `flagged_flag` CHAR NOT NULL DEFAULT 'N' ,
  `recent_flag` CHAR NOT NULL DEFAULT 'Y' ,
  `draft_flag` CHAR NOT NULL DEFAULT 'N' ,
  PRIMARY KEY (`messageid`) )
ENGINE = InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX `ix_hw_message_1` ON `hw_message` (`mailboxid`) ;

CREATE INDEX `ix_hw_message_2` ON `hw_message` (`physmessageid`) ;


-- -----------------------------------------------------
-- Table `hw_headername`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `hw_headername` ;

CREATE  TABLE IF NOT EXISTS `hw_headername` (
  `headernameid` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `headername` VARCHAR(100) NOT NULL ,
  PRIMARY KEY (`headernameid`) )
ENGINE = InnoDB DEFAULT CHARSET=utf8;

CREATE UNIQUE INDEX `ux_hw_headername_1` ON `hw_headername` (`headername`) ;

-- -----------------------------------------------------
-- Table `hw_headervalue`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `hw_headervalue` ;

CREATE  TABLE IF NOT EXISTS `hw_headervalue` (
  `headervalueid` BIGINT NOT NULL AUTO_INCREMENT ,
  `physmessageid` BIGINT UNSIGNED NOT NULL ,
  `headernameid` BIGINT UNSIGNED NOT NULL ,
  `headervalue` TEXT NULL ,
  PRIMARY KEY (`headervalueid`) )
ENGINE = InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX `ix_hw_headervalue_1` ON `hw_headervalue` (`headernameid`) ;

CREATE INDEX `ix_hw_headervalue_2` ON `hw_headervalue` (`physmessageid`) ;


-- -----------------------------------------------------
-- Table `acl`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `hw_acl` ;

CREATE  TABLE IF NOT EXISTS `hw_acl` (
  `userid` BIGINT UNSIGNED NOT NULL ,
  `mailboxid` BIGINT UNSIGNED NOT NULL ,
  `lookup_flag` CHAR NOT NULL DEFAULT 'N' ,
  `read_flag` CHAR NOT NULL DEFAULT 'N' ,
  `seen_flag` CHAR NOT NULL DEFAULT 'N' ,
  `write_flag` CHAR NOT NULL DEFAULT 'N' ,
  `insert_flag` CHAR NOT NULL DEFAULT 'N' ,
  `post_flag` CHAR NOT NULL DEFAULT 'N' ,
  `create_flag` CHAR NOT NULL DEFAULT 'N' ,
  `delete_flag` CHAR NOT NULL DEFAULT 'N' ,
  `deletemsg_flag` CHAR NOT NULL DEFAULT 'N' ,
  `expunge_flag` CHAR NOT NULL DEFAULT 'N' ,
  `admin_flag` CHAR NOT NULL DEFAULT 'N' ,
  PRIMARY KEY (`userid`, `mailboxid`) )
ENGINE = InnoDB;

CREATE INDEX `ix_hw_acl_1` ON `hw_acl` (`mailboxid`) ;


-- -----------------------------------------------------
-- Table `hw_keyword`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `hw_keyword` ;

CREATE  TABLE IF NOT EXISTS `hw_keyword` (
  `messageid` BIGINT UNSIGNED NOT NULL ,
  `keyword` VARCHAR(255) NOT NULL )
ENGINE = InnoDB;

CREATE INDEX `ix_hw_keyword_1` ON `hw_keyword` (`messageid`) ;


DELIMITER //

DROP TRIGGER IF EXISTS `trg_ai_hw_message` //
CREATE TRIGGER trg_ai_hw_message AFTER INSERT ON hw_message FOR EACH ROW BEGIN
    UPDATE hw_mailbox SET nextuid=new.messageid+1 WHERE mailboxid=new.mailboxid;
END
//

DELIMITER ;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
