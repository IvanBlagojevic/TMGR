package org.gs4tr.termmanager.model.dto;

public class TermProject {

    private Announcement[] _announcements;

    private String _ticket;

    public Announcement[] getAnnouncements() {
	return _announcements;
    }

    public String getTicket() {
	return _ticket;
    }

    public void setAnnouncements(Announcement[] announcements) {
	_announcements = announcements;
    }

    public void setTicket(String ticket) {
	_ticket = ticket;
    }

}
