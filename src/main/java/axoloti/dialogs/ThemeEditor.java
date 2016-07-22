package axoloti.dialogs;

import axoloti.Theme;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class ThemeEditor extends JFrame {

    private Theme theme;
    private JPanel p;

    public ThemeEditor() {
        this.setPreferredSize(new Dimension(1000, 1000));
        theme = Theme.getCurrentTheme();
        p = new JPanel();
        JScrollPane s = new JScrollPane(p,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        p.setLayout(
                new GridLayout(theme.getClass().getFields().length + 8, 2)
        );
        final JButton load = new JButton("Load");
        load.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                theme.load(ThemeEditor.this);
                theme = Theme.getCurrentTheme();
                update();
            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {

            }
        });
        final JButton save = new JButton("Save");
        save.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                theme.save(ThemeEditor.this);
            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {

            }
        });

        final JButton revertToDefault = new JButton("Load Default");
        revertToDefault.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Theme.loadDefaultTheme();
                theme = Theme.getCurrentTheme();
                update();
            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {

            }
        });

        p.add(load);
        p.add(save);
        p.add(revertToDefault);
        p.add(new JPanel());
        p.add(new JLabel("Note: reload patch to see changes."));
        p.add(new JPanel());
        p.add(new JPanel());
        p.add(new JPanel());

        for (final Field f : theme.getClass().getFields()) {
            p.add(new JLabel(f.getName().replace("_", " ")));
            try {
                if (f.getName() == "Theme_Name") {
                    final JTextArea textArea = new JTextArea((String) f.get(theme));
                    p.add(textArea);
                    textArea.getDocument().addDocumentListener(
                            new DocumentListener() {
                        @Override
                        public void removeUpdate(DocumentEvent e) {
                            updateThemeName(e);
                        }

                        @Override
                        public void insertUpdate(DocumentEvent e) {
                            updateThemeName(e);
                        }

                        @Override
                        public void changedUpdate(DocumentEvent e) {
                            updateThemeName(e);
                        }
                    });
                } else {
                    final JButton t = new JButton();
                    t.setBorder(BorderFactory.createLineBorder(getBackground(), 2));
                    final Color currentColor = (Color) f.get(theme);
                    t.setBackground(currentColor);
                    t.setContentAreaFilled(false);
                    t.setOpaque(true);
                    t.addMouseListener(new MouseListener() {
                        public void mouseClicked(MouseEvent e) {
                            try {
                                Color newColor = pickColor(e.getComponent().getBackground());
                                if (newColor != null) {
                                    f.set(theme, newColor);
                                    e.getComponent().setBackground(newColor);
                                    e.getComponent().repaint();
                                }

                            } catch (IllegalAccessException ex) {
                                Logger.getLogger(ThemeEditor.class.getName()).log(Level.SEVERE, "{0}", new Object[]{ex});
                            }
                        }

                        public void mousePressed(MouseEvent e) {

                        }

                        public void mouseEntered(MouseEvent e) {

                        }

                        public void mouseExited(MouseEvent e) {

                        }

                        public void mouseReleased(MouseEvent e) {

                        }
                    });
                    p.add(t);
                }
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ThemeEditor.class.getName()).log(Level.SEVERE, "{0}", new Object[]{ex});
            }
        }
        this.setContentPane(s);
        this.pack();
    }

    private void update() {
        int i = 9;
        for (final Field f : theme.getClass().getFields()) {
            Component target = p.getComponent(i);
            try {
                try {
                    Color color = (Color) f.get(theme);
                    target.setBackground(color);
                } catch (ClassCastException e) {
                    String themeName = (String) f.get(theme);
                    ((JTextArea) target).setText(themeName);
                }
            } catch (IllegalAccessException e) {
                Logger.getLogger(ThemeEditor.class.getName()).log(Level.SEVERE, "{0}", new Object[]{e});
            }
            target.repaint();
            i += 2;
        }
    }

    private void updateThemeName(DocumentEvent e) {
        try {
            theme.Theme_Name = e.getDocument().getText(0, e.getDocument().getLength());
        } catch (BadLocationException ex) {
            Logger.getLogger(ThemeEditor.class.getName()).log(Level.SEVERE, "{0}", new Object[]{e});
        }
    }

    private Color pickColor(Color initial) {
        return JColorChooser.showDialog(
                this,
                "Choose Color",
                initial);
    }
}
