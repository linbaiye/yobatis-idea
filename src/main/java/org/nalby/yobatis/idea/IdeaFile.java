package org.nalby.yobatis.idea;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.nalby.yobatis.core.exception.ResourceNotAvailableExeception;
import org.nalby.yobatis.core.structure.File;
import org.nalby.yobatis.core.structure.Folder;

import java.io.*;

public class IdeaFile implements File {

    private VirtualFile virtualFile;

    private Folder parent;

    public IdeaFile(Folder parent, VirtualFile virtualFile) {
        this.parent = parent;
        this.virtualFile = virtualFile;
    }

    @Override
    public String name() {
        return virtualFile.getName();
    }

    @Override
    public String path() {
        return virtualFile.getPath();
    }

    private void doWrite(byte[] bytes) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try (FileOutputStream fileOutputStream = new FileOutputStream(path())) {
                fileOutputStream.write(bytes);
                virtualFile.refresh(false, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public InputStream open() {
        try {
            return virtualFile.getInputStream();
        } catch (IOException e) {
            throw new ResourceNotAvailableExeception(e);
        }
    }

    @Override
    public void write(InputStream inputStream) {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream(10000)) {
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] byteArray = buffer.toByteArray();
            doWrite(byteArray);
        } catch (IOException e) {
            throw new ResourceNotAvailableExeception(e);
        }
    }

    @Override
    public void write(String s) {
        doWrite(s.getBytes());
    }

    @Override
    public Folder parentFolder() {
        return parent;
    }
}
