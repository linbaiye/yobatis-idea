package org.nalby.yobatis.idea;


import com.intellij.openapi.vfs.VirtualFile;
import org.nalby.yobatis.core.exception.ResourceNotAvailableExeception;
import org.nalby.yobatis.core.structure.AbstractFolder;
import org.nalby.yobatis.core.structure.File;
import org.nalby.yobatis.core.structure.Folder;
import org.nalby.yobatis.core.util.FolderUtil;

import java.util.LinkedList;
import java.util.List;

public class IdeaFolder extends AbstractFolder {

    private VirtualFile virtualFile;

    public IdeaFolder(String parentPath, VirtualFile virtualFile) {
        super(FolderUtil.concatPath(parentPath, virtualFile.getName()), virtualFile.getName());
        this.virtualFile = virtualFile;
    }

    @Override
    protected List<Folder> doListFolders() {
        List<Folder> folders = new LinkedList<>();
        for (VirtualFile file : virtualFile.getChildren()) {
            java.io.File jFile = new java.io.File(file.getPath());
            if (jFile.isHidden() || jFile.isFile()) {
                continue;
            }
            if (file.isDirectory()) {
                folders.add(new IdeaFolder(path, file));
            }
        }
        return folders;
    }

    @Override
    protected List<File> doListFiles() {
        List<File> files = new LinkedList<>();
        for (VirtualFile file : virtualFile.getChildren()) {
            if (!file.isDirectory()) {
                files.add(new IdeaFile(this, file));
            }
        }
        return files;
    }

    @Override
    protected Folder doCreateFolder(String s) {
        try {
            String fullPath = FolderUtil.concatPath(virtualFile.getPath(), s);
            java.io.File file = new java.io.File(fullPath);
            if (file.exists() && file.isFile()) {
                throw new ResourceNotAvailableExeception("A file named " + fullPath + " exists.");
            }
            if (!file.exists() && !file.mkdir()) {
                throw new ResourceNotAvailableExeception("Failed to mkdir " + fullPath);
            }
            virtualFile.refresh(false, false);
            VirtualFile vf = virtualFile.findChild(s);
            return new IdeaFolder(path, vf);
        } catch (ResourceNotAvailableExeception e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceNotAvailableExeception(e);
        }
    }

    @Override
    protected File doCreateFile(String s) {
        try {
            String fullPath = FolderUtil.concatPath(virtualFile.getPath(), s);
            java.io.File file = new java.io.File(fullPath);
            if (file.exists() && file.isDirectory()) {
                throw new ResourceNotAvailableExeception("A directory named " + fullPath + " exists.");
            }
            if (file.exists()) {
                file.delete();
            }
            if (!file.createNewFile()) {
                throw new ResourceNotAvailableExeception("Unable to create file " + file.getAbsolutePath());
            }
            virtualFile.refresh(false, false);
            VirtualFile vf = virtualFile.findChild(s);
            return new IdeaFile(this, vf);
        } catch (ResourceNotAvailableExeception e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceNotAvailableExeception(e);
        }
    }
}
