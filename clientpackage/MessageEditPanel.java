package clientpackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.AbstractDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.MinimalHTMLWriter;

public final class MessageEditPanel extends JPanel 
                                      implements ActionListener {   
	private static final long serialVersionUID = 1L;
	private final ConnectionToServer fCts;
    private final JTextPane fEditPane;
    private final JToggleButton fBoldToggleButton;
    private final JToggleButton fItalicsToggleButton;
    private final JToggleButton fUnderLineToggleButton;
    private final JButton fSendButton;
    private final JComboBox<String> fFontTypeCombo;
    private final JComboBox<Integer> fFontSizeCombo;
    private final JButton fFontColorButton;
    private String fFont;
    private Style fCurrStyle;
    private String fChatWithName;
    private int fSize;
    private final ChatClient fClient;
    private final StyledDocument fStyleDoc;
    private final List<JComponent> fComponentList;
    
	public MessageEditPanel(ChatClient aClient, ConnectionToServer aCts ) {
		this.fCts = aCts;
		this.fClient = aClient;
		fComponentList = new ArrayList<JComponent>();
		fBoldToggleButton = makeToggleButton("B", "Bold");
		fItalicsToggleButton = makeToggleButton("I", "Italics");
		fUnderLineToggleButton = makeToggleButton("U", "Underline");
	    fFontColorButton = makeButton("FC", "SET_COLOR", "Choose Font Color");
	    //Get Font Types
	    final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    final String[] fFontNames = ge.getAvailableFontFamilyNames();
	    fFontTypeCombo = new JComboBox<String>(fFontNames);
	    final DefaultComboBoxModel<String> modelType = (DefaultComboBoxModel<String>)fFontTypeCombo.getModel();
	    fFont = "Arial";
	    final int indexTypeDefault = modelType.getIndexOf(fFont);
	    fFontTypeCombo.setSelectedIndex(indexTypeDefault);
	    fFontTypeCombo.addActionListener(this);
	    fFontTypeCombo.setActionCommand("FONT_NAME");
	    fFontTypeCombo.setEditable(false);
	    fFontTypeCombo.setFocusable(false);
	    fFontTypeCombo.setEnabled(false);
	    fComponentList.add(fFontTypeCombo);
	    fFontSizeCombo = new JComboBox<Integer>();
	    final DefaultComboBoxModel<Integer> modelSize = (DefaultComboBoxModel<Integer>)fFontSizeCombo.getModel();
	    //Set Font Sizes
	    for(int index = 12; index <= 32; index+=2) {
	    	modelSize.addElement(index);	    	
	    }
	    final int indexSizeDefault = modelSize.getIndexOf(12);
	    fSize = modelSize.getElementAt(indexSizeDefault);
	    fFontSizeCombo.setSelectedIndex(indexSizeDefault);
	    fFontSizeCombo.addActionListener(this);
	    fFontSizeCombo.setActionCommand("FONT_SIZE");
	    fFontSizeCombo.setEditable(false);
	    fFontSizeCombo.setFocusable(false);
	    fFontSizeCombo.setEnabled(false);
	    fComponentList.add(fFontSizeCombo);
		final JPanel buttonPanel = new JPanel();
	    buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	    buttonPanel.add(fFontTypeCombo);
	    buttonPanel.add(fFontSizeCombo);
	    buttonPanel.add(fFontColorButton);
	    buttonPanel.add(new JSeparator(JSeparator.VERTICAL));
	    buttonPanel.add(new JSeparator(JSeparator.VERTICAL));
	    buttonPanel.add(fBoldToggleButton);
	    buttonPanel.add(fItalicsToggleButton);
	    buttonPanel.add(fUnderLineToggleButton);		
		fEditPane = new JTextPane();
		fEditPane.setEditorKit(new WrapEditorKit());
		fEditPane.setPreferredSize(new Dimension(200, 100));
		fEditPane.setEditable(true);
		fEditPane.setEnabled(false);
		fComponentList.add(fEditPane);
		fStyleDoc = fEditPane.getStyledDocument();
		AbstractDocument abstractDoc = (AbstractDocument)fStyleDoc;
		abstractDoc.setDocumentFilter(new EditTextDocumentFilter());
		final JScrollPane scrollEditPane = new JScrollPane(fEditPane, 
				                                           JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				                                           JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollEditPane.setViewportView(fEditPane);
		fComponentList.add(fEditPane);
		fSendButton = makeButton("Send", "SEND_MESSAGE", "Send Message");
		final JPanel panePanel = new JPanel();
		panePanel.setLayout(new BorderLayout());
		panePanel.add(scrollEditPane, BorderLayout.CENTER);
		panePanel.add(fSendButton, BorderLayout.EAST);		
		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(buttonPanel, BorderLayout.NORTH);
		mainPanel.add(panePanel, BorderLayout.SOUTH);
		add(mainPanel);
		addStyles();
		setNewStyle();
		fStyleDoc.setLogicalStyle(0, fCurrStyle);
	}
	
		@Override
		public final void actionPerformed(ActionEvent ae) {
			final String command = ae.getActionCommand();
			if(command.equals("SEND_MESSAGE")) {
				try {final StringWriter writer = new StringWriter();
				    MinimalHTMLWriter htmlWriter = new MinimalHTMLWriter(writer, fStyleDoc);
					htmlWriter.write();
					String messageHtml = writer.getBuffer().toString();
					fClient.appendTextPane(messageHtml, true);
					messageHtml = messageHtml.replaceAll("(\\r|\\n)", "");
					fCts.sendMessage("MESSAGE " + fChatWithName + " " + messageHtml);
					fEditPane.setText("");
				} catch (IOException | BadLocationException e) {
					e.printStackTrace();
				}				
			} else if(command.equals("SET_COLOR")) {
				final JColorChooser colorChooser = new JColorChooser();
                final AbstractColorChooserPanel[] panels = colorChooser.getChooserPanels(); 
                for(AbstractColorChooserPanel panel : panels) {
                	if(!(panel.getDisplayName().equals("Swatches"))) {
                		colorChooser.removeChooserPanel(panel);
                	}
                }                
                final AbstractColorChooserPanel colorPanel = panels[0];
                final JPanel panel = (JPanel)colorPanel.getComponent(0);
                panel.remove(2);
                panel.remove(1);   
                colorChooser.setPreviewPanel(new JPanel());
                final JDialog colorDialog = JColorChooser.createDialog(null, "Font Color", true, colorChooser, new ActionListener() {
                    public final void actionPerformed(ActionEvent ae) {
                        final Color color = colorChooser.getColor();
                             if(color != null) {
              			    	StyleConstants.setForeground(fCurrStyle, color);
              			     }         
                         }    
                    } , null);			    
                colorDialog.setVisible(true);
			} else if(command.equals("FONT_NAME")) {
			   fFont = getComboBoxElement(fFontTypeCombo).toString();
			   setAttributes();   			   			   
		   } else if(command.equals("FONT_SIZE")) {
			   fSize = (Integer)getComboBoxElement(fFontSizeCombo);
			   setAttributes();   
		   }
		}
	
		private final Object getComboBoxElement(JComboBox aComboBox) {
			final DefaultComboBoxModel<Object> model = (DefaultComboBoxModel)aComboBox.getModel();
			   final int index =  aComboBox.getSelectedIndex();
		       return model.getElementAt(index);
		}
		
		private final void addStyles() {
		     final StyleContext sc = StyleContext.getDefaultStyleContext();
		     final Style defaultContextStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
		     
		     final Style normal = fStyleDoc.addStyle("normal", defaultContextStyle);
		     
		     final Style bold = fStyleDoc.addStyle("bold", normal);
		     StyleConstants.setBold(bold, true);
		     
		     final Style italic = fStyleDoc.addStyle("italic", normal);
		     StyleConstants.setItalic(italic, true);

		     final Style underline = fStyleDoc.addStyle("underline", normal);
		     StyleConstants.setUnderline(underline, true);
		     
		     final Style boldItalic = fStyleDoc.addStyle("boldItalic", null);
		     StyleConstants.setBold(boldItalic, true);
		     StyleConstants.setItalic(boldItalic, true);
		     
		     final Style boldUnderline = fStyleDoc.addStyle("boldUnderline", null);
		     StyleConstants.setBold(boldUnderline, true);
		     StyleConstants.setItalic(boldUnderline, true);
		     
		     final Style italicUnderline = fStyleDoc.addStyle("italicUnderline", null);
		     StyleConstants.setBold(italicUnderline, true);
		     StyleConstants.setItalic(italicUnderline, true);
		     
		     final Style boldItalicUnderline = fStyleDoc.addStyle("boldItalicUnderline", null);
		     StyleConstants.setBold(boldItalicUnderline, true);
		     StyleConstants.setItalic(boldItalicUnderline, true);
		     StyleConstants.setUnderline(boldItalicUnderline, true);
		}
		
		private final void setNewStyle() {
			String styleName = "";
        	final boolean boldPressed = fBoldToggleButton.isSelected();
        	final boolean italicsPressed = fItalicsToggleButton.isSelected();
        	final boolean underLinePressed = fUnderLineToggleButton.isSelected();
        	if((boldPressed) && (italicsPressed) && (underLinePressed)) {
        		styleName = "boldItalicUnderline"; 
        	} else if ((boldPressed) && (italicsPressed)) {
        		styleName = "boldItalic";
        	} else if ((boldPressed) && (underLinePressed)) {
        		styleName = "boldUnderline";
        	} else if((italicsPressed) & (underLinePressed)) {
        		styleName = "italicUnderline";
            }else if(boldPressed) {
            	styleName = "bold";
        	} else if(italicsPressed) {
        		styleName = "italic";
        	} else if(underLinePressed) {
        		styleName = "underline";
        	} else {
        		styleName = "normal";
        	}
		    fCurrStyle = fStyleDoc.getStyle(styleName);
		    StyleConstants.setFontFamily(fCurrStyle, fFont);
		    StyleConstants.setFontSize(fCurrStyle, fSize);	
		}
		
		private final JButton makeButton(String label, String actionCommand, String toolTipText)    {			
	        final JButton b = new JButton(label);
	        final Border border = BorderFactory.createRaisedSoftBevelBorder();
	        b.setBorder(border);
	        b.setFocusPainted(false);
	        b.setFocusable(false);
	        b.setActionCommand(actionCommand);
	        b.addActionListener(this);
	        b.setToolTipText(toolTipText);
	        b.setEnabled(false);
	        fComponentList.add(b);
	        return b;
	   }
		
		private final JToggleButton makeToggleButton(String aLabel, String aToolTipText)  {			
	        final JToggleButton t = new JToggleButton(aLabel);
	        final Border border = BorderFactory.createRaisedSoftBevelBorder();
	        t.setBorder(border);
	        t.setFocusPainted(false);
	        t.setFocusable(false);
	        t.setToolTipText(aToolTipText);
	        t.addActionListener(new ActionListener() {
	        	@Override
		        public final void actionPerformed(ActionEvent ae) {
		            	setAttributes();
		        }
	        });
	        t.setEnabled(false);
	        fComponentList.add(t);
	        return t;
	   }
		
		private final void setAttributes() {
			setNewStyle();	
			final int start = fEditPane.getSelectionStart();
        	final int end = fEditPane.getSelectionEnd();
        	final int selection = (end - start);
        	if(selection > 0) {
        		fStyleDoc.setCharacterAttributes(start, selection, fCurrStyle, true);	
        	} else {
        		final int pos = fEditPane.getCaretPosition();
        		fStyleDoc.setLogicalStyle(pos, fCurrStyle);
        	}
		}
		
		public final void enableComponents(boolean aEnable) {
			for(JComponent component : fComponentList) {
				component.setEnabled(aEnable);
			}
			fChatWithName = fClient.getChatWithName();
		}
		
		final class EditTextDocumentFilter extends DocumentFilter {
			@Override
			public final void insertString(FilterBypass fb, int offset,
					                       String string, AttributeSet attr) throws BadLocationException {
				final int start = fEditPane.getSelectionStart();
	        	final int end = fEditPane.getSelectionEnd();
				
				if(start != end) {
					fBoldToggleButton.setSelected(false);
					fItalicsToggleButton.setSelected(false);
					fUnderLineToggleButton.setSelected(false);
					fFontSizeCombo.setSelectedIndex(-1);
				} else {
					fBoldToggleButton.setSelected(StyleConstants.isBold(attr));
				    fItalicsToggleButton.setSelected(StyleConstants.isItalic(attr));
				    fUnderLineToggleButton.setSelected(StyleConstants.isUnderline(attr));
				    fFont = StyleConstants.getFontFamily(attr);
				    fFontTypeCombo.setSelectedItem(fFont);
				    fSize = StyleConstants.getFontSize(attr);
				    fFontSizeCombo.setSelectedItem(fSize);
				    setNewStyle();
				}				
				super.insertString(fb, offset, string, fCurrStyle);
			}	
			
			@Override
			public final void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) 
			                                                          throws BadLocationException{
				super.replace(fb, offset, length, string, fCurrStyle);
			}
		}
		
		final class WrapEditorKit extends StyledEditorKit {
			private static final long serialVersionUID = 1L;
			final ViewFactory defaultFactory =  new WrapColumnFactory();
			public final ViewFactory getViewFactory() {
				return defaultFactory;
			}
		}
		
		final class WrapColumnFactory implements ViewFactory {
			public View create(Element elem) {
				final String kind = elem.getName();
				if(kind != null) {
					if (kind.equals(AbstractDocument.ContentElementName)) {
	                    return new WrapLabelView(elem);
	                } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
	                    return new ParagraphView(elem);
	                } else if (kind.equals(AbstractDocument.SectionElementName)) {
	                    return new BoxView(elem, View.Y_AXIS);
	                } else if (kind.equals(StyleConstants.ComponentElementName)) {
	                    return new ComponentView(elem);
	                } else if (kind.equals(StyleConstants.IconElementName)) {
	                    return new IconView(elem);
	                }					
				}
				return new LabelView(elem);
			}
		}
		
		final class WrapLabelView extends LabelView {
	        public WrapLabelView(Element elem) {
	            super(elem);
	        }
		
		public final  float getMinimumSpan(int axis) {
            switch (axis) {
                case View.X_AXIS:
                    return 0;
                case View.Y_AXIS:
                    return super.getMinimumSpan(axis);
                default:
                    throw new IllegalArgumentException("Invalid axis: " + axis);
            }
         }
		}
}
