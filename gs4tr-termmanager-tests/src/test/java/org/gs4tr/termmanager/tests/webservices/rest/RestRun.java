package org.gs4tr.termmanager.tests.webservices.rest;

import org.gs4tr.termmanager.tests.webservices.ConnectionDetails;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.gs4tr.termmanager.tests.webservices.rest.RestClient.exportDocument;

public class RestRun {

    public static void main(String[] args) {

        RestTerm sourceTerm = createTerm("sourceTermWF", "en-US", "sourceDescription");
        RestTerm targetTerm = createTerm("targetTermWF", "no-NO", "");

        RestExportSearchRequest detailedExportRequest = createSearchRequestForDetailedExport("1510268400000", "en-US",
                "no-NO", "JSON", "true", "true", null);



        RestExportSearchRequest exportRequest = createSearchRequestForExport("0", "en-US", "de-DE",
                "TBX", "false", null, "false", "context");





        ConnectionDetails connectionDetails = new ConnectionDetails(
                "tmgr://localhost:8080/TMGR?prj=TES000001&usr=power&pwd=password1!&src=en-US&tgt=de-DE");

        File file = new File("gs4tr-termmanager-tests/target/testFile.tbx");
        byte[] body = null;

        String result = exportDocument(connectionDetails, exportRequest);

        System.out.println(result);

    }

    private static byte[] createBody(File file) throws IOException {

        byte[] body = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(body);
        fis.close();

        return body;

    }

    private static RestExportSearchRequest createSearchRequestForDetailedExport(String date, String sourceLocale,
                                                                                String targetLocale, String exportFormat, String exportForbiddenTerms, String exportAllDescriptions,
                                                                                String blacklistTermsCount) {

        RestExportSearchRequest request = new RestExportSearchRequest();

        request.setSourceLocale(sourceLocale);
        request.setTargetLocale(targetLocale);
        request.setExportFormat(exportFormat);
        request.setExportForbiddenTerms(exportForbiddenTerms);
        request.setExportAllDescriptions(exportAllDescriptions);
        request.setBlacklistTermsCount(blacklistTermsCount);
        request.setAfterDate(date);

        return request;
    }

    private static RestExportSearchRequest createSearchRequestForExport(String date, String sourceLocale,
                                                                        String targetLocale, String exportFormat, String exportForbiddenTerms, String exportAllDescriptions,
                                                                        String generateStatistics, String descriptionType) {

        RestExportSearchRequest request = new RestExportSearchRequest();

        request.setSourceLocale(sourceLocale);
        request.setTargetLocale(targetLocale);
        request.setExportFormat(exportFormat);
        request.setExportForbiddenTerms(exportForbiddenTerms);
        request.setExportAllDescriptions(exportAllDescriptions);
        request.setGenerateStatistics(generateStatistics);
        request.setDescriptionType(descriptionType);
        request.setAfterDate(date);

        return request;
    }

    private static RestTerm createTerm(String termName, String languageId, String descrption) {

        RestTerm term = new RestTerm();
        term.setTermName(termName);
        term.setLanguageId(languageId);
        term.setTermDescription(descrption);

        return term;

    }

}
