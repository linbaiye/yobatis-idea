package org.nalby.yobatis.idea.navigation;

import com.intellij.psi.HierarchicalMethodSignature;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;

import java.util.Set;

public class BaseAwareXmlMapperAnchorSeeker extends AbstractPsiElementAnchorSeeker<PsiIdentifier>  {

    private XmlAttribute xmlAttribute;

    private XmlFile xmlFile;

    public BaseAwareXmlMapperAnchorSeeker(XmlAttribute xmlAttribute,
                                          XmlFile xmlFile) {
        this.xmlAttribute = xmlAttribute;
        this.xmlFile = xmlFile;
    }

    private String toNameOfDao() {
        return xmlFile.getName().replaceAll("Mapper\\.xml", "Dao.java");
    }

    @Override
    public PsiIdentifier seek() {
        Set<PsiJavaFile> files = findJavaFiles(xmlAttribute.getProject(), toNameOfDao());
        for (PsiJavaFile file : files) {
            for (PsiMethod method : file.getClasses()[0].getMethods()) {
                if (method.getName().equals(xmlAttribute.getValue())) {
                    return method.getNameIdentifier();
                }
            }
        }
        return null;
    }
}
