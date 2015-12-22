CREATE MEMORY TABLE hw_physmessage (
  physmessageid integer PRIMARY KEY,
  rfcsize integer NOT NULL,
  internaldate timestamp NOT NULL,
  subject varchar(500) NULL,
  sentdate timestamp NULL,
  fromaddr varchar(100) DEFAULT ''
);


CREATE MEMORY TABLE hw_message (
  messageid integer PRIMARY KEY,
  mailboxid integer NOT NULL,
  physmessageid integer NOT NULL,
  seen_flag char(1) DEFAULT 'N' NOT NULL,
  answered_flag char(1) DEFAULT 'N' NOT NULL,
  deleted_flag char(1) DEFAULT 'N' NOT NULL,
  flagged_flag char(1) DEFAULT 'N' NOT NULL,
  recent_flag char(1) DEFAULT 'Y' NOT NULL,
  draft_flag char(1) DEFAULT 'N' NOT NULL
);


CREATE MEMORY TABLE hw_headername (
  headernameid integer PRIMARY KEY,
  headername varchar(100) NOT NULL
);


CREATE MEMORY TABLE hw_headervalue (
  headervalueid integer PRIMARY KEY,
  physmessageid integer NOT NULL,
  headernameid integer NOT NULL,
  headervalue varchar(4000)
);


CREATE MEMORY TABLE hw_keyword (
  messageid integer PRIMARY KEY,
  keyword varchar(255) NOT NULL 
);

