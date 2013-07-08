package org.freeplane.plugin.remote.client.services;

import java.io.PrintStream;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.output.NullOutputStream;
import org.docear.messages.models.MapIdentifier;
import org.freeplane.core.util.LogUtils;
import org.freeplane.plugin.remote.client.ClientController;
import org.freeplane.plugin.remote.client.User;
import org.freeplane.plugin.remote.v10.model.updates.AddNodeUpdate;
import org.freeplane.plugin.remote.v10.model.updates.ChangeEdgeAttributeUpdate;
import org.freeplane.plugin.remote.v10.model.updates.ChangeNodeAttributeUpdate;
import org.freeplane.plugin.remote.v10.model.updates.DeleteNodeUpdate;
import org.freeplane.plugin.remote.v10.model.updates.MapUpdate;
import org.freeplane.plugin.remote.v10.model.updates.MoveNodeUpdate;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.dispatch.Futures;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class DocearOnlineWs implements WS {
	private final ClientController clientController;
	private final String serviceUrl = "http://localhost:9000/api/v1";
	// private final String serviceUrl = "https://staging.my.docear.org";
	private final Client restClient;

	public DocearOnlineWs(ClientController clientController) {
		this.clientController = clientController;
		
		/**
		 * important! WS does not run properly without the logging filter.
		 * Why? No Idea...
		 */
		PrintStream stream = new PrintStream(new NullOutputStream());
		restClient = ApacheHttpClient.create();
		restClient.addFilter(new LoggingFilter(stream));

		final String source = clientController.source();
		restClient.addFilter(new ClientFilter() {

			@Override
			public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
				String uriString = request.getURI().toASCIIString();
				uriString = uriString.contains("?") ? uriString + "&" : uriString + "?";

				final URI newUri = URI.create(uriString + "source=" + source);
				request.setURI(newUri);

				return getNext().handle(request);
			}
		});
	}

	@Override
	@Deprecated
	public Future<User> login(final String username, final String password) {
		final WebResource loginResource = restClient.resource(serviceUrl).path("user/login");
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("username", username);
		formData.add("password", password);
		final ClientResponse loginResponse = loginResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);

		if (loginResponse.getStatus() == 200) {
			final User user = new User(username, loginResponse.getEntity(String.class));
			return Futures.successful(user);
		} else {
			return null;
		}
	}

	@Override
	public Future<Boolean> listenIfUpdatesOccur(final User user, final MapIdentifier mapIdentifier) {
		return Futures.future(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				final WebResource resource = preparedResource(user).path("project/" + mapIdentifier.getProjectId() + "/map/" + mapIdentifier.getMapId() + "/listen");

				final ClientResponse loginResponse = resource.get(ClientResponse.class);
				return loginResponse.getStatus() == 200;
			}
		}, clientController.system().dispatcher());

	}
	
	@Override
	public List<Project> getProjectsForUser(final User user) {
		try {
			final WebResource projectsResource = preparedResource(user).path("user/projects");
			final Project[] projects = new ObjectMapper().readValue(projectsResource.get(String.class),Project[].class);
			
			return Arrays.asList(projects);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Project getProject(final User user,String projectId) {
		try {
			final WebResource projectResource = preparedResource(user).path("project/"+projectId);
			final Project projects = new ObjectMapper().readValue(projectResource.get(String.class),Project.class);
			
			return projects;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void createMindmap(User user, MapIdentifier mapIdentifier) {
		try {
			final WebResource createResource = preparedResource(user).path("project/" + mapIdentifier.getProjectId() + "/map/create");
			final MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
			formData.add("path", mapIdentifier.getMapId());
			
			createResource.post(ClientResponse.class,formData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Future<MapAsXmlResponse> getMapAsXml(final User user, final MapIdentifier mapIdentifier) {		
		try {
			final String urlEncodedPath = URLEncoder.encode(mapIdentifier.getMapId(),"UTF-8");
			final WebResource mapAsXmlResource = preparedResource(user).path("project/" + mapIdentifier.getProjectId() + "/map/" + urlEncodedPath + "/xml");
			final ClientResponse response = mapAsXmlResource.get(ClientResponse.class);
			final String xmlString = response.getEntity(String.class);
			
			final long revision = Long.parseLong(response.getHeaders().get("currentRevision").get(0));
			return Futures.successful(new MapAsXmlResponse(xmlString, revision));
		} catch (Exception e) {
			e.printStackTrace();
			return Futures.failed(e);
		}
	}

	@Override
	public Future<GetUpdatesResponse> getUpdatesSinceRevision(final User user, final MapIdentifier mapIdentifier, final int sinceRevision) {

		int currentRevision = -1;
		List<MapUpdate> updates = new ArrayList<MapUpdate>();
		final WebResource fetchUpdates = preparedResource(user).path("project/" + mapIdentifier.getProjectId() + "/map/" + mapIdentifier.getMapId() + "/updates/" + sinceRevision);
		final ClientResponse response = fetchUpdates.get(ClientResponse.class);
		final ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode json = mapper.readTree(response.getEntity(String.class));
			currentRevision = json.get("currentRevision").asInt();

			Iterator<JsonNode> it = json.get("orderedUpdates").iterator();
			while (it.hasNext()) {
				final JsonNode mapUpdateJson = it.next();

				final MapUpdate.Type type = MapUpdate.Type.valueOf(mapUpdateJson.get("type").asText());
				switch (type) {
				case AddNode:
					updates.add(mapper.treeToValue(mapUpdateJson, AddNodeUpdate.class));
					break;
				case ChangeNodeAttribute:
					updates.add(mapper.treeToValue(mapUpdateJson, ChangeNodeAttributeUpdate.class));
					break;
				case DeleteNode:
					updates.add(mapper.treeToValue(mapUpdateJson, DeleteNodeUpdate.class));
					break;
				case MoveNode:
					updates.add(mapper.treeToValue(mapUpdateJson, MoveNodeUpdate.class));
					break;
				case ChangeEdgeAttribute:
					updates.add(mapper.treeToValue(mapUpdateJson, ChangeEdgeAttributeUpdate.class));
					break;

				}

			}
		} catch (Exception e) {
			return Futures.failed(e);
		}
		return Futures.successful(new GetUpdatesResponse(currentRevision, updates));

	}

	@Override
	public Future<String> createNode(final User user, final MapIdentifier mapIdentifier, final String parentNodeId) {

		final WebResource resource = preparedResource(user).path("project/" + mapIdentifier.getProjectId() + "/map/" + mapIdentifier.getMapId() + "/node/create");
		final MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("parentNodeId", parentNodeId);

		final ClientResponse response = resource.post(ClientResponse.class, formData);
		try {
			final AddNodeUpdate update = new ObjectMapper().readValue(response.getEntity(String.class), AddNodeUpdate.class);
			return Futures.successful(update.getNewNodeId());
		} catch (Exception e) {
			e.printStackTrace();
			return Futures.failed(e);
		}
	}

	@Override
	public Future<Boolean> moveNodeTo(final User user, final MapIdentifier mapIdentifier, final String newParentId, final String nodeToMoveId, final int newIndex) {
		final WebResource resource = preparedResource(user).path("project/" + mapIdentifier.getProjectId() + "/map/" + mapIdentifier.getMapId() + "/node/move");
		final MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("newParentNodeId", newParentId);
		formData.add("nodetoMoveId", nodeToMoveId);
		formData.add("newIndex", newIndex + "");

		final ClientResponse response = resource.post(ClientResponse.class, formData);
		LogUtils.info("Status: " + response.getStatus());
		return Futures.successful(response.getStatus() == 200);

	}

	@Override
	public Future<Boolean> removeNode(final User user, final MapIdentifier mapIdentifier, final String nodeId) {

		final WebResource resource = preparedResource(user).path("project/" + mapIdentifier.getProjectId() + "/map/" + mapIdentifier.getMapId() + "/node/delete");
		final MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("nodeId", nodeId);

		ClientResponse response = resource.delete(ClientResponse.class, formData);

		LogUtils.info("Status: " + response.getStatus());
		return Futures.successful(response.getStatus() == 200);

	}

	@Override
	public Future<Boolean> changeNode(final User user, final MapIdentifier mapIdentifier, final String nodeId, final String attribute, final Object value) {
		try {
			final WebResource resource = preparedResource(user).path("project/" + mapIdentifier.getProjectId() + "/map/" + mapIdentifier.getMapId() + "/node/change");
			final MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
			formData.add("nodeId", nodeId);
			formData.add(attribute, value == null ? null : value.toString());

			LogUtils.info("locking node");
			// boolean isLocked =
			boolean isLocked = Await.result(lockNode(user, mapIdentifier, nodeId), Duration.create("5 seconds"));
			if (!isLocked)
				return Futures.successful(false);
			LogUtils.info("changing");
			ClientResponse response = resource.post(ClientResponse.class, formData);
			LogUtils.info("releasing node");
			releaseNode(user, mapIdentifier, nodeId);

			LogUtils.info("Status: " + response.getStatus());
			return Futures.successful(response.getStatus() == 200);

		} catch (Exception e) {
			e.printStackTrace();
			return Futures.failed(e);
		}
	}

	@Override
	public Future<Boolean> changeEdge(final User user, final MapIdentifier mapIdentifier, String nodeId, String attribute, Object value) {
		try {
			final WebResource resource = preparedResource(user).path("project/" + mapIdentifier.getProjectId() + "/map/" + mapIdentifier.getMapId() + "/node/changeEdge");
			final MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
			formData.add("nodeId", nodeId);
			formData.add(attribute, value.toString());

			LogUtils.info("changing");
			ClientResponse response = resource.post(ClientResponse.class, formData);

			LogUtils.info("Status: " + response.getStatus());
			return Futures.successful(response.getStatus() == 200);

		} catch (Exception e) {
			e.printStackTrace();
			return Futures.failed(e);
		}
	}

	private Future<Boolean> lockNode(final User user, final MapIdentifier mapIdentifier, final String nodeId) {
		final WebResource resource = preparedResource(user).path("project/" + mapIdentifier.getProjectId() + "/map/" + mapIdentifier.getMapId() + "/node/requestLock");
		final MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("nodeId", nodeId);

		ClientResponse response = resource.post(ClientResponse.class, formData);
		LogUtils.info("Status: " + response.getStatus());
		return Futures.successful(response.getStatus() == 200);
	}

	private Future<Boolean> releaseNode(final User user, final MapIdentifier mapIdentifier, final String nodeId) {
		final WebResource resource = preparedResource(user).path("project/" + mapIdentifier.getProjectId() + "/map/" + mapIdentifier.getMapId() + "/node/releaseLock");
		final MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("nodeId", nodeId);

		ClientResponse response = resource.post(ClientResponse.class, formData);

		LogUtils.info("Status: " + response.getStatus());
		return Futures.successful(response.getStatus() == 200);
	}

	private WebResource preparedResource(final User user) {
		return restClient.resource(serviceUrl).queryParam("username", user.getUsername()).queryParam("accessToken", user.getAccessToken());
	}
}