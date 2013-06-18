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
package net.sf.rej.gui.compare;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sf.rej.files.FileSet;
import net.sf.rej.files.SingleFile;
import net.sf.rej.gui.ProgressMonitor;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.util.IOToolkit;
import net.sf.rej.util.StopWatch;

public class CompareThread implements Runnable {

    // filesets to compare
    private FileSet filesetA;
    private FileSet filesetB;

    // list of files in each set
    private List<String> listA;
    private List<String> listB;

    private DefaultMutableTreeNode root;
    private JTree tree;

    public CompareThread(FileSet a1, FileSet a2, JTree tree, DefaultMutableTreeNode root) {
        this.filesetA = a1;
        this.filesetB = a2;
        this.tree = tree;
        this.root = root;
        this.listA = a1.getContentsList();
        this.listB = a2.getContentsList();
    }

    public void run() {
        try {
            this.root.removeAllChildren();
            
            if (this.filesetA instanceof SingleFile && this.filesetB instanceof SingleFile && !this.listA.equals(this.listB)) {
            	// Special case for comparison of two single files with nonequal names
            	FileItem fileItem = new FileItem(this.filesetA.getName(), this.filesetB.getName(), this.filesetA.getName(), this.filesetB.getName());
				DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(fileItem);
				this.root.add(fileNode);
				InputStream isA = this.filesetA.getInputStream(this.filesetA.getName());
				InputStream isB = this.filesetB.getInputStream(this.filesetB.getName());
				boolean eq = IOToolkit.areEqual(isA, isB);
				isA.close();
				isB.close();
				if (eq) {
					fileItem.setStyle(Style.PLAIN);
				} else {
					fileItem.setStyle(Style.RED_AND_YELLOW);
				}
				// expand the root
				this.tree.expandPath(this.tree.getPathForRow(0));
				
				return; // early return
            }
            StopWatch watch = new StopWatch();
			SystemFacade.getInstance().setStatus("Preparing comparison.");						
            
            final ProgressMonitor pm = SystemFacade.getInstance().getProgressMonitor();

			// get a list of all the packages in the fileset
			final Set<String> pkgsAll = new TreeSet<String>();
			Set<String> pkgsA = new TreeSet<String>();
			for (String contentFile : this.listA) {
				int index = contentFile.lastIndexOf('/');
				if (index == -1) {
					pkgsAll.add("");
					pkgsA.add("");
				} else {
					String pkg = contentFile.substring(0, index);
					pkgsAll.add(pkg);
					pkgsA.add(pkg);
				}
				
			}
			Set<String> pkgsB = new TreeSet<String>();
			for (String contentFile : this.listB) {
				int index = contentFile.lastIndexOf('/');
				if (index == -1) {
					pkgsAll.add("");
					pkgsB.add("");
				} else {
					String pkg = contentFile.substring(0, index);
					pkgsAll.add(pkg);
					pkgsB.add(pkg);
				}
				
			}
			
			// associate each package name with a tree node
			Map<String, DefaultMutableTreeNode> map = new HashMap<String, DefaultMutableTreeNode>(); 
			for (String pkg : pkgsAll) {
				PackageItem pkgItem = new PackageItem(pkg);
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(pkgItem);
				root.add(node);
				map.put(pkg, node);
				
				// style highlighting
				if (!pkgsA.contains(pkg)) {
					// only present in set B
					pkgItem.setStyle(Style.YELLOW);
				} else if (!pkgsB.contains(pkg)) {
					// only present in set A
					pkgItem.setStyle(Style.RED);
				} else {
					// present in both sets
					pkgItem.setStyle(Style.PLAIN);
				}
			}

			// sort
			Set<String> contentFileSet = new TreeSet<String>();
			contentFileSet.addAll(this.listA);
			contentFileSet.addAll(this.listB);
			
			// progress scope
			
			pm.setProgressScope(0, contentFileSet.size());
			int progress = 0;
			// add each file to the corresponding node
			for (String contentFile : contentFileSet) {
				SystemFacade.getInstance().setStatus("Comparing " + contentFile);						
				pm.setProgress(progress++);
				int index = contentFile.lastIndexOf('/');
				String pkg = "";
				if (index != -1) {
					pkg = contentFile.substring(0, index);
				}
				DefaultMutableTreeNode pkgNode = map.get(pkg);
				PackageItem pkgItem = (PackageItem) pkgNode.getUserObject();
				FileItem fileItem = null;
				if (index == -1) {
					fileItem = new FileItem(contentFile, contentFile);					
				} else {
					fileItem = new FileItem(contentFile.substring(pkg.length()+1), contentFile);
				}
				DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(fileItem);
				pkgNode.add(fileNode);
				
				// style highlighting
				if (!listA.contains(contentFile)) {
					// only in set B
					fileItem.setStyle(Style.YELLOW);
					if (pkgItem.getStyle() == Style.PLAIN) {
						pkgItem.setStyle(Style.RED_AND_YELLOW);
					}
				} else if (!listB.contains(contentFile)) {
					// only in set A
					fileItem.setStyle(Style.RED);
					if (pkgItem.getStyle() == Style.PLAIN) {
						pkgItem.setStyle(Style.RED_AND_YELLOW);
					}
				} else {
					// in both sets, compare (non-'java aware', simple binary compare)
					InputStream isA = this.filesetA.getInputStream(contentFile);
					InputStream isB = this.filesetB.getInputStream(contentFile);
					boolean eq = IOToolkit.areEqual(isA, isB);
					isA.close();
					isB.close();
					if (eq) {
						fileItem.setStyle(Style.PLAIN);
					} else {
						fileItem.setStyle(Style.RED_AND_YELLOW);
						pkgItem.setStyle(Style.RED_AND_YELLOW);
					}
				}
			}
			pm.setProgress(progress);

			// expand the root
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					tree.expandPath(tree.getPathForRow(0));				
					if (pkgsAll.size() == 1) {
						// only one package, expand it
						tree.expandPath(tree.getPathForRow(1));
					}
				}
			});
			
			SystemFacade.getInstance().setStatus("Compare done in " + watch.elapsedSeconds() + " seconds.");

        } catch (final Exception ee) {
        	SwingUtilities.invokeLater(new Runnable() {
        		public void run() {
        			SystemFacade.getInstance().handleException(ee);	
        		}
        	});
        }

    }

}
