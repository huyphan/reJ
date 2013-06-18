/* Copyright (C) 2004-2007 Sami Koivu
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.rej.gui.tab;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.sf.rej.gui.DefaultMatcher;
import net.sf.rej.gui.Link;
import net.sf.rej.gui.SystemFacade;

/**
 * Tab for doing java intelligent searches inside the classes of the project.
 *
 * @author Sami Koivu
 */
public class SearchTab extends JPanel implements Tabbable {
    JPanel freeTextPanel = new JPanel();
    JLabel jLabel1 = new JLabel();
    JTextField freeTextField = new JTextField();
    JButton freeTextButton = new JButton();
    JPanel resultPanel = new JPanel();
    JScrollPane jScrollPane1 = new JScrollPane();
    DefaultListModel model = new DefaultListModel();
    JList resultList = new JList(this.model);
    JRadioButton jRadioButton1 = new JRadioButton();
    JRadioButton jRadioButton2 = new JRadioButton();
    JRadioButton jRadioButton3 = new JRadioButton();
    ButtonGroup typeGroup = new ButtonGroup();

    public SearchTab() {
        try {
            this.setLayout(new GridBagLayout());
            this.freeTextPanel.setLayout(new GridBagLayout());
            this.setBackground(SystemColor.control);
            this.setBorder(BorderFactory.createEtchedBorder());
            this.jLabel1.setText("Search");
            this.freeTextField.setText("");
            this.freeTextField.setColumns(20);
            this.freeTextField.addKeyListener(new KeyAdapter() {
            	@Override
            	public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        executeSearch();
                    }
            	}
            });
            this.freeTextButton.setText("Search");
            this.freeTextButton.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
                    executeSearch();
            	}
            });
            this.resultPanel.setLayout(new BorderLayout());
            this.resultList.addMouseListener(new MouseAdapter() {
            	@Override
            	public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        Link link = (Link) resultList.getSelectedValue();
                        if (link != null) {
                            SystemFacade.getInstance().goTo(link);
                        }
                    }
            	}
            });
            this.jRadioButton1.setActionCommand("cs");
            this.jRadioButton1.setSelected(true);
            this.jRadioButton1.setText("Case-sensitive");
            this.jRadioButton2.setActionCommand("ci");
            this.jRadioButton2.setText("Case-insensitive");
            this.jRadioButton3.setActionCommand("re");
            this.jRadioButton3.setText("RegExp");
            this.add(this.freeTextPanel, new GridBagConstraints(0, 0, 1, 1,
                    1.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 30));
            this.freeTextPanel.add(this.freeTextButton, new GridBagConstraints(
                    1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            this.freeTextPanel.add(this.jLabel1, new GridBagConstraints(0, 0,
                    2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            this.freeTextPanel.add(this.freeTextField, new GridBagConstraints(
                    0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            this.freeTextPanel.add(this.jRadioButton1, new GridBagConstraints(
                    0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            this.freeTextPanel.add(this.jRadioButton2, new GridBagConstraints(
                    0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            this.freeTextPanel.add(this.jRadioButton3, new GridBagConstraints(
                    0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            this.add(this.resultPanel, new GridBagConstraints(0, 1, 1, 1, 1.0,
                    2.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            this.resultPanel.add(this.jScrollPane1, BorderLayout.CENTER);
            this.jScrollPane1.getViewport().add(this.resultList, null);
            this.typeGroup.add(this.jRadioButton3);
            this.typeGroup.add(this.jRadioButton2);
            this.typeGroup.add(this.jRadioButton1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void redo() {
    }

    public void undo() {
    }

    public void insert() {
    }

    public void remove() {
    }

    public void goTo(Link link) {
    }

    public void find() {
    }

    public void findNext() {
    }

    public void clear() {
        this.model.clear();
    }

    public void addResult(final Link link) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SearchTab.this.model.addElement(link);
            }
        });
    }

	private void executeSearch() {
        String cmd = this.typeGroup.getSelection().getActionCommand();
        this.model.clear();
        DefaultMatcher matcher = null;
        final String searchString = this.freeTextField.getText();

        if (cmd.equals("cs")) {
            matcher = new DefaultMatcher() {
                @Override
				public boolean matches(String match) {
                    return match.indexOf(searchString) != -1;
                }

                @Override
				public void addLink(final Link link) {
                	SwingUtilities.invokeLater(new Runnable() {
                		public void run() {
                            SearchTab.this.model.addElement(link);
                		}
                	});
                }
            };
        } else if (cmd.equals("ci")) {
            matcher = new DefaultMatcher() {
                @Override
				public boolean matches(String match) {
                    return match.toLowerCase().indexOf(
                            searchString.toLowerCase()) != -1;
                }

                @Override
				public void addLink(final Link link) {
                	SwingUtilities.invokeLater(new Runnable() {
                		public void run() {
                            SearchTab.this.model.addElement(link);
                		}
                	});
                }
            };
        } else if (cmd.equals("re")) {
            final Pattern pattern = Pattern.compile(searchString);

            matcher = new DefaultMatcher() {
                @Override
				public boolean matches(String match) {
                    java.util.regex.Matcher matcher = pattern.matcher(match);
                    return matcher.find();
                }

                @Override
				public void addLink(final Link link) {
                	SwingUtilities.invokeLater(new Runnable() {
                		public void run() {
                            SearchTab.this.model.addElement(link);
                		}
                	});
                }
            };
        } else {
        	throw new AssertionError("Invalid search type option.");
        }
        matcher.setProgressMonitor(SystemFacade.getInstance()
                .getProgressMonitor());
        SystemFacade.getInstance().search(matcher);
    }

	public void outline() {
	}

	public void leavingTab() {
		// TODO Auto-generated method stub
	}

	public String getTabTitle() {
		return "Search";
	}

	public void enteringTab() {
	}

}