package org.nalby.yobatis.idea.navigation;

import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;

import java.util.Set;

public class NonbaseXmlTagAnchorSeeker extends AbstractPsiElementAnchorSeeker<PsiIdentifier>  {

    private XmlAttribute xmlAttribute;

    private XmlFile xmlFile;

    NonbaseXmlTagAnchorSeeker(XmlAttribute xmlAttribute,
                                     XmlFile xmlFile) {
        this.xmlAttribute = xmlAttribute;
        this.xmlFile = xmlFile;
    }

    private String toNameOfDaoImpl() {
        return xmlFile.getName().replaceAll("Mapper\\.xml", "DaoImpl.java");
    }

    @Override
    public PsiIdentifier seek() {
        Set<PsiJavaFile> javaFiles = findJavaFiles(xmlAttribute.getProject(), toNameOfDaoImpl());
        for (PsiJavaFile e: javaFiles) {
            for (PsiMethod method : e.getClasses()[0].getAllMethods()) {
                if (method.getBody() == null) {
                    continue;
                }
                String anchor = findAnchorNameInMethodImpl(method);
                if (anchor == null || !anchor.equals(xmlAttribute.getValue())) {
                    continue;
                }
                if (method.findSuperMethods().length == 1) {
                    return method.findSuperMethods()[0].getNameIdentifier();
                }
            }
        }
        return null;
    }
}
