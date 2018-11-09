package org.nalby.yobatis.idea.navigation;

import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

import java.util.Set;

public class JavaInterfaceAnchorSeeker extends AbstractPsiElementAnchorSeeker<XmlTag>  {

    private PsiJavaFile psiJavaFile;

    public JavaInterfaceAnchorSeeker(PsiJavaFile psiJavaFile) {
        this.psiJavaFile = psiJavaFile;
    }

    @Override
    public XmlTag seek() {
        Set<XmlFile> xmlFileSet = findXmlFiles(psiJavaFile.getProject(), toNameOfXmlMapper(psiJavaFile.getName()));
        if (xmlFileSet.size() != 1) {
            return null;
        }
        for (XmlFile file : xmlFileSet) {
            return file.getRootTag();
        }
        return null;
    }
}
