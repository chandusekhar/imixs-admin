package org.imixs.application.admin.mvc;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.mvc.Models;
import javax.mvc.annotation.Controller;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.imixs.application.admin.ConnectionController;
import org.imixs.application.admin.DataController;
import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.xml.XMLDocument;
import org.imixs.workflow.xml.XMLDocumentAdapter;

/**
 * The Connect controller is used to establish a connectio to Imixs-Worklfow
 * remote interface.
 * 
 * @author rsoika
 *
 */
@Controller
@Path("/backup")
public class BackupController implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(BackupController.class.getName());

	@Inject
	private Models model;

	@Inject
	ConnectionController connectionController;

	@Inject
	DataController dataController;

	public BackupController() {
		super();
	}


	@GET
	public String home() {
		return "backup.xhtml";
	}

	/**
	 * This method creates a backup or restore request based on the formParam
	 * 'action'
	 * 
	 * @param query
	 * @param path
	 * @return
	 */
	@POST
	public String backup(@FormParam("query") String query, @FormParam("backupPath") String path,
			@FormParam("action") String action) {
		logger.info("backup=" + query + " path=" + path);

		dataController.setQuery(query);
		dataController.setBackupPath(path);

		if ("backup".equals(action)) {
			String uri = connectionController.getWorkflowCLient().getBaseURI() + "documents/backup/" + query
					+ "?filepath=" + path;
			// create put for backup ...
			// here we create a dummmy object 
			ItemCollection itemCol=new ItemCollection();
			XMLDocument xmlItemCol=XMLDocumentAdapter.getDocument(itemCol);
			Response response = connectionController.getWorkflowCLient().getClient().target(uri).request().put(Entity.xml(xmlItemCol));
			model.put("backupstatus", response.getStatus());
		}

		if ("restore".equals(action)) {
			String uri = connectionController.getWorkflowCLient().getBaseURI() + "documents/restore?filepath=" + path;
			// create put for backup ...
			Response response = connectionController.getWorkflowCLient().getClient().target(uri).request().get();
			model.put("backupstatus", response.getStatus());
		}

		return "backup.xhtml";
	}

}