INSERT INTO GS_GROUP(`GROUP_ID`,`GROUP_NAME`, `GROUP_DESC`) VALUES(1, 'system', 'reserved system group');
INSERT INTO GS_USER(`ID`, `NAME`, `DISPLAY_NAME`, `USER_ROLE`) VALUES(1, 'admin', 'Administrator', 'ADMIN');
INSERT INTO GS_USER_GROUP(`USER_ID`, `GROUP_ID`) VALUES(1, 1);

INSERT INTO GS_GADGET(`GADGET_TITLE`,`GADGET_AUTHOR`,`GADGET_AUTHOR_EMAIL`,`GADGET_DESCRIPTION`,`GADGET_THUMBNAIL_URL`,`GADGET_URL`) VALUES('Response Time','Red Hat','cyu@redhat.com','Response Time Gadget','${server}/gadgets/rt-gadget/thumbnail.png','${server}/gadgets/rt-gadget/gadget.xml');
INSERT INTO GS_GADGET(`GADGET_TITLE`,`GADGET_AUTHOR`,`GADGET_AUTHOR_EMAIL`,`GADGET_DESCRIPTION`,`GADGET_THUMBNAIL_URL`,`GADGET_URL`) VALUES('Situation Gadget','Red Hat','cyu@redhat.com','Situation Gadget','${server}/gadgets/situation-gadget/thumbnail.png','${server}/gadgets/situation-gadget/gadget.xml');
INSERT INTO GS_GADGET(`GADGET_TITLE`,`GADGET_AUTHOR`,`GADGET_AUTHOR_EMAIL`,`GADGET_DESCRIPTION`,`GADGET_THUMBNAIL_URL`,`GADGET_URL`) VALUES('Call Trace Gadget','Red Hat','cyu@redhat.com','Call Trace Gadget','${server}/gadgets/calltrace-gadget/thumbnail.png','${server}/gadgets/calltrace-gadget/gadget.xml');
INSERT INTO GS_GADGET(`GADGET_TITLE`,`GADGET_AUTHOR`,`GADGET_AUTHOR_EMAIL`,`GADGET_DESCRIPTION`,`GADGET_THUMBNAIL_URL`,`GADGET_URL`) VALUES('Service Overview Gadget','Red Hat','cyu@redhat.com','Service Overview Gadget','${server}/gadgets/so-gadget/thumbnail.png','${server}/gadgets/so-gadget/gadget.xml');
