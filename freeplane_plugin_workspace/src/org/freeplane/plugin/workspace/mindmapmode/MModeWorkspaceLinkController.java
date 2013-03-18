package org.freeplane.plugin.workspace.mindmapmode;

import java.io.File;
import java.net.URI;

import org.freeplane.core.resources.ResourceController;
import org.freeplane.core.util.LogUtils;
import org.freeplane.features.link.mindmapmode.MLinkController;
import org.freeplane.plugin.workspace.URIUtils;
import org.freeplane.plugin.workspace.WorkspaceController;
//WORKSPACE - todo register as LinkController but beware of addAction problems
public class MModeWorkspaceLinkController extends MLinkController {
	
	public final static int LINK_RELATIVE_TO_PROJECT = 2;
	private final static String LINK_RELATIVE_TO_PROJECT_PROPERTY = "relative_to_workspace";


	
	private static MModeWorkspaceLinkController self;

	protected void init() {
		
	}
	
	public static MModeWorkspaceLinkController getController() {
//		final ModeController modeController = Controller.getCurrentModeController();
//		return (MModeWorkspaceLinkController) getController(modeController);
		if(self == null) {
			self = new MModeWorkspaceLinkController();
		}
		return self;
	}
	
	public int linkType() {
		String linkTypeProperty = ResourceController.getResourceController().getProperty("links");
		if (linkTypeProperty.equals(LINK_RELATIVE_TO_PROJECT_PROPERTY)) {
			return LINK_RELATIVE_TO_PROJECT;
		}
		return super.linkType();
	}
	
	public URI createRelativeURI(final File map, final File input, final int linkType) {
		if (linkType == LINK_ABSOLUTE) {
			return null;
		}
		try {
			if (linkType == LINK_RELATIVE_TO_PROJECT) {
				return WorkspaceController.getCurrentProject().getRelativeURI(input.getAbsoluteFile().toURI());
			}
			else {
				return super.createRelativeURI(map, input, linkType);
			}
		}
		catch (Exception e) {
			LogUtils.warn(e);
		}
		return null;
	}
	
	/**
	 * similar to the File(File base, File ext) contructor. The extend path (child) will be appended to the base path.
	 * @param base
	 * @param child
	 * @return
	 */
	public static URI extendPath(URI base, String child) {
		return new File(URIUtils.getAbsoluteFile(base), child).toURI();
	}
}