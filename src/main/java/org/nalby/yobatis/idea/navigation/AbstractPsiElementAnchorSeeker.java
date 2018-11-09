package org.nalby.yobatis.idea.navigation;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class AbstractPsiElementAnchorSeeker<T> {

    private final static Pattern PATTERN = Pattern.compile(".+NAMESPACE\\+\"([^\"]+).+");

    private Set<PsiFile> findFiles(Project project, String name) {
        Set<PsiFile> ret = new HashSet<>();
        for (Module module: ModuleManager.getInstance(project).getModules()) {
            PsiFile[] files = FilenameIndex.getFilesByName(project, name,
                    GlobalSearchScope.moduleRuntimeScope(module, false));
            if (files.length != 0) {
                ret.addAll(Arrays.asList(files));
            }
        }
        return ret;
    }

    protected Set<PsiJavaFile> findJavaFiles(Project project, String name) {
        Set<PsiFile> ret = findFiles(project, name);
        return ret.stream().filter(e -> e instanceof PsiJavaFile).map(e -> (PsiJavaFile)e).collect(Collectors.toSet());
    }

    protected Set<XmlFile> findXmlFiles(Project project, String name) {
        Set<PsiFile> ret = findFiles(project, name);
        return ret.stream().filter(e -> e instanceof XmlFile).map(e -> (XmlFile)e).collect(Collectors.toSet());
    }

    protected XmlTag findTagInFilesByIdAttribute(Set<XmlFile> files, String attr) {
        for (XmlFile file: files) {
            if (null == file.getRootTag()) {
                continue;
            }
            XmlTag xmlTag = file.getRootTag();
            for (XmlTag tag : xmlTag.getSubTags()) {
                String id = tag.getAttributeValue("id");
                if (attr.equals(id)) {
                    return tag;
                }
            }
        }
        return null;
    }


    protected String toNameOfDaoImpl(String daoName) {
        return daoName.replaceAll("Dao\\.java", "DaoImpl.java");
    }

    protected String toNameOfXmlMapper(String daoName) {
        return daoName.replaceAll("Dao\\.java", "Mapper.xml");
    }

    protected String findAnchorNameInMethodImpl(PsiMethod method) {
        if (method.getBody() == null) {
            return null;
        }
        for (PsiStatement psiStatement: method.getBody().getStatements()) {
            String line = psiStatement.getText().replaceAll("\\s+", "");
            if (line.startsWith("//") || !line.contains("(NAMESPACE+")) {
                continue;
            }
            Matcher matcher = PATTERN.matcher(line);
            if (!matcher.find()) {
                continue;
            }
            return matcher.group(1);
        }
        return null;
    }

    public abstract T seek();
}
