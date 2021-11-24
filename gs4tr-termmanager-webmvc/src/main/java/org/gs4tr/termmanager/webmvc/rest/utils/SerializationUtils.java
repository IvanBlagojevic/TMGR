package org.gs4tr.termmanager.webmvc.rest.utils;

import org.gs4tr.termmanager.model.dto.Announcement;
import org.gs4tr.termmanager.model.dto.Attribute;
import org.gs4tr.termmanager.model.dto.Date;
import org.gs4tr.termmanager.model.dto.ExportInfo;
import org.gs4tr.termmanager.model.dto.ImportErrorMessage;
import org.gs4tr.termmanager.model.dto.ImportSummary;
import org.gs4tr.termmanager.model.dto.Language;
import org.gs4tr.termmanager.model.dto.Metadata;
import org.gs4tr.termmanager.model.dto.Project;
import org.gs4tr.termmanager.model.dto.ProjectInfo;
import org.gs4tr.termmanager.model.dto.Task;
import org.gs4tr.termmanager.model.dto.Term;
import org.gs4tr.termmanager.model.dto.TermEntry;
import org.gs4tr.termmanager.model.dto.TermSearchModel;
import org.gs4tr.termmanager.model.dto.pagedlist.PagedListInfo;
import org.gs4tr.termmanager.model.dto.pagedlist.TaskPagedList;

import com.thoughtworks.xstream.XStream;

public class SerializationUtils {

    private static final XStream X_STREAM = new XStream();

    public static String toXML(Object obj) {
	return X_STREAM.toXML(obj);
    }

    static {
	X_STREAM.processAnnotations(new Class[] { Announcement.class, Attribute.class, Date.class, ExportInfo.class,
		ImportErrorMessage.class, ImportSummary.class, Language.class, Metadata.class, PagedListInfo.class,
		Project.class, ProjectInfo.class, Term.class, TermEntry.class, TaskPagedList.class, Task.class,
		TermSearchModel.class });
    }
}
