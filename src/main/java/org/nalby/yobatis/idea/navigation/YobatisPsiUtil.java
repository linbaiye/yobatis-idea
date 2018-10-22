package org.nalby.yobatis.idea.navigation;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.Nullable;
import org.nalby.yobatis.core.mybatis.NamingHelper;

import java.util.Collection;

public final class YobatisPsiUtil {

    private YobatisPsiUtil() {
        throw new IllegalStateException("Do not construct me");
    }


    @Nullable
    private static String findMapperNamespace(PsiJavaFile psiJavaFile) {
        if (psiJavaFile == null) {
            return null;
        }
        VirtualFile virtualFile = psiJavaFile.getVirtualFile();
        if (virtualFile == null || !virtualFile.getName().endsWith("Dao.java")) {
            return null;
        }
        return NamingHelper.glueMapperNamespace(psiJavaFile.getPackageName(), virtualFile.getName());
    }


    @Nullable
    private static String findMapperNamespace(XmlFile xmlFile) {
        if (xmlFile == null || xmlFile.getRootTag() == null || !"mapper".equals(xmlFile.getRootTag().getName())) {
            return null;
        }
        return xmlFile.getRootTag().getAttributeValue("namespace");
    }

    @Nullable
    public static PsiClass findCorrespondingJavaInterface(XmlFile xmlFile) {
        String xmlNamespace = findMapperNamespace(xmlFile);
        if (xmlNamespace == null) {
            return null;
        }
        String daoFileName = NamingHelper.getDaoFileName(xmlNamespace);
        String packageName = NamingHelper.getDaoPackageName(xmlNamespace);
        for (Module module : ModuleManager.getInstance(xmlFile.getProject()).getModules()) {
            Collection<VirtualFile> files = FilenameIndex.getVirtualFilesByName(xmlFile.getProject(), daoFileName,
                    GlobalSearchScope.moduleRuntimeScope(module, false));
            for (VirtualFile file : files) {
                PsiJavaFile javaFile = (PsiJavaFile) PsiManager.getInstance(xmlFile.getProject()).findFile(file);
                if (javaFile == null || javaFile.getClasses().length != 1 || !packageName.equals(javaFile.getPackageName())) {
                    continue;
                }
                return javaFile.getClasses()[0];
            }
        }
        return null;
    }

    @Nullable
    public static XmlFile findCorrespondingMapperFile(PsiJavaFile psiJavaFile) {
        if (psiJavaFile.getClasses().length != 1 || !psiJavaFile.getClasses()[0].isInterface()) {
            return null;
        }
        String mapperNamespace = findMapperNamespace(psiJavaFile);
        if (mapperNamespace == null) {
            return null;
        }
        String mapperFileName = NamingHelper.getMapperFileName(psiJavaFile.getName());
        for (Module module : ModuleManager.getInstance(psiJavaFile.getProject()).getModules()) {
            Collection<VirtualFile> files = FilenameIndex.getVirtualFilesByName(psiJavaFile.getProject(), mapperFileName,
                    GlobalSearchScope.moduleRuntimeScope(module, false));
            for (VirtualFile file : files) {
                XmlFile xmlFile = (XmlFile) PsiManager.getInstance(psiJavaFile.getProject()).findFile(file);
                String xmlNamespace = findMapperNamespace(xmlFile);
                if (mapperNamespace.equals(xmlNamespace)) {
                    return xmlFile;
                }
            }
        }
        return null;
    }
}

