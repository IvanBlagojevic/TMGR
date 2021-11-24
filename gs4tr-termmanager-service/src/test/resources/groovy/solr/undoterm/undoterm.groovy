import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTerm;
import org.gs4tr.termmanager.model.glossary.backup.submission.DbSubmissionTermEntry;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;

uuid="1"
/*First revision*/
DbSubmissionTermEntry temp=new DbSubmissionTermEntry()
temp.setUuId(uuid)
temp.setRevisionId(1)
Set<DbSubmissionTerm> submissionTerms = new HashSet<>()
DbSubmissionTerm enTerm=new DbSubmissionTerm()
enTerm.setLanguageId("en-US")
enTerm.setDateModified(new Date(1))
enTerm.setNameAsBytes("currentEnValue")
enTerm.setTempText("currentEnValue")
enTerm.setUuId("2")
enTerm.setCanceled(false);

submissionTerms.add(enTerm)

DbSubmissionTerm deTerm=new DbSubmissionTerm()
deTerm.setLanguageId("de-US")
deTerm.setDateModified(new Date(2))
deTerm.setNameAsBytes("currentDeValue")
deTerm.setTempText("currentEnValue")
deTerm.setUuId("3")
deTerm.setCanceled(false);

submissionTerms.add(deTerm)

DbSubmissionTerm frTerm=new DbSubmissionTerm()
frTerm.setLanguageId("fr-CA")
frTerm.setDateModified(new Date(3))
frTerm.setNameAsBytes("currentFrValue")
frTerm.setTempText("currentFrValue")
frTerm.setUuId("4")
frTerm.setCanceled(false);

submissionTerms.add(frTerm)

temp.setSubmissionTerms(submissionTerms)
firstRevision=temp
/*second revision */

DbSubmissionTermEntry temp1=new DbSubmissionTermEntry()
temp1.setUuId(uuid)
temp1.setRevisionId(2)
Set<DbSubmissionTerm> submissionTerms1 = new HashSet<>()
DbSubmissionTerm enTerm1=new DbSubmissionTerm()
enTerm1.setLanguageId("en-US")
enTerm1.setDateModified(new Date(11L))
enTerm1.setNameAsBytes("currentEnValue")
enTerm1.setTempText("currentEnValue")
enTerm1.setUuId("2")
enTerm1.setCanceled(false);

submissionTerms1.add(enTerm1)

DbSubmissionTerm deTerm1=new DbSubmissionTerm()
deTerm1.setLanguageId("de-US")
deTerm1.setDateModified(new Date(12L))
deTerm1.setNameAsBytes("currentDeValue")
deTerm1.setTempText("currentEnValue")
deTerm1.setUuId("3")
deTerm1.setCanceled(false);

submissionTerms1.add(deTerm1)

DbSubmissionTerm frTerm1=new DbSubmissionTerm()
frTerm1.setLanguageId("fr-CA")
frTerm1.setDateModified(new Date(13L))
frTerm1.setNameAsBytes("currentFrValue")
frTerm1.setTempText("currentFrValue")
frTerm1.setUuId("4")
frTerm1.setCanceled(false);

submissionTerms1.add(frTerm1)

DbSubmissionTerm itTerm1=new DbSubmissionTerm()
itTerm1.setLanguageId("it")
itTerm1.setDateModified(new Date(14L))
itTerm1.setNameAsBytes("currentItValue")
itTerm1.setTempText("currentItValue")
itTerm1.setUuId("5")
itTerm1.setCanceled(false);

submissionTerms1.add(itTerm1)

temp1.setSubmissionTerms(submissionTerms1)
secondRevision=temp1

/*Third revision*/

DbSubmissionTermEntry temp2=new DbSubmissionTermEntry()
temp2.setUuId(uuid)
temp2.setRevisionId(3)
Set<DbSubmissionTerm> submissionTerms2 = new HashSet<>()
DbSubmissionTerm enTerm2=new DbSubmissionTerm()
enTerm2.setLanguageId("en-US")
enTerm2.setDateModified(new Date(11L))
enTerm2.setNameAsBytes("currentEnValue")
enTerm2.setTempText("currentEnValue")
enTerm2.setUuId("2")
enTerm2.setCanceled(false);

submissionTerms2.add(enTerm2)

DbSubmissionTerm deTerm2=new DbSubmissionTerm()
deTerm2.setLanguageId("de-US")
deTerm2.setDateModified(new Date(12L))
deTerm2.setNameAsBytes("currentDeValue")
deTerm2.setTempText("currentEnValue")
deTerm2.setUuId("3")
deTerm2.setCanceled(false);

submissionTerms2.add(deTerm2)

DbSubmissionTerm frTerm2=new DbSubmissionTerm()
frTerm2.setLanguageId("fr-CA")
frTerm2.setDateModified(new Date(13L))
frTerm2.setNameAsBytes("currentFrValue")
frTerm2.setTempText("currentFrValue")
frTerm2.setUuId("4")
frTerm2.setCanceled(false);

submissionTerms2.add(frTerm2)

DbSubmissionTerm itTerm2=new DbSubmissionTerm()
itTerm2.setLanguageId("it")
itTerm2.setDateModified(new Date(16L))
itTerm2.setNameAsBytes("currentItValue")
itTerm2.setTempText("changed currentItValue")
itTerm2.setUuId("5")
itTerm2.setCanceled(false);

submissionTerms2.add(itTerm2)

temp2.setSubmissionTerms(submissionTerms2)
thirdRevision=temp2

