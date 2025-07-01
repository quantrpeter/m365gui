package org.hkprog.m365gui;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.Filter;
import java.io.Serializable;

@Plugin(name = "LogEditorPaneAppender", category = "Core", elementType = Appender.ELEMENT_TYPE, printObject = true)
public class LogEditorPaneAppender extends AbstractAppender {
    private static JEditorPane editorPane;

    protected LogEditorPaneAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout, true);
    }

    public static void setEditorPane(JEditorPane pane) {
        editorPane = pane;
    }

    @Override
    public void append(LogEvent event) {
        if (editorPane == null) return;
        final String message = new String(getLayout().toByteArray(event));
        SwingUtilities.invokeLater(() -> {
            String current = editorPane.getText();
            editorPane.setText(current + message);
            editorPane.setCaretPosition(editorPane.getDocument().getLength());
        });
    }

    @PluginFactory
    public static LogEditorPaneAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") Filter filter) {
        if (name == null) {
            LOGGER.error("No name provided for LogEditorPaneAppender");
            return null;
        }
        if (layout == null) {
            layout = org.apache.logging.log4j.core.layout.PatternLayout.createDefaultLayout();
        }
        return new LogEditorPaneAppender(name, filter, layout);
    }
}
