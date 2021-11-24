package org.gs4tr.termmanager.webservice.rest.template.runner;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.dto.Description;
import org.gs4tr.termmanager.model.dto.TermV2Model;
import org.gs4tr.termmanager.model.dto.TermV2ModelExtended;
import org.gs4tr.termmanager.model.dto.converter.TicketConverter;
import org.gs4tr.termmanager.webservice.model.request.AddTermsCommand;
import org.gs4tr.termmanager.webservice.model.response.SearchTermEntryResponse;
import org.gs4tr.termmanager.webservice.model.response.TermEntryHit;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WsV2TestRunner {

    private static final String DE = "de"; //$NON-NLS-1$

    // private static final boolean FETCH_LANGUAGES = true;

    private static final String FR = "fr"; //$NON-NLS-1$

    private static final int MAX_NUM = 100;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // private static final String SHORT_CODE = "SKY000001"; //$NON-NLS-1$

    public static void main(String[] args) throws URISyntaxException {

	try {

	    String securityTicket = WsV2TestClient.loginPost("super", "password1!");

	    String projectTicket = convertProjectId(1L);

	    List<String> targets = new ArrayList<String>();
	    targets.add("de-DE");

	    String segmentTermHits = WsV2TestClient.batchSegmentTermSearch(securityTicket, projectTicket, false, false,
		    "en-US", targets, MAX_NUM, Arrays.asList("Just an ordinary sentence sourceTerm1."));

	    System.out.println(String.format("Segment term search: %s %n", segmentTermHits));

	    String addTermEntry = WsV2TestClient.addTermEntry(securityTicket, projectTicket);

	    System.out.println(String.format("Add term entry: %s %n", addTermEntry));

	    String termEntrySearch = WsV2TestClient.termEntrySearch(securityTicket, 100, projectTicket, true, "en-US",
		    targets, null, null, "two");

	    System.out.println(String.format("Term entry search search: %s %n", termEntrySearch));

	    SearchTermEntryResponse response = OBJECT_MAPPER.readValue(termEntrySearch, SearchTermEntryResponse.class);

	    AddTermsCommand command = createUpdateCommand(response, projectTicket, securityTicket);

	    String updateTermEntry = WsV2TestClient.updateTermEntry(command);

	    System.out.println(String.format("Update term entry: %s %n", updateTermEntry));

	    String searchResult = WsV2TestClient.termEntrySearch(securityTicket, 100, projectTicket, true, "en-US",
		    targets, null, null, "editedTwo");

	    String projectMetadata = WsV2TestClient.getProjectMetadata(securityTicket, null, "Emisia", null, null,
		    null);

	    System.out.println(String.format("Project metadata: %s %n", projectMetadata));

	    System.out.println(String.format("Term entry search search: %s %n", searchResult));

	    String logout = WsV2TestClient.logoutPost(securityTicket);

	    System.out.println(String.format("Logout success: %s", logout));

	} catch (Exception exception) {
	    exception.printStackTrace();
	}
    }

    private static String convertProjectId(Long projectId) {
	return TicketConverter.fromInternalToDto(projectId);
    }

    private static AddTermsCommand createUpdateCommand(SearchTermEntryResponse response, String projectTicket,
	    String securityTicket) {
	TermEntryHit termEntryHit = response.getTermEntryHits().iterator().next();
	TermV2Model term = termEntryHit.getSources().getTerms().get(0);

	Description description = term.getDescriptions().iterator().next();
	description.setValue("editedDescription");

	List<Description> descriptions = new ArrayList<>();
	descriptions.add(description);

	List<TermV2ModelExtended> terms = new ArrayList<>();
	TermV2ModelExtended termTerm = new TermV2ModelExtended();
	termTerm.setLocale("en-US");
	termTerm.setDescriptions(descriptions);
	termTerm.setTermId(term.getTermId());
	termTerm.setTermText("editedTwo");

	terms.add(termTerm);

	AddTermsCommand command = new AddTermsCommand();
	command.setProjectTicket(projectTicket);
	command.setSecurityTicket(securityTicket);
	command.setTermEntryId(termEntryHit.getTermEntryId());
	command.setTerms(terms);

	return command;
    }
}
