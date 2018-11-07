package org.nalby.yobatis.idea;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.PluginPathManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.nalby.yobatis.core.structure.Folder;

import javax.print.attribute.standard.MediaSize;
import java.io.File;

public class IdeaProject extends org.nalby.yobatis.core.structure.Project {

    public static final String NAME = "yobatis";

    private IdeaProject(Folder root) {
        super(root);
    }

    @Override
    public String getAbsPathOfSqlConnector() {
        PluginId pluginId = PluginId.getId(NAME);
        IdeaPluginDescriptor pluginDescriptor = PluginManager.getPlugin(pluginId);
        if (pluginDescriptor == null) {
            return null;
        }
        String basePath = pluginDescriptor.getPath().getPath();
        return basePath + File.separatorChar + "lib" + File.separatorChar + "mysql-connector-java-5.1.25.jar";
    }

    public static IdeaProject wrap(Project project) {
        VirtualFile virtualFile = project.getBaseDir();
        Folder folder = new IdeaFolder("/", virtualFile);
        return new IdeaProject(folder);
    }
}
