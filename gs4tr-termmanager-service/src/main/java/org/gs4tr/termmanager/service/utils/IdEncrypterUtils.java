package org.gs4tr.termmanager.service.utils;

import java.util.ArrayList;
import java.util.List;

import org.gs4tr.foundation.modules.entities.model.Ticket;
import org.gs4tr.foundation3.core.utils.IdEncrypter;

public class IdEncrypterUtils {
    public static Long decryptGenericId(String message) {
	return IdEncrypter.decryptGenericId(message);
    }

    public static List<Long> decryptGenericIds(List<String> messages) {
	List<Long> decryptedIds = new ArrayList<Long>();

	for (String message : messages) {
	    decryptedIds.add(IdEncrypter.decryptGenericId(message));
	}

	return decryptedIds;
    }

    public static List<Long> decryptGenericTickets(List<Ticket> tickets) {
	List<Long> decryptedIds = new ArrayList<Long>();

	for (Ticket ticket : tickets) {
	    decryptedIds.add(IdEncrypter.decryptGenericId(ticket.getTicketId()));
	}

	return decryptedIds;
    }

    public static String encryptGenericId(Long id) {
	return IdEncrypter.encryptGenericId(id);
    }

    public static List<String> encryptGenericIds(List<Long> ids) {
	List<String> encryptedIds = new ArrayList<String>();

	for (Long id : ids) {
	    encryptedIds.add(IdEncrypter.encryptGenericId(id));
	}

	return encryptedIds;
    }

    public static List<Ticket> encryptGenericTickets(List<Long> ids) {
	List<Ticket> encryptedIds = new ArrayList<Ticket>();

	for (Long id : ids) {
	    encryptedIds.add(new Ticket(IdEncrypter.encryptGenericId(id)));
	}

	return encryptedIds;
    }
}
