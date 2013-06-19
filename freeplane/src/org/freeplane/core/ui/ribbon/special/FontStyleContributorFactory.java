package org.freeplane.core.ui.ribbon.special;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.freeplane.core.io.IElementHandler;
import org.freeplane.core.resources.ResourceController;
import org.freeplane.core.ui.AFreeplaneAction;
import org.freeplane.core.ui.IndexedTree;
import org.freeplane.core.ui.ribbon.IRibbonContributor;
import org.freeplane.core.ui.ribbon.IRibbonContributorFactory;
import org.freeplane.core.ui.ribbon.RibbonActionContributorFactory;
import org.freeplane.core.ui.ribbon.RibbonBuilder.RibbonPath;
import org.freeplane.core.util.TextUtils;
import org.freeplane.features.mode.Controller;
import org.freeplane.features.styles.mindmapmode.MUIFactory;
import org.freeplane.n3.nanoxml.XMLElement;
import org.freeplane.view.swing.ui.UserInputListenerFactory;
import org.pushingpixels.flamingo.api.common.AbstractCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandButtonStrip;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JFlowRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonComponent;

public class FontStyleContributorFactory implements IRibbonContributorFactory {

	private final class FontSizeCreator implements IElementHandler {
		public Object createElement(final Object parent, final String tag, final XMLElement attributes) {
			if (attributes == null) {
				return null;
			}

			UserInputListenerFactory userInputListenerFactory = new UserInputListenerFactory(Controller.getCurrentModeController());
			
			final RibbonPath menuPath = new RibbonPath((RibbonPath) parent);
			menuPath.setName(attributes.getAttribute("name", null));
			IRibbonContributorFactory factory = userInputListenerFactory.getRibbonBuilder().getContributorFactory(tag);
			if (factory != null && !userInputListenerFactory.getRibbonBuilder().containsKey(menuPath.getKey())) {
				userInputListenerFactory.getRibbonBuilder().add(factory.getContributor(attributes.getAttributes()), menuPath.getParent(), IndexedTree.AS_CHILD);
			}
			return menuPath;
		}
	}

	public IRibbonContributor getContributor(final Properties attributes) {
		return new IRibbonContributor() {

			public String getKey() {
				return attributes.getProperty("name");
			}

			public void contribute(IndexedTree structure, IRibbonContributor parent) {
				if (parent == null) {
					return;
				}
				final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
				Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
				try {
					// RIBBONS expandlistener and icon
					JFlowRibbonBand fontBand = new JFlowRibbonBand("Font", null, null);
					fontBand.setExpandButtonKeyTip("FN");
					fontBand.setCollapsedStateKeyTip("ZF");

					MUIFactory uiFactory = new MUIFactory();
					
					final Container fontBox = uiFactory.createFontBox();
//					final Dimension preferredSize = fontBox.getPreferredSize();
//					preferredSize.width = 150;
//					fontBox.setPreferredSize(preferredSize);					
					JRibbonComponent fontComboWrapper = new JRibbonComponent((JComponent) fontBox);
					fontComboWrapper.setKeyTip("SF");
					fontBand.addFlowComponent(fontComboWrapper);
					
					final Container sizeBox = uiFactory.createSizeBox();
					JRibbonComponent sizeComboWrapper = new JRibbonComponent((JComponent) sizeBox);
					sizeComboWrapper.setKeyTip("SS");
					fontBand.addFlowComponent(sizeComboWrapper);
					
					JCommandButtonStrip styleStrip = new JCommandButtonStrip();
					
//					styleBoldButton.setActionRichTooltip(new RichTooltip(TextUtils.getRawText(action.getTooltipKey()), "makes the node text bold"));
//					styleBoldButton.setActionKeyTip("1");
					styleStrip.add(RibbonActionContributorFactory.createCommandButton("BoldAction"));
					styleStrip.add(RibbonActionContributorFactory.createCommandButton("ItalicAction"));
					styleStrip.add(RibbonActionContributorFactory.createCommandButton("NodeColorAction"));
					styleStrip.add(RibbonActionContributorFactory.createCommandButton("NodeBackgroundColorAction"));
					
					// JCommandToggleButton styleItalicButton = new
					// JCommandToggleButton("", new format_text_italic());
					// styleItalicButton.setActionRichTooltip(new
					// RichTooltip("Italic", "makes the node text italic"));
					// styleItalicButton.setActionKeyTip("2");
					// styleStrip.add(styleItalicButton);
					//
					// JCommandToggleButton styleUnderlineButton = new
					// JCommandToggleButton("", new format_text_underline());
					// // styleUnderlineButton.setActionRichTooltip(new
					// RichTooltip(resourceBundle.getString("FontUnderline.tooltip.textActionTitle"),
					// resourceBundle
					// //
					// .getString("FontUnderline.tooltip.textActionParagraph1")));
					// // styleUnderlineButton.setActionKeyTip("3");
					// styleStrip.add(styleUnderlineButton);
					//
					// JCommandToggleButton styleStrikeThroughButton = new
					// JCommandToggleButton("", new
					// format_text_strikethrough());
					// // styleStrikeThroughButton.setActionRichTooltip(new
					// RichTooltip(resourceBundle.getString("FontStrikethrough.tooltip.textActionTitle"),
					// resourceBundle
					// //
					// .getString("FontStrikethrough.tooltip.textActionParagraph1")));
					// // styleStrikeThroughButton.setActionKeyTip("4");
					// // styleStrip.add(styleStrikeThroughButton);

					fontBand.addFlowComponent(styleStrip);

					parent.addChild(fontBand);
				}
				finally {
					Thread.currentThread().setContextClassLoader(contextClassLoader);
				}

			}

			public void addChild(Object child) {
				// TODO Auto-generated method stub

			}
		};
	}
	/***********************************************************************************
	 * CONSTRUCTORS
	 **********************************************************************************/

	/***********************************************************************************
	 * METHODS
	 **********************************************************************************/

	/***********************************************************************************
	 * REQUIRED METHODS FOR INTERFACES
	 **********************************************************************************/
}
