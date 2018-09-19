package org.nalby.yobatis.idea;

import com.intellij.openapi.vfs.VirtualFile;
import org.nalby.yobatis.core.exception.ResourceNotAvailableExeception;
import org.nalby.yobatis.core.structure.File;
import org.nalby.yobatis.core.structure.Folder;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    private void doWrite(byte[] bytes) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path())) {
            fileOutputStream.write(bytes);
        }
        virtualFile.refresh(false, false);
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
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
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
        try {
            doWrite(s.getBytes());
        } catch (IOException e) {
            throw new ResourceNotAvailableExeception(e);
        }
    }

    @Override
    public Folder parentFolder() {
        return parent;
    }
}
