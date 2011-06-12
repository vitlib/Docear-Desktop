/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitry Polivaev
 *
 *  This file is modified by Dimitry Polivaev in 2008.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.features.styles.mindmapmode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.freeplane.core.controller.Controller;
import org.freeplane.core.controller.IMapSelection;
import org.freeplane.core.controller.INodeSelectionListener;
import org.freeplane.core.frame.IMapViewChangeListener;
import org.freeplane.core.frame.IMapViewManager;
import org.freeplane.core.resources.ResourceController;
import org.freeplane.core.resources.components.BooleanProperty;
import org.freeplane.core.resources.components.ColorProperty;
import org.freeplane.core.resources.components.ComboProperty;
import org.freeplane.core.resources.components.EditableComboProperty;
import org.freeplane.core.resources.components.FontProperty;
import org.freeplane.core.resources.components.IPropertyControl;
import org.freeplane.core.resources.components.NextColumnProperty;
import org.freeplane.core.resources.components.NextLineProperty;
import org.freeplane.core.resources.components.NumberProperty;
import org.freeplane.core.resources.components.SeparatorProperty;
import org.freeplane.core.util.ColorUtils;
import org.freeplane.core.util.TextUtils;
import org.freeplane.features.cloud.CloudController;
import org.freeplane.features.cloud.CloudModel;
import org.freeplane.features.cloud.mindmapmode.MCloudController;
import org.freeplane.features.edge.EdgeController;
import org.freeplane.features.edge.EdgeModel;
import org.freeplane.features.edge.EdgeStyle;
import org.freeplane.features.edge.mindmapmode.AutomaticEdgeColorHook;
import org.freeplane.features.edge.mindmapmode.MEdgeController;
import org.freeplane.features.format.FormatController;
import org.freeplane.features.format.IFormattedObject;
import org.freeplane.features.map.INodeChangeListener;
import org.freeplane.features.map.MapController;
import org.freeplane.features.map.ModeController;
import org.freeplane.features.map.NodeChangeEvent;
import org.freeplane.features.map.NodeModel;
import org.freeplane.features.nodestyle.NodeStyleController;
import org.freeplane.features.nodestyle.NodeStyleModel;
import org.freeplane.features.nodestyle.mindmapmode.MNodeStyleController;
import org.freeplane.features.styles.IStyle;
import org.freeplane.features.styles.LogicalStyleController;
import org.freeplane.features.styles.LogicalStyleModel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

public class StyleEditorPanel extends JPanel {
	private class BgColorChangeListener extends ChangeListener {
		public BgColorChangeListener(final BooleanProperty mSet, final IPropertyControl mProperty) {
			super(mSet, mProperty);
		}

		@Override
		void applyValue(final boolean enabled, final NodeModel node, final PropertyChangeEvent evt) {
			final MNodeStyleController styleController = (MNodeStyleController) Controller
					.getCurrentModeController().getExtension(
							NodeStyleController.class);
			styleController.setBackgroundColor(node, enabled ? mNodeBackgroundColor.getColorValue() : null);
		}
	}

	private class NodeShapeChangeListener extends ChangeListener {
		public NodeShapeChangeListener(final BooleanProperty mSet, final IPropertyControl mProperty) {
			super(mSet, mProperty);
		}

		@Override
		void applyValue(final boolean enabled, final NodeModel node, final PropertyChangeEvent evt) {
			final MNodeStyleController styleController = (MNodeStyleController) Controller
			.getCurrentModeController().getExtension(
					NodeStyleController.class);
			styleController.setShape(node, enabled ? mNodeShape.getValue() : null);
		}
	}

	private class ColorChangeListener extends ChangeListener {
		public ColorChangeListener(final BooleanProperty mSet, final IPropertyControl mProperty) {
			super(mSet, mProperty);
		}

		@Override
		void applyValue(final boolean enabled, final NodeModel node, final PropertyChangeEvent evt) {
			final MNodeStyleController styleController = (MNodeStyleController) Controller
			.getCurrentModeController().getExtension(
					NodeStyleController.class);
			styleController.setColor(node, enabled ? mNodeColor.getColorValue() : null);
		}
	}

	private class FontBoldChangeListener extends ChangeListener {
		public FontBoldChangeListener(final BooleanProperty mSet, final IPropertyControl mProperty) {
			super(mSet, mProperty);
		}

		@Override
		void applyValue(final boolean enabled, final NodeModel node, final PropertyChangeEvent evt) {
			final MNodeStyleController styleController = (MNodeStyleController) Controller
			.getCurrentModeController().getExtension(
					NodeStyleController.class);
			styleController.setBold(node, enabled ? mNodeFontBold.getBooleanValue() : null);
		}
	}

	private class FontItalicChangeListener extends ChangeListener {
		public FontItalicChangeListener(final BooleanProperty mSet, final IPropertyControl mProperty) {
			super(mSet, mProperty);
		}

		@Override
		void applyValue(final boolean enabled, final NodeModel node, final PropertyChangeEvent evt) {
			final MNodeStyleController styleController = (MNodeStyleController) Controller
			.getCurrentModeController().getExtension(
					NodeStyleController.class);
			styleController.setItalic(node, enabled ? mNodeFontItalic.getBooleanValue() : null);
		}
	}

	private class FontSizeChangeListener extends ChangeListener {
		public FontSizeChangeListener(final BooleanProperty mSet, final IPropertyControl mProperty) {
			super(mSet, mProperty);
		}

		@Override
		void applyValue(final boolean enabled, final NodeModel node, final PropertyChangeEvent evt) {
			final MNodeStyleController styleController = (MNodeStyleController) Controller
			.getCurrentModeController().getExtension(
					NodeStyleController.class);
			styleController.setFontSize(node, enabled ? Integer.valueOf(mNodeFontSize.getValue()) : null);
		}
	}

	private class FontNameChangeListener extends ChangeListener {
		public FontNameChangeListener(final BooleanProperty mSet, final IPropertyControl mProperty) {
			super(mSet, mProperty);
		}

		@Override
		void applyValue(final boolean enabled, final NodeModel node, final PropertyChangeEvent evt) {
			final MNodeStyleController styleController = (MNodeStyleController) Controller
			.getCurrentModeController().getExtension(
					NodeStyleController.class);
			styleController.setFontFamily(node, enabled ? mNodeFontName.getValue() : null);
		}
	}

	private class EdgeColorChangeListener extends ChangeListener {
		public EdgeColorChangeListener(final BooleanProperty mSet, final IPropertyControl mProperty) {
			super(mSet, mProperty);
		}

		@Override
		void applyValue(final boolean enabled, final NodeModel node, final PropertyChangeEvent evt) {
			final MEdgeController edgeController = (MEdgeController) MEdgeController.getController();
			edgeController.setColor(node, enabled ? mEdgeColor.getColorValue() : null);
		}
	}

	private class EdgeStyleChangeListener extends ChangeListener {
		public EdgeStyleChangeListener(final BooleanProperty mSet, final IPropertyControl mProperty) {
			super(mSet, mProperty);
		}

		@Override
		void applyValue(final boolean enabled, final NodeModel node,
				final PropertyChangeEvent evt) {
			final MEdgeController styleController = (MEdgeController) Controller
					.getCurrentModeController().getExtension(
							EdgeController.class);
			styleController.setStyle(node, enabled ? EdgeStyle.getStyle(mEdgeStyle.getValue()) : null);
		}
	}

	private class EdgeWidthChangeListener extends ChangeListener {
		public EdgeWidthChangeListener(final BooleanProperty mSet, final IPropertyControl mProperty) {
			super(mSet, mProperty);
		}

		@Override
		void applyValue(final boolean enabled, final NodeModel node, final PropertyChangeEvent evt) {
			final MEdgeController styleController = (MEdgeController) Controller
			.getCurrentModeController().getExtension(
					EdgeController.class);
			styleController.setWidth(node, enabled ? Integer.parseInt(mEdgeWidth.getValue()): EdgeModel.DEFAULT_WIDTH);
		}
	}

	private class CloudColorChangeListener extends ChangeListener {
		public CloudColorChangeListener(final BooleanProperty mSet, final IPropertyControl mProperty) {
			super(mSet, mProperty);
		}

		@Override
		void applyValue(final boolean enabled, final NodeModel node,
				final PropertyChangeEvent evt) {
			final MCloudController styleController = (MCloudController) Controller
					.getCurrentModeController().getExtension(
							CloudController.class);
			if (enabled) {
				styleController.setColor(node, mCloudColor.getColorValue());
			}
			else {
				styleController.setCloud(node, false);
			}
		}
	}

	private class CloudShapeChangeListener extends ChangeListener {
		public CloudShapeChangeListener(final BooleanProperty mSet, final IPropertyControl mProperty) {
			super(mSet, mProperty);
		}

		@Override
		void applyValue(final boolean enabled, final NodeModel node,
				final PropertyChangeEvent evt) {
			final MCloudController styleController = (MCloudController) Controller
					.getCurrentModeController().getExtension(
						CloudController.class);
			if (enabled) {
				styleController.setShape(node, CloudModel.Shape.valueOf(mCloudShape.getValue()));
			}
			else {
				styleController.setCloud(node, false);
			}
		}
	}

	private class NodeNumberingChangeListener extends ChangeListener {
		public NodeNumberingChangeListener(final BooleanProperty mSet, final IPropertyControl mProperty) {
			super(mSet, mProperty);
		}

		@Override
		void applyValue(final boolean enabled, final NodeModel node, final PropertyChangeEvent evt) {
			final MNodeStyleController styleController = (MNodeStyleController) Controller.getCurrentModeController()
			    .getExtension(NodeStyleController.class);
			styleController.setNodeNumbering(node, enabled ? mNodeNumbering.getBooleanValue() : null);
		}
	}
	
	private class NodeFormatChangeListener extends ChangeListener {
		public NodeFormatChangeListener(final BooleanProperty mSet, final IPropertyControl mProperty) {
			super(mSet, mProperty);
		}

		@Override
		void applyValue(final boolean enabled, final NodeModel node, final PropertyChangeEvent evt) {
			final MNodeStyleController styleController = (MNodeStyleController) Controller.getCurrentModeController()
			    .getExtension(NodeStyleController.class);
			styleController.setNodeFormat(node, enabled ? mNodeFormat.getValue() : null);
		}
	}

	private class StyleChangeListener implements PropertyChangeListener{

		public StyleChangeListener() {
        }

		public void propertyChange(PropertyChangeEvent evt) {
			if(internalChange){
				return;
			}
			BooleanProperty isSet = (BooleanProperty) evt.getSource();
			final MLogicalStyleController styleController = (MLogicalStyleController) LogicalStyleController.getController();
			if(isSet.getBooleanValue()){
				styleController.setStyle((IStyle) uiFactory.getStyles().getSelectedItem());
			}
			else{
				styleController.setStyle(null);
			}
        }
		
	}
	private abstract class ChangeListener implements PropertyChangeListener {
		final private IPropertyControl mProperty;
		final private BooleanProperty mSet;

		public ChangeListener(final BooleanProperty mSet, final IPropertyControl mProperty) {
			super();
			this.mSet = mSet;
			this.mProperty = mProperty;
		}

		abstract void applyValue(final boolean enabled, NodeModel node, PropertyChangeEvent evt);

		public void propertyChange(final PropertyChangeEvent evt) {
			if (internalChange) {
				return;
			}
			final boolean enabled;
			if (evt.getSource().equals(mSet)) {
				enabled = mSet.getBooleanValue();
			}
			else {
				assert evt.getSource().equals(mProperty);
				enabled = true;
				mSet.setValue(true);
			}
			final List<NodeModel> nodes = Controller.getCurrentController().getSelection().getSelection();
			if (enabled )
				internalChange = true;
			for (final NodeModel node : nodes) {
				applyValue(enabled, node, evt);
			}
			internalChange = false;
		}
	}

	private static final String CLOUD_COLOR = "cloudcolor";
	private static final String EDGE_COLOR = "edgecolor";
	private static final String EDGE_STYLE = "edgestyle";
	private static final String CLOUD_SHAPE = "cloudshape";
	private static final String[] EDGE_STYLES = StyleEditorPanel.initializeEdgeStyles();
	private static final String[] CLOUD_SHAPES = StyleEditorPanel.initializeCloudShapes();
	private static final String EDGE_WIDTH = "edgewidth";
//	private static final String ICON = "icon";
	private static final String NODE_BACKGROUND_COLOR = "nodebackgroundcolor";
	private static final String NODE_COLOR = "nodecolor";
	private static final String NODE_FONT_BOLD = "nodefontbold";
	private static final String NODE_FONT_ITALIC = "nodefontitalic";
	private static final String NODE_FONT_NAME = "nodefontname";
	private static final String NODE_FONT_SIZE = "nodefontsize";
	private static final String NODE_NUMBERING = "nodenumbering";
	private static final String NODE_SHAPE = "nodeshape";
	private static final String NODE_TEXT_COLOR = "standardnodetextcolor";
	private static final String NODE_FORMAT = "nodeformat";
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private static final String SET_RESOURCE = "set_property_text";
	
	private static String[] initializeEdgeStyles() {
		final EdgeStyle[] enumConstants = EdgeStyle.class.getEnumConstants();
		final String[] strings = new String[enumConstants.length];
		for (int i = 0; i < enumConstants.length; i++) {
			strings[i] = enumConstants[i].toString();
		}
		return strings;
	}

	private static String[] initializeCloudShapes() {
		final CloudModel.Shape[] enumConstants = CloudModel.Shape.class.getEnumConstants();
		final String[] strings = new String[enumConstants.length];
		for (int i = 0; i < enumConstants.length; i++) {
			strings[i] = enumConstants[i].toString();
		}
		return strings;
	}

	private boolean internalChange;
	private ColorProperty mCloudColor;
	private ComboProperty mCloudShape;
	private List<IPropertyControl> mControls;
	private ColorProperty mEdgeColor;
	private ComboProperty mEdgeStyle;
	private NumberProperty mEdgeWidth;
// 	private final ModeController mMindMapController;
	private ColorProperty mNodeBackgroundColor;
	private ColorProperty mNodeColor;
	private BooleanProperty mNodeFontBold;
	private BooleanProperty mNodeFontItalic;
	private FontProperty mNodeFontName;
	private ComboProperty mNodeFontSize;
	private BooleanProperty mNodeNumbering;
	private ComboProperty mNodeShape;
	private EditableComboProperty mNodeFormat;
	private BooleanProperty mSetCloud;
	private BooleanProperty mSetEdgeColor;
	private BooleanProperty mSetEdgeStyle;
	private BooleanProperty mSetEdgeWidth;
	private BooleanProperty mSetNodeBackgroundColor;
	private BooleanProperty mSetNodeColor;
	private BooleanProperty mSetNodeFontBold;
	private BooleanProperty mSetNodeFontItalic;
	private BooleanProperty mSetNodeFontName;
	private BooleanProperty mSetNodeFontSize;
	private BooleanProperty mSetNodeNumbering;
	private BooleanProperty mSetNodeShape;
	private BooleanProperty mSetNodeFormat;
	private BooleanProperty mSetStyle;
	final private String[] sizes = new String[] { "2", "4", "6", "8", "10", "12", "14", "16", "18", "20", "22", "24",
	        "30", "36", "48", "72" };
	private final boolean addStyleBox;
	private final MUIFactory uiFactory;

	/**
	 * @throws HeadlessException
	 */
	public StyleEditorPanel(final MUIFactory uiFactory,
	                        final boolean addStyleBox) throws HeadlessException {
		super();
		this.addStyleBox = addStyleBox;
		this.uiFactory = uiFactory;
	}

	private void addBgColorControl(final List<IPropertyControl> controls) {
		mSetNodeBackgroundColor = new BooleanProperty(StyleEditorPanel.SET_RESOURCE);
		controls.add(mSetNodeBackgroundColor);
		mNodeBackgroundColor = new ColorProperty(StyleEditorPanel.NODE_BACKGROUND_COLOR, ResourceController
		    .getResourceController().getDefaultProperty(NODE_BACKGROUND_COLOR));
		controls.add(mNodeBackgroundColor);
		final BgColorChangeListener listener = new BgColorChangeListener(mSetNodeBackgroundColor, mNodeBackgroundColor);
		mSetNodeBackgroundColor.addPropertyChangeListener(listener);
		mNodeBackgroundColor.addPropertyChangeListener(listener);
		mNodeBackgroundColor.fireOnMouseClick();
	}

	private void addFormatControl(final List<IPropertyControl> controls) {
		mSetNodeFormat = new BooleanProperty(StyleEditorPanel.SET_RESOURCE);
		controls.add(mSetNodeFormat);
		mNodeFormat = new EditableComboProperty(StyleEditorPanel.NODE_FORMAT,
		    new FormatController().getAllPatterns());
		controls.add(mNodeFormat);
		final NodeFormatChangeListener listener = new NodeFormatChangeListener(mSetNodeFormat,
		    mNodeFormat);
		mSetNodeFormat.addPropertyChangeListener(listener);
		mNodeFormat.addPropertyChangeListener(listener);
		mNodeFormat.fireOnMouseClick();
	}
	
	private void addNodeNumberingControl(final List<IPropertyControl> controls) {
		mSetNodeNumbering = new BooleanProperty(StyleEditorPanel.SET_RESOURCE);
		controls.add(mSetNodeNumbering);
		mNodeNumbering = new BooleanProperty(StyleEditorPanel.NODE_NUMBERING);
		controls.add(mNodeNumbering);
		final NodeNumberingChangeListener listener = new NodeNumberingChangeListener(mSetNodeNumbering, mNodeNumbering);
		mSetNodeNumbering.addPropertyChangeListener(listener);
		mNodeNumbering.addPropertyChangeListener(listener);
		mNodeNumbering.fireOnMouseClick();
	}

	private void addCloudColorControl(final List<IPropertyControl> controls) {
		mSetCloud = new BooleanProperty(StyleEditorPanel.SET_RESOURCE);
		controls.add(mSetCloud);
		mCloudColor = new ColorProperty(StyleEditorPanel.CLOUD_COLOR, ResourceController.getResourceController()
		    .getDefaultProperty(CloudController.RESOURCES_CLOUD_COLOR));
		controls.add(mCloudColor);
		final CloudColorChangeListener listener = new CloudColorChangeListener(mSetCloud, mCloudColor);
		mSetCloud.addPropertyChangeListener(listener);
		mCloudColor.addPropertyChangeListener(listener);
		mCloudColor.fireOnMouseClick();
	}

	private void addColorControl(final List<IPropertyControl> controls) {
		mSetNodeColor = new BooleanProperty(StyleEditorPanel.SET_RESOURCE);
		controls.add(mSetNodeColor);
		mNodeColor = new ColorProperty(StyleEditorPanel.NODE_COLOR, ResourceController.getResourceController()
		    .getDefaultProperty(NODE_TEXT_COLOR));
		controls.add(mNodeColor);
		final ColorChangeListener listener = new ColorChangeListener(mSetNodeColor, mNodeColor);
		mSetNodeColor.addPropertyChangeListener(listener);
		mNodeColor.addPropertyChangeListener(listener);
		mNodeColor.fireOnMouseClick();
	}

	private void addEdgeColorControl(final List<IPropertyControl> controls) {
		mSetEdgeColor = new BooleanProperty(StyleEditorPanel.SET_RESOURCE);
		controls.add(mSetEdgeColor);
		mEdgeColor = new ColorProperty(StyleEditorPanel.EDGE_COLOR, ColorUtils.colorToString(EdgeController.STANDARD_EDGE_COLOR));
		controls.add(mEdgeColor);
		final EdgeColorChangeListener listener = new EdgeColorChangeListener(mSetEdgeColor, mEdgeColor);
		mSetEdgeColor.addPropertyChangeListener(listener);
		mEdgeColor.addPropertyChangeListener(listener);
		mEdgeColor.fireOnMouseClick();
	}

	private void addEdgeStyleControl(final List<IPropertyControl> controls) {
		mSetEdgeStyle = new BooleanProperty(StyleEditorPanel.SET_RESOURCE);
		controls.add(mSetEdgeStyle);
		mEdgeStyle = new ComboProperty(StyleEditorPanel.EDGE_STYLE, EDGE_STYLES);
		controls.add(mEdgeStyle);
		final EdgeStyleChangeListener listener = new EdgeStyleChangeListener(mSetEdgeStyle, mEdgeStyle);
		mSetEdgeStyle.addPropertyChangeListener(listener);
		mEdgeStyle.addPropertyChangeListener(listener);
		mEdgeStyle.fireOnMouseClick();
	}

	private void addCloudShapeControl(final List<IPropertyControl> controls) {
		mCloudShape = new ComboProperty(StyleEditorPanel.CLOUD_SHAPE, CLOUD_SHAPES);
		controls.add(mCloudShape);
		final CloudShapeChangeListener listener = new CloudShapeChangeListener(mSetCloud, mCloudShape);
		mSetCloud.addPropertyChangeListener(listener);
		mCloudShape.addPropertyChangeListener(listener);
		mCloudShape.fireOnMouseClick();
	}

	private void addEdgeWidthControl(final List<IPropertyControl> controls) {
		mSetEdgeWidth = new BooleanProperty(StyleEditorPanel.SET_RESOURCE);
		controls.add(mSetEdgeWidth);
		mEdgeWidth = new NumberProperty(StyleEditorPanel.EDGE_WIDTH, 0, 100, 1);
		controls.add(mEdgeWidth);
		final EdgeWidthChangeListener listener = new EdgeWidthChangeListener(mSetEdgeWidth, mEdgeWidth);
		mSetEdgeWidth.addPropertyChangeListener(listener);
		mEdgeWidth.addPropertyChangeListener(listener);
		mEdgeWidth.fireOnMouseClick();
	}

	private void addFontBoldControl(final List<IPropertyControl> controls) {
		mSetNodeFontBold = new BooleanProperty(StyleEditorPanel.SET_RESOURCE);
		controls.add(mSetNodeFontBold);
		mNodeFontBold = new BooleanProperty(StyleEditorPanel.NODE_FONT_BOLD);
		controls.add(mNodeFontBold);
		final FontBoldChangeListener listener = new FontBoldChangeListener(mSetNodeFontBold, mNodeFontBold);
		mSetNodeFontBold.addPropertyChangeListener(listener);
		mNodeFontBold.addPropertyChangeListener(listener);
		mNodeFontBold.fireOnMouseClick();
	}

	private void addFontItalicControl(final List<IPropertyControl> controls) {
		mSetNodeFontItalic = new BooleanProperty(StyleEditorPanel.SET_RESOURCE);
		controls.add(mSetNodeFontItalic);
		mNodeFontItalic = new BooleanProperty(StyleEditorPanel.NODE_FONT_ITALIC);
		controls.add(mNodeFontItalic);
		final FontItalicChangeListener listener = new FontItalicChangeListener(mSetNodeFontItalic, mNodeFontItalic);
		mSetNodeFontItalic.addPropertyChangeListener(listener);
		mNodeFontItalic.addPropertyChangeListener(listener);
		mNodeFontItalic.fireOnMouseClick();
	}

	private void addFontNameControl(final List<IPropertyControl> controls) {
		mSetNodeFontName = new BooleanProperty(StyleEditorPanel.SET_RESOURCE);
		controls.add(mSetNodeFontName);
		mNodeFontName = new FontProperty(StyleEditorPanel.NODE_FONT_NAME);
		controls.add(mNodeFontName);
		final FontNameChangeListener listener = new FontNameChangeListener(mSetNodeFontName, mNodeFontName);
		mSetNodeFontName.addPropertyChangeListener(listener);
		mNodeFontName.addPropertyChangeListener(listener);
		mNodeFontName.fireOnMouseClick();
	}

	private void addFontSizeControl(final List<IPropertyControl> controls) {
		mSetNodeFontSize = new BooleanProperty(StyleEditorPanel.SET_RESOURCE);
		controls.add(mSetNodeFontSize);
		final List<String> sizesVector = new ArrayList<String>(Arrays.asList(sizes));
		mNodeFontSize = new ComboProperty(StyleEditorPanel.NODE_FONT_SIZE, sizesVector, sizesVector);
		controls.add(mNodeFontSize);
		final FontSizeChangeListener listener = new FontSizeChangeListener(mSetNodeFontSize, mNodeFontSize);
		mSetNodeFontSize.addPropertyChangeListener(listener);
		mNodeFontSize.addPropertyChangeListener(listener);
		mNodeFontSize.fireOnMouseClick();
	}

	private void addNodeShapeControl(final List<IPropertyControl> controls) {
		mSetNodeShape = new BooleanProperty(StyleEditorPanel.SET_RESOURCE);
		controls.add(mSetNodeShape);
		mNodeShape = new ComboProperty(StyleEditorPanel.NODE_SHAPE, new String[] { "fork", "bubble", "as_parent",
		        "combined" });
		controls.add(mNodeShape);
		final NodeShapeChangeListener listener = new NodeShapeChangeListener(mSetNodeShape, mNodeShape);
		mSetNodeShape.addPropertyChangeListener(listener);
		mNodeShape.addPropertyChangeListener(listener);
		mNodeShape.fireOnMouseClick();
	}

	private List<IPropertyControl> getControls() {
		final List<IPropertyControl> controls = new ArrayList<IPropertyControl>();
		controls.add(new SeparatorProperty("OptionPanel.separator.NodeColors"));
		addColorControl(controls);
		addBgColorControl(controls);
		controls.add(new SeparatorProperty("OptionPanel.separator.NodeText"));
		addFormatControl(controls);
		addNodeNumberingControl(controls);
		controls.add(new SeparatorProperty("OptionPanel.separator.NodeShape"));
		addNodeShapeControl(controls);
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("OptionPanel.separator.NodeFont"));
		addFontNameControl(controls);
		addFontSizeControl(controls);
		addFontBoldControl(controls);
		addFontItalicControl(controls);
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("OptionPanel.separator.EdgeControls"));
		addEdgeWidthControl(controls);
		addEdgeStyleControl(controls);
		addEdgeColorControl(controls);
		controls.add(new NextLineProperty());
		controls.add(new SeparatorProperty("OptionPanel.separator.CloudControls"));
		addCloudColorControl(controls);
		controls.add(new NextLineProperty());
		controls.add(new NextColumnProperty(2));
		addCloudShapeControl(controls);
		return controls;
	}

	/**
	 * Creates all controls and adds them to the frame.
	 * @param modeController 
	 */
	public void init() {
		final String form = "right:max(20dlu;p), 2dlu, p, 1dlu,right:max(20dlu;p), 4dlu, 80dlu, 7dlu";
		final FormLayout rightLayout = new FormLayout(form, "");
		final DefaultFormBuilder rightBuilder = new DefaultFormBuilder(rightLayout);
		rightBuilder.setBorder(Borders.DLU2_BORDER);
		if (addStyleBox) {
			rightBuilder.appendSeparator(TextUtils.getText("OptionPanel.separator.NodeStyle"));
			addAutomaticLayout(rightBuilder);
			addStyleBox(rightBuilder);
		}
		mControls = getControls();
		for (final IPropertyControl control : mControls) {
			control.layout(rightBuilder);
		}
		add(rightBuilder.getPanel(), BorderLayout.CENTER);
		addListeners();
		setFont(this, 10);
	}

	private void addStyleBox(final DefaultFormBuilder rightBuilder) {
	    mStyleBox = uiFactory.createStyleBox();
	    rightBuilder.nextLine();
	    mSetStyle = new BooleanProperty(StyleEditorPanel.SET_RESOURCE);
		final StyleChangeListener listener = new StyleChangeListener();
		mSetStyle.addPropertyChangeListener(listener);
		mSetStyle.layout(rightBuilder);
	    rightBuilder.append(new JLabel(TextUtils.getText("style")));
	    rightBuilder.append(mStyleBox);
	    rightBuilder.nextLine();
    }

	private JCheckBox mAutomaticLayoutCheckBox;
	private JCheckBox mAutomaticEdgeColorCheckBox;
	private Container mStyleBox;
	private void addAutomaticLayout(final DefaultFormBuilder rightBuilder) {
		{
		if(mAutomaticLayoutCheckBox == null){
			 mAutomaticLayoutCheckBox = new JCheckBox();
			 mAutomaticLayoutCheckBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final ModeController modeController = Controller.getCurrentModeController();
					AutomaticLayout al = (AutomaticLayout) modeController.getExtension(AutomaticLayout.class);
					al.undoableToggleHook(Controller.getCurrentController().getMap().getRootNode(), al);
				}
			});
		}
	    final String label = TextUtils.removeMnemonic(TextUtils.getText("AutomaticLayoutAction.text"));
	    rightBuilder.append(new JLabel(label), 5);
	    rightBuilder.append(mAutomaticLayoutCheckBox);
		}
		{
			if(mAutomaticEdgeColorCheckBox == null){
				mAutomaticEdgeColorCheckBox = new JCheckBox();
				mAutomaticEdgeColorCheckBox.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						final ModeController modeController = Controller.getCurrentModeController();
						AutomaticEdgeColorHook al = (AutomaticEdgeColorHook) modeController.getExtension(AutomaticEdgeColorHook.class);
						al.undoableToggleHook(Controller.getCurrentController().getMap().getRootNode());
					}
				});
			}
			final String label = TextUtils.removeMnemonic(TextUtils.getText("AutomaticEdgeColorHookAction.text"));
			rightBuilder.append(new JLabel(label), 5);
			rightBuilder.append(mAutomaticEdgeColorCheckBox);
		}
	}

	private void setFont(Container c, float size) {
		c.setFont(c.getFont().deriveFont(size));
		for(int i = 0; i < c.getComponentCount(); i++){
			setFont((Container) c.getComponent(i), size);
		}
    }

	public void setStyle( final NodeModel node) {
		if (internalChange) {
			return;
		}
		internalChange = true;
		try {
			if(addStyleBox){
				final boolean isStyleSet = LogicalStyleModel.getStyle(node) != null;
				mSetStyle.setValue(isStyleSet);
			}
			final NodeStyleController styleController = NodeStyleController.getController();
			{
				final Color nodeColor = NodeStyleModel.getColor(node);
				final Color viewNodeColor = styleController.getColor(node);
				mSetNodeColor.setValue(nodeColor != null);
				mNodeColor.setColorValue(viewNodeColor);
			}
			{
				final Color color = NodeStyleModel.getBackgroundColor(node);
				final Color viewColor = styleController.getBackgroundColor(node);
				mSetNodeBackgroundColor.setValue(color != null);
				mNodeBackgroundColor.setColorValue(viewColor != null ? viewColor : Controller.getCurrentController()
				    .getMapViewManager().getBackgroundColor(node));
			}
			{
				final String shape = NodeStyleModel.getShape(node);
				final String viewShape = styleController.getShapeEx(node);
				mSetNodeShape.setValue(shape != null);
				mNodeShape.setValue(viewShape);
			}
			final EdgeController edgeController = EdgeController.getController();
			final EdgeModel edgeModel = EdgeModel.getModel(node);
			{
				final Color edgeColor = edgeModel != null ? edgeModel.getColor() : null;
				final Color viewColor = edgeController.getColor(node);
				mSetEdgeColor.setValue(edgeColor != null);
				mEdgeColor.setColorValue(viewColor);
			}
			{
				final EdgeStyle style = edgeModel != null ? edgeModel.getStyle() : null;
				final EdgeStyle viewStyle = edgeController.getStyle(node);
				mSetEdgeStyle.setValue(style != null);
				mEdgeStyle.setValue(viewStyle.toString());
			}
			{
				final int width = edgeModel != null ? edgeModel.getWidth() : EdgeModel.DEFAULT_WIDTH;
				final int viewWidth = edgeController.getWidth(node);
				mSetEdgeWidth.setValue(width != EdgeModel.DEFAULT_WIDTH);
				mEdgeWidth.setValue(Integer.toString(viewWidth));
			}
			{
				final CloudController cloudController = CloudController.getController();
				final CloudModel cloudModel = CloudModel.getModel(node);
				final Color viewCloudColor = cloudController.getColor(node);
				mSetCloud.setValue(cloudModel != null);
				mCloudColor.setColorValue(viewCloudColor);

				final CloudModel.Shape viewCloudShape = cloudController.getShape(node);
				mCloudShape.setValue(viewCloudShape != null ? viewCloudShape.toString() : CloudModel.Shape.ARC.toString());
			}
			{
				final String fontFamilyName = NodeStyleModel.getFontFamilyName(node);
				final String viewFontFamilyName = styleController.getFontFamilyName(node);
				mSetNodeFontName.setValue(fontFamilyName != null);
				mNodeFontName.setValue(viewFontFamilyName);
			}
			{
				final Integer fontSize = NodeStyleModel.getFontSize(node);
				final Integer viewfontSize = styleController.getFontSize(node);
				mSetNodeFontSize.setValue(fontSize != null);
				mNodeFontSize.setValue(viewfontSize.toString());
			}
			{
				final Boolean bold = NodeStyleModel.isBold(node);
				final Boolean viewbold = styleController.isBold(node);
				mSetNodeFontBold.setValue(bold != null);
				mNodeFontBold.setValue(viewbold);
			}
			{
				final Boolean italic = NodeStyleModel.isItalic(node);
				final Boolean viewitalic = styleController.isItalic(node);
				mSetNodeFontItalic.setValue(italic != null);
				mNodeFontItalic.setValue(viewitalic);
			}
			{
				final Boolean nodeNumbering = NodeStyleModel.getNodeNumbering(node);
				final Boolean viewNodeNumbering = styleController.getNodeNumbering(node);
				mSetNodeNumbering.setValue(nodeNumbering != null);
				mNodeNumbering.setValue(viewNodeNumbering);
			}
			{
				String nodeFormat = NodeStyleModel.getNodeFormat(node);
				mSetNodeFormat.setValue(nodeFormat != null);
//				LogUtils.warn("hi, getNodeFormat(" + node.getUserObject() + ": " + node.getUserObject().getClass());
				if (nodeFormat == null && node.getUserObject() instanceof IFormattedObject)
					nodeFormat = ((IFormattedObject)node.getUserObject()).getPattern();
				mNodeFormat.setValue(nodeFormat);
			}
			if(mAutomaticLayoutCheckBox != null){
				final ModeController modeController = Controller.getCurrentModeController();
				AutomaticLayout al = (AutomaticLayout) modeController.getExtension(AutomaticLayout.class);
				mAutomaticLayoutCheckBox.setSelected(al.isActive(node));
			}
			if(mAutomaticEdgeColorCheckBox != null){
				final ModeController modeController = Controller.getCurrentModeController();
				AutomaticEdgeColorHook al = (AutomaticEdgeColorHook) modeController.getExtension(AutomaticEdgeColorHook.class);
				mAutomaticEdgeColorCheckBox.setSelected(al.isActive(node));
			}
		}
		finally {
			internalChange = false;
		}
	}


	private void addListeners() {
		final Controller controller = Controller.getCurrentController();
		final ModeController modeController = Controller.getCurrentModeController();
		final MapController mapController = modeController.getMapController();
		mapController.addNodeSelectionListener(new INodeSelectionListener() {
			public void onSelect(final NodeModel node) {
				final IMapSelection selection = controller.getSelection();
				if (selection == null) {
					return;
				}
				if (selection.size() == 1) {
					setStyle(node);
				}
			}

			public void onDeselect(final NodeModel node) {
			}
		});
		mapController.addNodeChangeListener(new INodeChangeListener() {
			public void nodeChanged(final NodeChangeEvent event) {
				final IMapSelection selection = controller.getSelection();
				if (selection == null) {
					return;
				}
				final NodeModel node = event.getNode();
				if (selection.getSelected().equals(node)) {
					setStyle(node);
				}
			}
		});
		final IMapViewManager mapViewManager = controller.getMapViewManager();
		mapViewManager.addMapViewChangeListener(new IMapViewChangeListener() {
			public void beforeViewChange(final Component oldView, final Component newView) {
			}

			public void afterViewCreated(final Component mapView) {
			}

			public void afterViewClose(final Component oldView) {
			}

			public void afterViewChange(final Component oldView, final Component newView) {
				final Container panel = (Container) getComponent(0);
				for (int i = 0; i < panel.getComponentCount(); i++) {
					panel.getComponent(i).setEnabled(newView != null);
				}
			}
		});
	}
}