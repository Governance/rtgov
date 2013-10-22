INSERT INTO GS_GROUP(`GROUP_ID`,`GROUP_NAME`, `GROUP_DESC`) VALUES(1, 'system', 'reserved system group');
INSERT INTO GS_USER(`ID`, `NAME`, `DISPLAY_NAME`, `USER_ROLE`) VALUES(1, 'admin', 'Administrator', 'ADMIN');
INSERT INTO GS_USER_GROUP(`USER_ID`, `GROUP_ID`) VALUES(1, 1);

INSERT INTO GS_GADGET(`GADGET_TITLE`,`GADGET_AUTHOR`,`GADGET_AUTHOR_EMAIL`,`GADGET_DESCRIPTION`,`GADGET_THUMBNAIL_URL`,`GADGET_URL`) VALUES('Response Time','Red Hat','cyu@redhat.com','Response Time Gadget','${server}/gadgets/rt-gadget/thumbnail.png','${server}/gadgets/rt-gadget/gadget.xml');
INSERT INTO GS_GADGET(`GADGET_TITLE`,`GADGET_AUTHOR`,`GADGET_AUTHOR_EMAIL`,`GADGET_DESCRIPTION`,`GADGET_THUMBNAIL_URL`,`GADGET_URL`) VALUES('Situation Gadget','Red Hat','cyu@redhat.com','Situation Gadget','${server}/gadgets/situation-gadget/thumbnail.png','${server}/gadgets/situation-gadget/gadget.xml');
INSERT INTO GS_GADGET(`GADGET_TITLE`,`GADGET_AUTHOR`,`GADGET_AUTHOR_EMAIL`,`GADGET_DESCRIPTION`,`GADGET_THUMBNAIL_URL`,`GADGET_URL`) VALUES('Call Trace Gadget','Red Hat','cyu@redhat.com','Call Trace Gadget','${server}/gadgets/calltrace-gadget/thumbnail.png','${server}/gadgets/calltrace-gadget/gadget.xml');
INSERT INTO GS_GADGET(`GADGET_TITLE`,`GADGET_AUTHOR`,`GADGET_AUTHOR_EMAIL`,`GADGET_DESCRIPTION`,`GADGET_THUMBNAIL_URL`,`GADGET_URL`) VALUES('Service Overview Gadget','Red Hat','cyu@redhat.com','Service Overview Gadget','${server}/gadgets/so-gadget/thumbnail.png','${server}/gadgets/so-gadget/gadget.xml');


INSERT INTO GS_PAGE
(`PAGE_ID`,`PAGE_COLUMNS`,`PAGE_NAME`,`PAGE_ORDER`,`USER_ID`)
VALUES(1,2,'Dashboard',0,1);

INSERT INTO GS_WIDGET
(`WIDGET_ID`,`WIDGET_URL`,`WIDGET_NAME`,`WIDGET_ORDER`,`PAGE_PAGE_ID`)
VALUES(1,'${server}/gadgets/rt-gadget/gadget.xml','Response Time',1,1);
INSERT INTO GS_WIDGET
(`WIDGET_ID`,`WIDGET_URL`,`WIDGET_NAME`,`WIDGET_ORDER`,`PAGE_PAGE_ID`)
VALUES(2,'${server}/gadgets/situation-gadget/gadget.xml','Situation Gadget',2,1);
INSERT INTO GS_WIDGET
(`WIDGET_ID`,`WIDGET_URL`,`WIDGET_NAME`,`WIDGET_ORDER`,`PAGE_PAGE_ID`)
VALUES(3,'${server}/gadgets/calltrace-gadget/gadget.xml','Call Trace Gadget',3,1);
INSERT INTO GS_WIDGET
(`WIDGET_ID`,`WIDGET_URL`,`WIDGET_NAME`,`WIDGET_ORDER`,`PAGE_PAGE_ID`)
VALUES(4,'${server}/gadgets/so-gadget/gadget.xml','Service Overview Gadget',4,1);

